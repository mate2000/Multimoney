package com.multimoney.multimoney.presentation.ui.payment.options

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.PaymentMethod
import com.multimoney.domain.model.security.TransferAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnSaveArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentOptionsViewModel @Inject constructor() : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var creditNumber: String? = null
    private var transferAccount: TransferAccount? = null

    private fun onSaveArguments(
        idBrand: Int,
        creditNumber: String?,
        paymentMethodList: List<PaymentMethod>?,
        transferAccount: TransferAccount?
    ) {
        this.idBrand = idBrand
        this.creditNumber = creditNumber
        this.transferAccount = transferAccount
        uiState = uiState.copy(paymentMethodList = paymentMethodList)
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
            is OnSaveArguments -> onSaveArguments(
                uiEvent.idBrand,
                uiEvent.creditNumber,
                uiEvent.paymentMethodList,
                uiEvent.transferAccount
            )
            is OnGetTextResources -> onGetTextResource()
            is OnNavigateBack -> navigateBack()
        }
    }

    sealed class UIEvent {

        data class OnSaveArguments(
            val idBrand: Int,
            val creditNumber: String?,
            val paymentMethodList: List<PaymentMethod>?,
            val transferAccount: TransferAccount?
        ) : UIEvent()

        object OnGetTextResources : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}
