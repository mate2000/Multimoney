package com.multimoney.data.util.exeption

enum class MultimoneyException(val description: String) {
    APOLLO_PARSE_EXCEPTION("There was an error trying to parse GraphQL server response"),
    APOLLO_ERROR("An apollo client error occurred"),
    UNKNOWN_ERROR("An unexpected error occurred")
}
