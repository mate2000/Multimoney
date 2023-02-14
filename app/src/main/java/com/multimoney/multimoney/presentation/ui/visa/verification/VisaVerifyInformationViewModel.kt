package com.multimoney.multimoney.presentation.ui.visa.verification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaVerifyInformationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var identification: String = ""
    private var idCard: String = ""
    private var user: String = ""
    private var idBrand: Int = 0
    private var previousScreen = ""

    init {
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idCard = savedStateHandle[ID_CARD] ?: ""
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun onNavigateToNextScreen() = navigateTo(
        route = Screen.VisaVerifyDepositScreen.baseRoute
            .plus(
                getNavParam(IDENTIFICATION, identification)
            )
            .plus(
                getNavParam(ID_CARD, idCard)
            )
            .plus(
                getNavParam(USER, user)
            )
            .plus(
                getNavParam(ID_BRAND, idBrand)
            )
            .plus(
                getNavParam(PREVIOUS_SCREEN, previousScreen)
            )
    )

    private fun onNavigateBack() {
        if (previousScreen == Screen.ProfileCardListScreen.baseRoute) {
            navigateBack(popTo = Screen.ProfileCardListScreen.route, isRestart = false)
        } else {
            // TODO return to nickname card screen
        }
    }

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.visa_verified_dialog_title,
                descriptionResource = R.string.visa_verified_dialog_description,
                positiveResource = R.string.cancel,
                positiveAction = { onNavigateBackHome() },
                negativeResource = R.string.button_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnNavigateToNextScreen -> onNavigateToNextScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseClick : UIEvent()
        object OnNavigateToNextScreen : UIEvent()
    }
}
