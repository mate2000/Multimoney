package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            idBrand = savedStateHandle[ID_BRAND],
            pkUser = savedStateHandle[PK_USER],
            idClient = savedStateHandle[ID_CLIENT],
            identification = savedStateHandle[IDENTIFICATION],
            email = savedStateHandle.get<String>(EMAIL)?.trim()?.lowercase(Locale.getDefault()),
            firstName = savedStateHandle[FIRST_NAME],
            userName = savedStateHandle[USER_NAME],
        )
    }

    private fun navigateToEditEmail() {
        navigateTo("${Screen.ProfileChangeEmailScreen.baseRoute}/${uiState.idClient}/${FieldToChange.EMAIL.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    private fun navigateToEditPhone() {
        navigateTo("${Screen.ProfileChangePhoneScreen.baseRoute}/${uiState.idClient}/${FieldToChange.PHONE.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    data class UIState(
        // Fields
        val userData: UserData? = null,
        val userName: String? = null,
        val email: String? = null,
        val identification: String? = null,
        val pkUser: String? = null,
        val firstName: String? = null,
        val phoneNumber: String? = null,
        val idBrand: Int? = null,
        val idClient : Int? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnNavigateToEditPhone -> navigateToEditPhone()
            is UIEvent.OnNavigateToEditEmail -> navigateToEditEmail()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToEditPhone : UIEvent()
        object OnNavigateToEditEmail : UIEvent()
    }
}
