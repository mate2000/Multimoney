package com.multimoney.multimoney.presentation.ui.home.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.COUNTRY_CODE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
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
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
        )
    }

    private fun navigateToPersonalInfoScreen(){
        navigateTo("${Screen.PersonalInfoScreen.baseRoute}/${uiState.idBrand}/${uiState.phoneNumber}")
    }

    data class UIState(
        // Fields
        val userName: String = "",
        val userEmail: String = "",
        val phoneNumber: String = "",
        val idBrand: Int? = null,
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
