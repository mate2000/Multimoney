package com.multimoney.multimoney.presentation.ui.credit.howmuchyouwantpay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.Currency
import javax.inject.Inject

class HowMuchYouWantPayViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.VisaIssuanceScreen.route
            )
            is UIEvent.OnValidateAmount -> validateAmount()
            is UIEvent.OnGetTextResources -> getTextResources(5/*uiEvent.idBrand.toInt()*/)
            is UIEvent.OnAmountValueChange -> onAmountValueChange(uiEvent.value)
        }
    }

    private fun onAmountValueChange(value: String) {
        val valueToReplace = value.replace(uiState.currency, "").replace(",", "")
        val valueToUse = if (valueToReplace == "") { 0 } else { valueToReplace.toInt() }

        uiState = uiState.copy(
            currentAmountValueString = value,
            currentAmountValue = valueToUse
        )
        clearAmountError()
        isValidAmount()
    }

    private fun isValidAmount() {
        uiState = uiState.copy(
            enableButton = (
                uiState.currentAmountValue <= uiState.maxAmountValue &&
                    uiState.currentAmountValue >= uiState.minAmountValue
                )
        )
    }

    private fun validateAmount() {
        uiState = uiState.copy(
            currentAmountError = if (uiState.currentAmountValue > uiState.maxAmountValue) {
                Pair(true, R.string.how_much_you_want_to_pay_amount_max_error)
            } else if (uiState.currentAmountValue < uiState.minAmountValue) {
                Pair(true, R.string.how_much_you_want_to_pay_amount_min_error)
            } else {
                Pair(false, R.string.error_empty)
            }
        )
    }

    private fun clearAmountError() {
        uiState = uiState.copy(
            currentAmountError = Pair(false, R.string.error_empty)
        )
    }

    private fun getTextResources(idBrand: Int) {
        when (idBrand) {
            Brand.CostaRica.id -> {
                uiState = uiState.copy(
                    titleResource = R.string.how_much_you_want_to_pay_title_cr,
                    subtitleResource = R.string.how_much_you_want_to_pay_subtitle_cr,
                    subtitleMinButton = R.string.how_much_you_want_to_pay_min_amount_cr,
                    subtitleMaxButton = R.string.how_much_you_want_to_pay_max_amount_cr,
                    minAmountResource = R.string.how_much_you_want_to_pay_amount_cr,
                    maxAmountResource = R.string.how_much_you_want_to_pay_amount_cr,
                    currentAmountResource = R.string.how_much_you_want_to_pay_amount_cr,
                    currency = Currency.COSTA_RICA
                )
            }
        }
    }

    fun setCurrentValueToMin() {
        onAmountValueChange(uiState.minAmountValue.toString())
    }

    fun setCurrentValueToMax() {
        onAmountValueChange(uiState.maxAmountValue.toString())
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val titleResource: Int = R.string.empty,
        val subtitleResource: Int = R.string.empty,
        val subtitleMinButton: Int = R.string.empty,
        val subtitleMaxButton: Int = R.string.empty,
        val minAmountResource: Int = R.string.empty,
        val maxAmountResource: Int = R.string.empty,
        val currentAmountResource: Int = R.string.empty,
        val minAmountValue: Int = 5000,
        val maxAmountValue: Int = 35000,
        val currentAmountValue: Int = 0,
        val currency: String = "",
        val currentAmountValueString: String = currentAmountValue.toString(),
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false
    )

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateAmount : UIEvent()
        class OnGetTextResources(val idBrand: String) : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
    }
}
