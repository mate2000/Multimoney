package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompanyAddressViewModel @Inject constructor() : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    val country = ZERO

    private fun onDivisionOneValueChange(divisionOne: String) {

    }

    private fun onDivisionTwoValueChange(divisionTwo: String) {

    }

    private fun onDivisionThreeValueChange(divisionThree: String) {

    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
    }

    data class UIState(
        val divisionOne: String = "",
        val divisionTwo: String = "",
        val divisionThree: String = "",
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error)
    )

    fun onUiEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnDivisionOneValueChange -> onDivisionOneValueChange(uiEvent.divisionOne)
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(uiEvent.divisionTwo)
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(uiEvent.divisionThree)
            is OnAddressValueChange -> onAddressValueChange(uiEvent.address)
        }
    }

    sealed class UIEvent {
        data class OnDivisionOneValueChange(val divisionOne: String) : UIEvent()
        data class OnDivisionTwoValueChange(val divisionTwo: String) : UIEvent()
        data class OnDivisionThreeValueChange(val divisionThree: String) : UIEvent()
        data class OnAddressValueChange(val address: String) : UIEvent()
    }

    companion object {
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
    }
}