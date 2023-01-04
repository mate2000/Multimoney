package com.multimoney.multimoney.presentation.ui.visa.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnCheckedChange
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaPreferencesViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val nfcHelper: NfcHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var identification: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0

    init {
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
    }

    private fun onOpenLinkCardDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.card_preferences_linked_card_dialog_title,
                descriptionResource = R.string.card_preferences_linked_card_dialog_description,
                positiveResource = R.string.card_preferences_linked_card_dialog_positive_button,
                negativeResource = R.string.card_preferences_linked_card_dialog_negative_button,
                positiveAction = {},
                negativeAction = { uiState = uiState.copy(switchButtonValue = uiState.switchButtonValue.not()) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onOpenUnlinkCardDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.card_preferences_unlinked_card_dialog_title,
                descriptionResource = R.string.card_preferences_unlinked_card_dialog_description,
                positiveResource = R.string.card_preferences_unlinked_card_dialog_positive_button,
                negativeResource = R.string.card_preferences_unlinked_card_dialog_negative_button,
                positiveAction = {},
                negativeAction = { uiState = uiState.copy(switchButtonValue = uiState.switchButtonValue.not()) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = navigateBack(Screen.VisaCardScreen.route, false)

    private fun onCheckedChange(value: Boolean) {
        uiState = uiState.copy(switchButtonValue = value)
        if (value) {
            onOpenLinkCardDialog()
        } else {
            onOpenUnlinkCardDialog()
        }
    }

    data class UIState(
        // Interactions
        val switchButtonValue: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCheckedChange -> onCheckedChange(uiEvent.value)
            is OnNavigateBack -> onNavigateBack()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnCheckedChange(val value: Boolean) : UIEvent()
    }
}
