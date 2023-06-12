package com.multimoney.data.base

interface DomainMapper<T : Any> {
    fun mapToDomainModel(): T
}