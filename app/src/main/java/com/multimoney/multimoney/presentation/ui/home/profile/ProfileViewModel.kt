package com.multimoney.multimoney.presentation.ui.home.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand.Guatemala
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import timber.log.Timber
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val cognitoHelper: CognitoHelper,
    private val countDownTimer: MMCountDownTimer
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set


    private fun getProfileInfo()  {
        uiState = uiState.copy(
            userName = savedStateHandle[USER_NAME],
            email = savedStateHandle[EMAIL],
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            identification = savedStateHandle[IDENTIFICATION],
            pkUser = savedStateHandle[PK_USER],
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            firstName = savedStateHandle[FIRST_NAME]
        )
    }


    private fun navigateToPersonalInfoScreen() {
        navigateTo("${Screen.ProfilePersonalInfoScreen.baseRoute}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    private fun signOutDialogConfirmation() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = if (uiState.idBrand == Guatemala.id) R.string.sign_out_dialog_title_gt else R.string.sign_out_dialog_title,
                descriptionResource = R.string.sign_out_dialog_description,
                positiveResource = R.string.button_continue,
                negativeResource = R.string.cancel,
                positiveAction = { signOut() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun signOut() {
        cognitoHelper.signOut(signOutError = {
            Timber.d("SignOut Error")
        })
        viewModelScope.launch {
            dataStorePreferences.setAuthToken("")
        }
        countDownTimer.discardTimer()
        popAndNavigateTo(
            Screen.SignInScreen.route,
            Screen.HomeScreen.route
        )
    }

    data class UIState(
        // Fields
        val userName: String? = null,
        val email: String? = null,
        val phoneNumber: String? = null,
        val identification : String? = null,
        val idBrand: Int? = null,
        val pkUser: String? = null,
        val firstName : String? = null,
        val countryCode : String? = null,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetProfileInfo -> getProfileInfo()
            is UIEvent.OnUpdateProfileClick -> navigateToPersonalInfoScreen()
            is UIEvent.OnMyAccountsClick -> Timber.d("navigate to my account screen")
            is UIEvent.OnMyCardsClick -> Timber.d("navigate to my cards screen")
            is UIEvent.OnSettingsClick -> Timber.d("navigate to settings screen")
            is UIEvent.OnHelpClick -> Timber.d("navigate to help screen")
            is UIEvent.OnInviteFriendsClick -> Timber.d("navigate to invite friends screen")
            is UIEvent.OnLogoutClick -> signOutDialogConfirmation()

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
