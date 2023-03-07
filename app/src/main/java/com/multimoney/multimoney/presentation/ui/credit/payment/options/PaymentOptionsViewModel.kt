package com.multimoney.multimoney.presentation.ui.credit.payment.options

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.PaymentMethod
import com.multimoney.domain.model.security.TransferAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.INFO_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_METHOD
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnPaymentMethodClick
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentOptionsViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var infoUser: InfoUser? = null
    private var identification: String = ""
    private var creditNumber: String? = null
    private var transferAccount: TransferAccount? = null
    private var minimumPayment: Float? = null
    private var minimumPaymentLabel: String? = null
    private var maximumPayment: Float? = null
    private var maximumPaymentLabel: String = ""
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    private var idCurrency: Int? = null
    private var paymentDate: String? = ""

    init {
        infoUser = savedStateHandle[INFO_USER]
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER]
        transferAccount = savedStateHandle[TRANSFER_ACCOUNT]
        minimumPayment = savedStateHandle[MINIMUM_PAYMENT]
        minimumPaymentLabel = savedStateHandle[MINIMUM_PAYMENT_LABEL]
        maximumPayment = savedStateHandle[MAXIMUM_PAYMENT]
        maximumPaymentLabel = savedStateHandle[MAXIMUM_PAYMENT_LABEL] ?: ""
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
        idCurrency = savedStateHandle[ID_CURRENCY]
        paymentDate = savedStateHandle[PAYMENT_DATE] ?: ""
        uiState = uiState.copy(paymentMethodList = savedStateHandle.get<Array<PaymentMethod>>(PAYMENT_METHOD)?.toList())
    }

    private fun onGetTextResource() {
        uiState = uiState.copy(titleResource = R.string.payment_options_title_sv)
    }

    private fun onPaymentMethodClick(paymentMethodType: String) {
        val route = when (paymentMethodType) {
            PaymentMethodType.TransferBank.value ->
                "${Screen.PaymentOptionsTransferScreen.baseRoute}/${infoUser?.idBrand ?: 0}/$creditNumber/${
                    encodeData(
                        transferAccount
                    )
                }"
            PaymentMethodType.VisaDirect.value -> {
                "${Screen.PaymentCardsListScreen.baseRoute}/$identification/$creditNumber/$idClient/$idLoanClient/$minimumPayment/$minimumPaymentLabel/$maximumPayment/$maximumPaymentLabel/$idCurrency/$paymentDate/${
                    encodeData(
                        infoUser
                    )
                }"
            }
            else -> {
                "${Screen.PaymentPointsScreen.baseRoute}/${infoUser?.idBrand ?: 0}/$creditNumber/$minimumPaymentLabel"
            }
        }
        navigateTo(route = route)
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val paymentMethodList: List<PaymentMethod>? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetTextResources -> onGetTextResource()
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnPaymentMethodClick -> onPaymentMethodClick(uiEvent.paymentMethodType)
        }
    }

    sealed class UIEvent {
        object OnGetTextResources : UIEvent()
        object OnNavigateBack : UIEvent()
        class OnPaymentMethodClick(val paymentMethodType: String) : UIEvent()
    }
}
