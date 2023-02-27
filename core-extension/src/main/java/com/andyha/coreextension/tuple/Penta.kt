package com.andyha.coreextension.tuple

data class Penta<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val quad: D,
    val five: E
) : Serializable {
    override fun toString(): String = "Quadruple data is ($first, $second, $third, $quad, $five)"
}

fun <T> Penta<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, quad, five)
