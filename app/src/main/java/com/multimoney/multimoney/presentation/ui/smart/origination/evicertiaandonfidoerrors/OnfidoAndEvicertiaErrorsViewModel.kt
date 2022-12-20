package com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_AND_EVICERTIA_ERROR
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToOnfidoProcess
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
    var pkUser: Long = 0
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Long = 0
    var firstName: String = ""
    var lastName: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        uiState = uiState.copy(error = savedStateHandle[ONFIDO_AND_EVICERTIA_ERROR] ?: "")
    }

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartOnfidoAndEvicertiaErrorsScreen.route
        )
    }

    private fun onNavigateToOnfidoProcess() {
        popAndNavigateTo(
            route = "${Screen.SmartOnfidoScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$PRINT_EMPTY/$URL_EMPTY/${SmartOnFidoOrFirmStatus.FIRMED.status}",
            popTo = Screen.SmartOnfidoAndEvicertiaErrorsScreen.route
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

    companion object {
        private const val PRINT_EMPTY = 0
        private const val URL_EMPTY = "url"
    }
}