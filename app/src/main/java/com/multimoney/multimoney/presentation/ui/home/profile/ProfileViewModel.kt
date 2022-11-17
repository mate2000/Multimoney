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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

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
            userEmail = dataStorePreferences.getUserEmail().first(),
            phoneNumber = dataStorePreferences.getUserPhoneNumber().first(),
            idBrand =  savedStateHandle.get<String>(ID_BRAND)?.toInt() ?: 0
        )
    }

    data class UIState(
        // Fields
        val userName: String = "",
        val userEmail: String = "",
        val phoneNumber: String = "",
        val idBrand: Int = 0,
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetProfileInfo -> getProfileInfo()
            is UIEvent.OnUpdateProfileClick -> Timber.d("navigate to update profile screen")
            is UIEvent.OnMyAccountsClick -> Timber.d("navigate to my account screen")
            is UIEvent.OnMyCardsClick -> Timber.d("navigate to my cards screen")
            is UIEvent.OnSettingsClick -> Timber.d("navigate to settings screen")
            is UIEvent.OnHelpClick -> Timber.d("navigate to help screen")
            is UIEvent.OnInviteFriendsClick -> Timber.d("navigate to invite friends screen")
            is UIEvent.OnLogoutClick -> Timber.d("handle logout action")
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
