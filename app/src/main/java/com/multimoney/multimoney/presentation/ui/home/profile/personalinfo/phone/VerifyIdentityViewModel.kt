package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.NEW_PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.util.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class VerifyIdentityViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {




    data class UIState(
        // Fields
        val userName: String? = null,
        val phoneNumber: String? = null,
        val newPhoneNumber: String? = null,
        val identification: String? = null,
        val idBrand: Int? = null,
        val phoneCode: String = "",
        val email : String? = null,
        val countryCode: String? = null,
        val isButtonEnabled: Boolean = false,
        val questionOneValue: Boolean = true,
        val questionTwoValue: Boolean = false,
        val firstName : String? = null,
        val pkUser : String? = null



        )

    var uiState by mutableStateOf(UIState())

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            newPhoneNumber = savedStateHandle[NEW_PHONE_NUMBER],
            identification = savedStateHandle[IDENTIFICATION],
            userName = savedStateHandle[USER_NAME],
            firstName = savedStateHandle[FIRST_NAME],
            email = savedStateHandle[EMAIL],
            pkUser = savedStateHandle[PK_USER]
        )
    }

    private fun onQuestionOneValueChange(value: Boolean) {
        if (value != uiState.questionOneValue) {
            uiState = uiState.copy(questionOneValue = value)
        }
    }



    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnQuestionOneValueChange -> onQuestionOneValueChange(event.value)
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnContinueButtonClicked -> navigateTo("${Screen.ProfileValidateOTPScreen.baseRoute}/${uiState.identification}/${uiState.firstName}/${uiState.email}/${uiState.phoneNumber}/${uiState.pkUser}/${uiState.idBrand}/${uiState.userName}")

        }
    }

    sealed class UIEvent {

        data class OnQuestionOneValueChange(val value: Boolean) : UIEvent()
        object OnContinueButtonClicked : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}