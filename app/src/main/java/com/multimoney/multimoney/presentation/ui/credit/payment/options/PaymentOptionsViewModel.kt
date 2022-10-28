package com.multimoney.multimoney.presentation.ui.credit.payment.options

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.PaymentMethod
import com.multimoney.domain.model.security.TransferAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_METHOD
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnNavigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentOptionsViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var creditNumber: String? = null
    private var transferAccount: TransferAccount? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        creditNumber = savedStateHandle[CREDIT_NUMBER]
        transferAccount = savedStateHandle[TRANSFER_ACCOUNT]
        uiState = uiState.copy(paymentMethodList = savedStateHandle.get<Array<PaymentMethod>>(PAYMENT_METHOD)?.toList())
    }

    private fun onGetTextResource() {
        uiState = uiState.copy(
            titleResource = if (idBrand == Brand.ElSalvador.id) {
                R.string.payment_options_title_sv
            } else {
                R.string.payment_options_title_gt
            }
        )
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val paymentMethodList: List<PaymentMethod>? = null,
        val transferAccount: TransferAccount? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetTextResources -> onGetTextResource()
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
        }
    }

    sealed class UIEvent {
        object OnGetTextResources : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}
