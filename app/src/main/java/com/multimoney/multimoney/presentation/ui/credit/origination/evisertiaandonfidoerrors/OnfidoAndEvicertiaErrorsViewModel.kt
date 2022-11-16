package com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
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
    var pkUser: Int = 0
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Long = 0
    var firstName: String = ""
    var lastName: String = ""
    var idPrint: Long = 0
    var evicertiaUrl: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        evicertiaUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
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
            route = "${Screen.CreditOnfidoScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$idPrint/$evicertiaUrl",
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
