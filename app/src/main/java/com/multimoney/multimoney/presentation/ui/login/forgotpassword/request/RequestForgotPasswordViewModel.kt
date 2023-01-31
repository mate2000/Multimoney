package com.multimoney.multimoney.presentation.ui.login.forgotpassword.request

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnChangePasswordClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnValidateEmail
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDeviceId
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class RequestForgotPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryValidateUserExistsUseCase: QueryValidateUserExistsUseCase
) : BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var previousScreen: String? = null
    private var idBrand: Int = Brand.Default.id
    private var pkUser: String = ""

    init {
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun onNavigateBack(focusManager: FocusManager) {
        focusManager.clearFocus()
        if (uiState.isAlertResultVisible) {
            uiState = uiState.copy(isAlertResultVisible = false)
        } else {
            onCloseClick(focusManager)
        }
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        navigateBack(popTo = previousScreen ?: "", isRestart = false)
    }

    private fun onEmailValueChange(value: String) {
        uiState = uiState.copy(email = value)
        isFormValid()
    }

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.email.isBlank() -> false
                isEmailValid(uiState.email).not() -> false
                else -> true
            }
        )
    }

    private fun isEmailValid() {
        uiState = uiState.copy(
            emailError = if (isEmailValid(uiState.email).not()) {
                Pair(true, R.string.request_forgot_password_email_not_valid)
            } else {
                Pair(false, R.string.error_empty)
            }
        )
    }

    private fun callQueryValidationUserExistsUseCase(activity: FragmentActivity) =
        executeUseCase {
            queryValidateUserExistsUseCase(
                email = uiState.email,
                deviceId = getDeviceId(activity)
            ).collectLatest { result ->
                result.onSuccess {
                    idBrand = Brand.Default.id
                    uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
                }.onMessage {
                    idBrand = it?.idBrand ?: Brand.Default.id
                    pkUser = it?.pkUser.orEmpty()
                    onResetPassword()
                }.onFailure {
                    idBrand = Brand.Default.id
                    uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    private fun onResetPassword() = Amplify.Auth.resetPassword(
        uiState.email,
        {
            uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
        },
        {
            uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
        }
    )

    private fun onContinueClick(activity: FragmentActivity, focusManager: FocusManager) {
        focusManager.clearFocus()
        callQueryValidationUserExistsUseCase(activity)
    }

    private fun onChangePasswordClick() = popAndNavigateTo(
        route = Screen.ProcessForgotPassword.baseRoute
            .plus(
                getNavParam(PREVIOUS_SCREEN, previousScreen)
            )
            .plus(
                getNavParam(EMAIL, uiState.email)
            )
            .plus(
                getNavParam(ID_BRAND, idBrand)
            )
            .plus(
                getNavParam(PK_USER, pkUser)
            ),
        popTo = Screen.RequestForgotPassword.route
    )

    data class UIState(
        // Interactions
        val email: String = "",
        val emailError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val isFormValid: Boolean = false,
        val isLoading: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack(uiEvent.focusManager)
            is OnCloseClick -> onCloseClick(uiEvent.focusManager)
            is OnContinueClick -> onContinueClick(uiEvent.activity, uiEvent.focusManager)
            is OnChangePasswordClick -> onChangePasswordClick()
            is OnEmailValueChange -> onEmailValueChange(uiEvent.value)
            is OnValidateEmail -> isEmailValid()
        }
    }

    sealed class UIEvent {
        data class OnNavigateBack(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val activity: FragmentActivity, val focusManager: FocusManager) : UIEvent()
        object OnChangePasswordClick : UIEvent()
        data class OnEmailValueChange(val value: String) : UIEvent()
        object OnValidateEmail : UIEvent()
    }
}
