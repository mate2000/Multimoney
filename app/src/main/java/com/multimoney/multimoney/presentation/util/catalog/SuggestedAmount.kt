package com.multimoney.multimoney.presentation.util.catalog

abstract class SuggestedAmount(val isDollarAccount: Boolean) {
    open val display: String = ""
    open val value: String = ""
}

class FirstSuggestion : SuggestedAmount() {
    override val display: String
        get() = setDisplay()
    override val value: String
        get() = setValue()

    private fun setDisplay() = if (isDollarAccount) "$250" else ""
    private fun setValue() = if (isDollarAccount) "250" else ""
}

class SecondSuggestion(private val isDollarAccount: Boolean) : SuggestedAmount {
    override val display: String
        get() = setDisplay()
    override val value: String
        get() = setValue()

    private fun setDisplay() = if (isDollarAccount) "$500" else ""
    private fun setValue() = if (isDollarAccount) "500" else ""
}

class ThirdSuggestion(private val isDollarAccount: Boolean) : SuggestedAmount {
    override val display: String
        get() = setDisplay()
    override val value: String
        get() = setValue()

    private fun setDisplay() = if (isDollarAccount) "$1,000" else ""
    private fun setValue() = if (isDollarAccount) "1000" else ""
}

