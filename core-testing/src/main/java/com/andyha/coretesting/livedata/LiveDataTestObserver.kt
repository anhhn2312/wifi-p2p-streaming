package com.andyha.coretesting.livedata

import androidx.arch.core.util.Function
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class LiveDataTestObserver<T> private constructor() : Observer<T?> {
    private val valueHistory: MutableList<T?> = ArrayList()
    private val onChanged: MutableList<Consumer<T?>> = ArrayList()
    private var valueLatch = CountDownLatch(1)
    override fun onChanged(value: T?) {
        valueHistory.add(value)
        valueLatch.countDown()
        for (consumer in onChanged) {
            consumer.accept(value)
        }
    }

    /**
     * Returns a last received value. Fails if no value was received yet.
     *
     * @return a last received value
     */
    private fun value(): T? {
        assertHasValue()
        return valueHistory[valueHistory.size - 1]
    }

    /**
     * Returns a unmodifiable list of received values.
     *
     * @return a list of received values
     */
    fun getHistory(): List<T> {
        return Collections.unmodifiableList(valueHistory) as List<T>
    }

    /**
     * Assert that this TestObserver received at least one value.
     *
     * @return this
     */
    fun assertHasValue(): LiveDataTestObserver<T> {
        if (valueHistory.isEmpty()) {
            throw fail("Observer never received any value")
        }
        return this
    }

    /**
     * Assert that this TestObserver never received any value.
     *
     * @return this
     */
    fun assertNoValue(): LiveDataTestObserver<T> {
        if (!valueHistory.isEmpty()) {
            throw fail("Expected no value, but received: " + value())
        }
        return this
    }

    /**
     * Assert that this TestObserver last received value was null
     *
     * @return this
     */
    fun assertNullValue(): LiveDataTestObserver<T> {
        val value = value()
        if (value != null) {
            throw fail("Value " + valueAndClass(value) + " is not null")
        }
        return this
    }

    /**
     * Assert that this TestObserver received the specified number of values.
     *
     * @param expectedSize the expected number of received values
     * @return this
     */
    fun assertHistorySize(expectedSize: Int): LiveDataTestObserver<T> {
        val size = valueHistory.size
        if (size != expectedSize) {
            throw fail("History size differ; Expected: $expectedSize, Actual: $size")
        }
        return this
    }

    /**
     * Assert that this TestObserver last received value is equal to
     * the given value.
     *
     * @param expected the value to expect being equal to last value, can be null
     * @return this
     */
    fun assertValue(expected: T): LiveDataTestObserver<T> {
        val value = value()
        if (notEquals(value, expected)) {
            throw fail("Expected: " + valueAndClass(expected) + ", Actual: " + valueAndClass(value))
        }
        return this
    }

    /**
     * Asserts that for this TestObserver last received value
     * the provided predicate returns true.
     *
     * @param valuePredicate the predicate that receives the observed value
     * and should return true for the expected value.
     * @return this
     */
    fun assertValue(valuePredicate: Function<T?, Boolean?>): LiveDataTestObserver<T> {
        val value = value()
        if (!valuePredicate.apply(value)!!) {
            throw fail(
                "Value " + valueAndClass(value) + " does not match the predicate "
                        + valuePredicate.toString() + "."
            )
        }
        return this
    }

    /**
     * Asserts that the TestObserver received only the specified values in the specified order.
     *
     * @param values the values expected
     * @return this
     */
    fun assertGetHistory(vararg values: T): LiveDataTestObserver<T> {
        val valueHistory = getHistory()
        val size = valueHistory.size
        if (size != values.size) {
            throw fail(
                "Value count differs; expected: " + values.size + " " + Arrays.toString(values)
                        + " but was: " + size + " " + this.valueHistory
            )
        }
        for (valueIndex in 0 until size) {
            val historyItem = valueHistory[valueIndex]
            val expectedItem = values[valueIndex]
            if (notEquals(expectedItem, historyItem)) {
                throw fail(
                    "Values at position " + valueIndex + " differ; expected: " + valueAndClass(
                        expectedItem
                    ) + " but was: " + valueAndClass(historyItem)
                )
            }
        }
        return this
    }

    /**
     * Asserts that this TestObserver did not receive any value for which
     * the provided predicate returns true.
     *
     * @param valuePredicate the predicate that receives the observed values
     * and should return true for the value not supposed to be received.
     * @return this
     */
    fun assertNever(valuePredicate: Function<T, Boolean>): LiveDataTestObserver<T> {
        val size = valueHistory.size
        for (valueIndex in 0 until size) {
            val value: T? = valueHistory[valueIndex]
            if (valuePredicate.apply(value)) {
                throw fail(
                    "Value at position " + valueIndex + " matches predicate "
                            + valuePredicate.toString() + ", which was not expected."
                )
            }
        }
        return this
    }

    /**
     * Allows assertion of some mapped value extracted from originally observed values.
     * History of observed values is retained.
     *
     *
     * This can became useful when you want to perform assertions on some complex structure and
     * you want to assert only on one field.
     *
     * @param mapper Function to map originally observed value.
     * @param <N>    Type of mapper.
     * @return TestObserver for mapped value
    </N> */
    fun <N> map(mapper: Function<T?, N>): LiveDataTestObserver<N> {
        val newObserver: LiveDataTestObserver<N> = create()
        // We want the history match the current one
        for (value in valueHistory) {
            newObserver.onChanged(mapper.apply(value))
        }
        doOnChanged(Map(newObserver, mapper))
        return newObserver
    }

    /**
     * Adds a Consumer which will be triggered on each value change to allow assertion on the value.
     *
     * @param onChanged Consumer to call when new value is received
     * @return this
     */
    fun doOnChanged(onChanged: Consumer<T?>): LiveDataTestObserver<T> {
        this.onChanged.add(onChanged)
        return this
    }

    /**
     * Awaits until this TestObserver has any value.
     *
     *
     * If this TestObserver has already value then this method returns immediately.
     *
     * @return this
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun awaitValue(): LiveDataTestObserver<T> {
        valueLatch.await()
        return this
    }

    /**
     * Awaits the specified amount of time or until this TestObserver has any value.
     *
     *
     * If this TestObserver has already value then this method returns immediately.
     *
     * @return this
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun awaitValue(timeout: Long, timeUnit: TimeUnit?): LiveDataTestObserver<T> {
        valueLatch.await(timeout, timeUnit)
        return this
    }

    /**
     * Awaits until this TestObserver receives next value.
     *
     *
     * If this TestObserver has already value then it awaits for another one.
     *
     * @return this
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun awaitNextValue(): LiveDataTestObserver<T> {
        return withNewLatch().awaitValue()
    }

    /**
     * Awaits the specified amount of time or until this TestObserver receives next value.
     *
     *
     * If this TestObserver has already value then it awaits for another one.
     *
     * @return this
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun awaitNextValue(timeout: Long, timeUnit: TimeUnit?): LiveDataTestObserver<T> {
        return withNewLatch().awaitValue(timeout, timeUnit)
    }

    private fun withNewLatch(): LiveDataTestObserver<T> {
        valueLatch = CountDownLatch(1)
        return this
    }

    private fun fail(message: String): AssertionError {
        return AssertionError(message)
    }

    internal class Map<T, N>(
        private val newObserver: LiveDataTestObserver<N>,
        private val mapper: Function<T, N>
    ) : Consumer<T> {
        override fun accept(value: T) {
            newObserver.onChanged(mapper.apply(value))
        }
    }

    companion object {
        private fun notEquals(o1: Any?, o2: Any?): Boolean {
            return o1 != o2 && (o1 == null || o1 != o2)
        }

        private fun valueAndClass(value: Any?): String {
            return if (value != null) {
                value.toString() + " (class: " + value.javaClass.simpleName + ")"
            } else "null"
        }

        fun <T> create(): LiveDataTestObserver<T> {
            return LiveDataTestObserver()
        }

        fun <T> test(liveData: LiveData<T>): LiveDataTestObserver<T> {
            val observer = LiveDataTestObserver<T>()
            liveData.observeForever(observer)
            return observer
        }
    }
}