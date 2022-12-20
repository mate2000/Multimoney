package com.multimoney.multimoney.presentation.ui.visa.issuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnIssuanceClick
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaIssuanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nfcHelper: NfcHelper
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
    private var cardInformation: CardInformation? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND) ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        cardInformation = savedStateHandle.get<CardInformation>(CARD_INFORMATION)
        getTextResources()
    }

    private fun getTextResources() {
        when {
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_nfc_subtitle
                )
            }
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_no_nfc_subtitle
                )
            }
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
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_no_nfc_subtitle
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
            is OnIssuanceClick -> popAndNavigateTo(
                "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$phone/${
                encodeData(
                    cardInformation
                )
                }",
                Screen.VisaIssuanceScreen.route
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnIssuanceClick : UIEvent()
    }
}
