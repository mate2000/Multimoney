package com.multimoney.multimoney.presentation.util.catalog

interface SuggestedAmount {
    val display: String
    val value: String
}

enum class SuggestedAmountSV : SuggestedAmount {
    TWO_HUNDRED {
        override val display: String
            get() = "$200"
        override val value: String
            get() = "200"
    },
    FIVE_HUNDRED {
        override val display: String
            get() = "$500"
        override val value: String
            get() = "500"
    },
    ONE_THOUSAND {
        override val display: String
            get() = "$1,000"
        override val value: String
            get() = "1000"
    }
}
