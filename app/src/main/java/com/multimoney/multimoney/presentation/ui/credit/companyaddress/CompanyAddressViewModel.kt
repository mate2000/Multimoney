package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionOneChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionThreeChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionTwoChange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompanyAddressViewModel @Inject constructor() : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    val country = ZERO

    private fun onDivisionOneChange(divisionOne: String) {

    }

    private fun onDivisionTwoChange(divisionTwo: String) {

    }

    private fun onDivisionThreeChange(divisionThree: String) {

    }

    data class UIState(
        val divisionOne: String = "",
        val divisionTwo: String = "",
        val divisionThree: String = "",
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error)
    )

    fun onUiEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnDivisionOneChange -> onDivisionOneChange(uiEvent.onDivisionOne)
            is OnDivisionTwoChange -> onDivisionOneChange(uiEvent.onDivisionTwo)
            is OnDivisionThreeChange -> onDivisionOneChange(uiEvent.onDivisionThree)
        }
    }

    sealed class UIEvent {
        data class OnDivisionOneChange(val onDivisionOne: String) : UIEvent()
        data class OnDivisionTwoChange(val onDivisionTwo: String) : UIEvent()
        data class OnDivisionThreeChange(val onDivisionThree: String) : UIEvent()
    }

    companion object {
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
    }
}