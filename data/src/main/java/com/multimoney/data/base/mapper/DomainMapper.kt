package com.multimoney.data.base.mapper

interface DomainMapper<T : Any> {
    fun mapToDomainModel(): T
}