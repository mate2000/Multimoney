package com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.OTP_METHOD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnOtpOptionSelected
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisteredUserOtpOptionsViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var previousScreen: String = ""
    private var idBrand: Int = 0
    var userData: UserData? = null

    init {
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        userData = savedStateHandle.get<UserData>(USER_DATA)
    }

    private fun onStart() {
        uiState = if (idBrand == Brand.CostaRica.id) {
            uiState.copy(
                titleResource = R.string.registered_user_otp_options_title_cr,
                messageResource = R.string.registered_user_otp_options_message_cr,
                isFormValid = isFormValid()
            )
        } else {
            uiState.copy(
                titleResource = R.string.registered_user_otp_options_title,
                messageResource = R.string.registered_user_otp_options_message,
                isFormValid = isFormValid()
            )
        }
    }

    private fun isFormValid() = when {
        uiState.otpOption.isBlank() -> false
        else -> true
    }

    private fun onOtpOptionSelected(value: String) {
        uiState = uiState.copy(otpOption = value)
        uiState = uiState.copy(isFormValid = isFormValid())
    }

    private fun onBackClick() = navigateBack(
        popTo = when (previousScreen) {
            Screen.RegisteredUserEmailScreen.baseRoute -> Screen.RegisteredUserEmailScreen.route
            else -> Screen.SignUpScreen.route
        },
        isRestart = false
    )

    private fun onContinueClick() = navigateTo(
        route = Screen.RegisteredUserOtpScreen.baseRoute
            .plus(
                getNavParam(ID_BRAND, idBrand)
            )
            .plus(
                getNavParam(USER_DATA, encodeData(userData))
            )
            .plus(
                getNavParam(OTP_METHOD, uiState.otpOption)
            )
    )

    data class UIState(
        // Fields
        val titleResource: Int = R.string.empty,
        val messageResource: Int = R.string.empty,
        val otpOption: String = "",
        val isFormValid: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnOtpOptionSelected -> onOtpOptionSelected(event.value)
            is OnStart -> onStart()
            is OnBackClick -> onBackClick()
            is OnContinueClick -> onContinueClick()
        }
    }

    sealed class UIEvent {
        data class OnOtpOptionSelected(val value: String) : UIEvent()
        object OnStart : UIEvent()
        object OnBackClick : UIEvent()
        object OnContinueClick : UIEvent()
    }
}
