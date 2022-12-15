package com.multimoney.multimoney.presentation.util.catalog

data class SuggestedAmount(
    var display: String = "",
    var value: String = "",
    var order: SuggestionOrder = SuggestionOrder.MIN
) {
    fun isSelected(order: SuggestionOrder) = this.order == order

    companion object {
        fun createSuggestion(
            isDollarAccount: Boolean,
            suggestionOrder: SuggestionOrder
        ): SuggestedAmount {
            return when (suggestionOrder) {
                SuggestionOrder.MIN -> SuggestedAmount(
                    display = if (isDollarAccount) MIN_DOLLAR_DISPLAY else MIN_COLON_DISPLAY,
                    value = if (isDollarAccount) MIN_DOLLAR_VALUE else MIN_COLON_VALUE,
                    order = suggestionOrder
                )
                SuggestionOrder.MEDIUM -> SuggestedAmount(
                    display = if (isDollarAccount) MEDIUM_DOLLAR_DISPLAY else MEDIUM_COLON_DISPLAY,
                    value = if (isDollarAccount) MEDIUM_DOLLAR_VALUE else MEDIUM_COLON_VALUE,
                    order = suggestionOrder
                )
                SuggestionOrder.MAX -> SuggestedAmount(
                    display = if (isDollarAccount) MAX_DOLLAR_DISPLAY else MAX_COLON_DISPLAY,
                    value = if (isDollarAccount) MAX_DOLLAR_VALUE else MAX_COLON_VALUE,
                    order = suggestionOrder
                )
            }
        }
    }
}

enum class SuggestionOrder {
    MIN,
    MEDIUM,
    MAX
}

private const val MIN_DOLLAR_DISPLAY = "$250"
private const val MIN_DOLLAR_VALUE = "250"
private const val MEDIUM_DOLLAR_DISPLAY = "$500"
private const val MEDIUM_DOLLAR_VALUE = "500"
private const val MAX_DOLLAR_DISPLAY = "$1,000"
private const val MAX_DOLLAR_VALUE = "1000"
private const val MIN_COLON_DISPLAY = "₡165,000"
private const val MIN_COLON_VALUE = "165000"
private const val MEDIUM_COLON_DISPLAY = "₡325,000"
private const val MEDIUM_COLON_VALUE = "325000"
private const val MAX_COLON_DISPLAY = "₡650,000"
private const val MAX_COLON_VALUE = "650000"

