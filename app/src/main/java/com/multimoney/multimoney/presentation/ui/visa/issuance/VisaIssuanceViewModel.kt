package com.multimoney.multimoney.presentation.ui.visa.issuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnIssuanceClick
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisaIssuanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nfcHelper: NfcHelper,
    private val dataStorePreferences: DataStorePreferences
) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var pkUser: Long = 0
    var identification: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var balanceCardInformation: BalanceCardInformation? = null
    var availableBalanceLabel: String? = null
    private var idClient: Int = 0
    private var idLoanClient: Int = 0

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND) ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        getTextResources()
    }

    private fun getTextResources() {
        when {
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_no_nfc_subtitle
                )
            }
            (idBrand == Brand.Guatemala.id || idBrand == Brand.ElSalvador.id) && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_nfc_subtitle
                )
            }
            (idBrand == Brand.Guatemala.id || idBrand == Brand.ElSalvador.id) && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_no_nfc_subtitle
                )
            }
        }
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val titleResource: Int = R.string.empty,
        val subtitleResource: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnIssuanceClick -> {
                viewModelScope.launch {
                    if (dataStorePreferences.isAdjustFirstLinkMMVisaEventRegister().first()) {
                        registerAdjustEvent(AdjustEventType.MM_VISA_CTA_FIRST_LINK_MM_VISA_5038, applyAdjust = false, data = BaseEventDataDto(user = email, idBrand = idBrand, idClient = idClient, idLoanClient = idLoanClient, identification = identification).toJson())
                        dataStorePreferences.isAdjustFirstLinkMMVisaEventRegister(false)
                    }
                }
                popAndNavigateTo(
                    "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$phone/${
                    encodeData(
                        balanceCardInformation
                    )
                    }",
                    Screen.VisaIssuanceScreen.route
                )
            }
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnIssuanceClick : UIEvent()
    }
}
