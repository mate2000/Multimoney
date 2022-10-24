package com.multimoney.domain.model.util.catalog

sealed class ConfigurationPlatform(
    val value: String,
    val appVersion: String
) {
    object Android : ConfigurationPlatform(
        "Android",
        "20.1"
    )
}
