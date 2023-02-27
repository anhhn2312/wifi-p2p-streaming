package com.andyha.coredata.base

interface BaseUseCase<in Input, out Output> {
    operator fun invoke(params: Input): Output
}

interface NoInputUseCase<out Output> {
    operator fun invoke(): Output
}