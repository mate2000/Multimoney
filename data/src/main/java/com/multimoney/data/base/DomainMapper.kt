package com.multimoney.data.base

/**
 * Created by rodrigomiranda on 3/1/20.
 * Applaudo Studios
 */
interface DomainMapper<T : Any> {
    fun mapToDomainModel(): T
}