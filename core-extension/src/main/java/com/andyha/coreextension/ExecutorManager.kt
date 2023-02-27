package com.andyha.coreextension

import android.os.Process
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.*


object ExecutorManager {

    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    private val workerThread: Executor by lazy {
        val backgroundPriorityThreadFactory: ThreadFactory =
            PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)
        return@lazy ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            backgroundPriorityThreadFactory
        )
    }

    fun io(): Scheduler {
        return Schedulers.from(workerThread)
    }
}

class PriorityThreadFactory(private val mThreadPriority: Int) : ThreadFactory {
    override fun newThread(runnable: Runnable): Thread {
        val wrapperRunnable = Runnable {
            try {
                Process.setThreadPriority(mThreadPriority)
            } catch (t: Throwable) {
            }
            runnable.run()
        }
        return Thread(wrapperRunnable)
    }
}