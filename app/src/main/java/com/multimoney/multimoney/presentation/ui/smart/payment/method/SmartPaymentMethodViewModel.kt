package com.multimoney.multimoney.presentation.ui.smart.payment.method

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIState
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnTransferSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnVisaSelected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartPaymentMethodViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess
    var userSmartAccount: String = ""
    var username: String = savedStateHandle[USER] ?: ""
    var idBrand: String = savedStateHandle[ID_BRAND] ?: ""
    var identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    var accountToken: String = savedStateHandle[ACCOUNT_TOKEN] ?: ""
    var currencyId: Int = savedStateHandle[ID_CURRENCY] ?: 0


    private fun onStart() {
        viewModelScope.launch {
            userSmartAccount = savedStateHandle[USER_SMART_ACCOUNT] ?: ""
            idBrand = dataStorePreferences.getIdBrand().first()
            identification = dataStorePreferences.getIdentification().first()
        }
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    private fun navigateToTransferScreen() {
        navigateTo("${Screen.SavingMethodTransferScreen.baseRoute}/$userSmartAccount")
    }

    private fun navigateToVisaScreen() {
        navigateTo(
            "${Screen.SmartPaymentCardsScreen.baseRoute}/$username/$idBrand/$identification/${accountToken.toLongOrNull()}/$currencyId"
        )
        //TODO verify if there are added cards
        navigateTo("${Screen.PaymentSmartCardsScreen.baseRoute}/$userSmartAccount/$idBrand/$identification")
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnStart -> onStart()
            is OnNavigateBack -> onNavigateBack()
            is OnTransferSelected -> navigateToTransferScreen()
            is OnVisaSelected -> navigateToVisaScreen()
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnTransferSelected : UIEvent()
        object OnVisaSelected : UIEvent()
    }
}
