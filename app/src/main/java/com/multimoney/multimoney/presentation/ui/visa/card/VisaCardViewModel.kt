package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.data.util.catalog.Brand.Guatemala
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnAvailableAmountClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateToVisaTokenizationScreen
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnOpenDialogConfirmToStartTokenizationProcess
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaCardViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val nfcHelper: NfcHelper) :
    BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var pkUser: Long = 0
    private var email: String = ""
    private var phone: String = ""
    private var cardInformation: CardInformation? = null
    var availableBalanceLabel: String? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND)?.toInt() ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        cardInformation = savedStateHandle.get<CardInformation>(CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
        callNovoGetFavoriteCard()
    }

    private fun onAvailableAmountClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                descriptionResource = when (idBrand) {
                    ElSalvador.id -> string.visa_card_sv_dialog_description_available
                    CostaRica.id -> string.visa_card_cr_dialog_description_available
                    Guatemala.id -> string.visa_card_gt_dialog_description_available
                    else -> string.empty
                },
                positiveResource = string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun callNovoGetFavoriteCard() {
        uiState = uiState.copy(
            isNfcAvailable = nfcHelper.isNfcSupported(),
            isCardTokenize = NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY
        )
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val isNfcAvailable: Boolean = false,
        val isCardTokenize: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnAvailableAmountClick -> onAvailableAmountClick()
            is OnNavigateToVisaTokenizationScreen -> navigateTo(
                "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$email/$phone/${
                encodeData(
                    cardInformation
                )
                }"
            )
            is OnOpenDialogConfirmToStartTokenizationProcess -> uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = string.visa_card_dialog_title,
                    descriptionResource = string.visa_card_dialog_description,
                    positiveResource = string.link,
                    negativeResource = string.cancel,
                    positiveAction = {
                        onUIEvent(OnNavigateToVisaTokenizationScreen)
                    },
                    isActive = mutableStateOf(true)
                )
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAvailableAmountClick : UIEvent()
        object OnNavigateToVisaTokenizationScreen : UIEvent()
        object OnOpenDialogConfirmToStartTokenizationProcess : UIEvent()
    }

    companion object {
        const val NOVO_CARD_TOKEN_EMPTY = "-1"
    }
}
