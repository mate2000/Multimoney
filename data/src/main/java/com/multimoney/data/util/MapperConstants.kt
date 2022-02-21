package com.multimoney.data.util

sealed class MapperConstants(val message: String) {
    object MapperFailed : MapperConstants("Mapper failed")
}