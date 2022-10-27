package com.multimoney.domain.model.util.catalog

sealed class ConfigurationPlatform(
    val value: String
) {
    object Android : ConfigurationPlatform(
        "Android"
    )
}
