package com.multimoney.multimoney.presentation.ui.visa.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.mmvisa.MutationDeleteTokenDeviceNVUseCase
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.Companion.NOVO_CARD_TOKEN_EMPTY
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.Companion.NOVO_CARD_TOKEN_EMPTY_TWO
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnCheckedChange
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnNavigateToVisaTokenizationScreen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class VisaPreferencesViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val mutationDeleteTokenDeviceNVUseCase: MutationDeleteTokenDeviceNVUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var pkUser: Long = 0
    var identification: String = ""
    private var user: String = ""
    private var phone: String = ""
    private var cardInformation: BalanceCardInformation? = null
    var availableBalanceLabel: String? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND)?.toInt() ?: 0
        idClient = savedStateHandle.get<Int>(ID_CLIENT)?.toInt() ?: 0
        idLoanClient = savedStateHandle.get<Int>(ID_LOAN_CLIENT)?.toInt() ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        user = savedStateHandle.get<String>(USER) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        cardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
        callNovoGetFavoriteCard()
    }

    private fun callNovoGetFavoriteCard() {
        uiState = uiState.copy(
            switchButtonValue = NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY && NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY_TWO
        )
    }

    private fun onOpenLinkCardDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.card_preferences_linked_card_dialog_title,
                descriptionResource = R.string.card_preferences_linked_card_dialog_description,
                positiveResource = R.string.card_preferences_linked_card_dialog_positive_button,
                negativeResource = R.string.card_preferences_linked_card_dialog_negative_button,
                positiveAction = { onNavigateToVisaTokenizationScreen() },
                negativeAction = { uiState = uiState.copy(switchButtonValue = uiState.switchButtonValue.not()) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onOpenUnlinkCardDialog(onDeleteTokenBaseEvent: () -> Unit) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.card_preferences_unlinked_card_dialog_title,
                descriptionResource = R.string.card_preferences_unlinked_card_dialog_description,
                positiveResource = R.string.card_preferences_unlinked_card_dialog_positive_button,
                negativeResource = R.string.card_preferences_unlinked_card_dialog_negative_button,
                positiveAction = { onDeleteTokenDevice(onDeleteTokenBaseEvent) },
                negativeAction = { uiState = uiState.copy(switchButtonValue = uiState.switchButtonValue.not()) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = navigateBack(Screen.VisaCardScreen.route, false)

    private fun onNavigateToVisaTokenizationScreen() = navigateTo(
        "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$identification/$user/$phone/${
        encodeData(
            cardInformation
        )
        }"
    )

    private fun onDeleteTokenDevice(onDeleteTokenBaseEvent: () -> Unit) = executeUseCase {
        mutationDeleteTokenDeviceNVUseCase.invoke(
            identification = identification,
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            idDevice = NovoVTS.getDeviceId()
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                NovoVTS.setFavoriteCard(NOVO_CARD_TOKEN_EMPTY)
                navigateHome(onDeleteTokenBaseEvent)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    switchButtonValue = uiState.switchButtonValue.not(),
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun navigateHome(onDeleteTokenBaseEvent: () -> Unit) {
        navigateBack(Screen.HomeScreen.route, false)
        onDeleteTokenBaseEvent()
    }

    private fun onCheckedChange(value: Boolean, onDeleteTokenBaseEvent: () -> Unit) {
        uiState = uiState.copy(switchButtonValue = value)
        if (value) {
            onOpenLinkCardDialog()
        } else {
            onOpenUnlinkCardDialog(onDeleteTokenBaseEvent)
        }
    }

    data class UIState(
        // Interactions
        val switchButtonValue: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCheckedChange -> onCheckedChange(uiEvent.value, uiEvent.onDeleteTokenBaseEvent)
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateToVisaTokenizationScreen -> onNavigateToVisaTokenizationScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToVisaTokenizationScreen : UIEvent()
        data class OnCheckedChange(val value: Boolean, val onDeleteTokenBaseEvent: () -> Unit) : UIEvent()
    }
}
