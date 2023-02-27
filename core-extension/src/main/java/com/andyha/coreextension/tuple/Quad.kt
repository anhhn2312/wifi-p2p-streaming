package com.andyha.coreextension.tuple

internal typealias Serializable = java.io.Serializable

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val quad: D
) : Serializable {
    override fun toString(): String = "Quadruple data is ($first, $second, $third, $quad)"
}

fun <T> Quad<T, T, T, T>.toList(): List<T> = listOf(first, second, third, quad)
