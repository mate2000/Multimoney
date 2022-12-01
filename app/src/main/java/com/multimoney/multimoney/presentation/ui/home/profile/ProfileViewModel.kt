package com.multimoney.multimoney.presentation.ui.home.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set


    private fun getProfileInfo() = viewModelScope.launch {
        uiState = uiState.copy(
            userName = dataStorePreferences.getUserName().first(),
            email = dataStorePreferences.getUserEmail().first(),
            phoneNumber = dataStorePreferences.getUserPhoneNumber().first(),
            identification = dataStorePreferences.getIdentification().first(),
            pkUser = dataStorePreferences.getPkUser().first(),
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            firstName = savedStateHandle[FIRST_NAME]
        )
    }

    private fun navigateToPersonalInfoScreen(){
        navigateTo("${Screen.ProfilePersonalInfoScreen.baseRoute}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    data class UIState(
        // Fields
        val userName: String = "",
        val email: String = "",
        val phoneNumber: String = "",
        val identification : String = "",
        val idBrand: Int? = null,
        val pkUser: String? = null,
        val firstName : String? = null,
        val countryCode : String? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetProfileInfo -> getProfileInfo()
            is UIEvent.OnUpdateProfileClick -> navigateToPersonalInfoScreen()

        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetProfileInfo : UIEvent()
        object OnUpdateProfileClick : UIEvent()
        object OnMyAccountsClick : UIEvent()
        object OnMyCardsClick : UIEvent()
        object OnSettingsClick : UIEvent()
        object OnHelpClick : UIEvent()
        object OnInviteFriendsClick : UIEvent()
        object OnLogoutClick : UIEvent()
    }
}
