package com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_AND_EVICERTIA_ERROR
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToOnfidoProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnfidoAndEvicertiaErrorsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var idBrand: Int = 0

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        uiState = uiState.copy(error = savedStateHandle[ONFIDO_AND_EVICERTIA_ERROR] ?: "")
    }

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.OnfidoAndEvicertiaErrorsScreen.route
        )
    }

    private fun onNavigateToOnfidoProcess() {
        popAndNavigateTo(
            route = Screen.CreditScreen.route,
            popTo = Screen.OnfidoAndEvicertiaErrorsScreen.route
        )
    }

    data class UIState(
        val error: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is OnNavigateToOnfidoProcess -> onNavigateToOnfidoProcess()
        }
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnNavigateToOnfidoProcess : UIEvent()
    }
}
