package com.multimoney.data.database.util

sealed class DbConstants(val message: String) {
    object NoResults : DbConstants("No entry found in database")
}