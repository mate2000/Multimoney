package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.biometric.BiometricPrompt
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.data.util.catalog.Brand.Guatemala
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnAvailableAmountClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHidePasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnInitializeBiometricPrompt
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateToVisaTokenizationScreen
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnOpenDialogConfirmToStartTokenizationProcess
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordChange
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordConfirmClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordForgotPassword
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnSeeDataClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnShowPasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnTryWithPassword
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.BiometricHelper
import com.multimoney.multimoney.util.CognitoHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class VisaCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val countDownTimer: MMCountDownTimer,
    private val nfcHelper: NfcHelper,
    private val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences,
    private val cognitoHelper: CognitoHelper
) :
    BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var pkUser: Long = 0
    var identification: String = ""
    private var email: String = ""
    private var phone: String = ""
    var cardInformation: CardInformation? = null
    var availableBalanceLabel: String? = null
    private var isBiometricActive = false
    private var password = ""
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""
    private var passwordAttempts = INIT_PASSWORD_ATTEMPTS

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND)?.toInt() ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        cardInformation = savedStateHandle.get<CardInformation>(CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
        callNovoGetFavoriteCard()
    }

    private fun onStart() {
        viewModelScope.launch {
            isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
        }
    }

    private fun initializeBiometricPrompt(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
    }

    private fun biometricPromptError(errorCode: Int, errString: CharSequence) {
        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            onShowPasswordBottomSheet(isPasswordMessage = true)
        }
    }

    private fun onShowBiometricPromptForDecryption(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            biometricHelper.showBiometricPrompt(
                title = biometricPromptTitle,
                description = biometricPromptDescription,
                negative = biometricPromptNegative,
                activity = fragmentActivity,
                processSuccess = { result ->
                    biometricPromptForDecryptionSuccess(result)
                },
                processError = { errorCode, errString ->
                    biometricPromptError(errorCode, errString)
                },
                initializationVector = dataStorePreferences.getUserPasswordVector()
                    .first()
            )
        }
    }

    private fun biometricPromptForDecryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                password = dataStorePreferences.getUserPassword(this@apply).first()
                callCognitoSignIn()
            }
        }
    }

    private fun onAvailableAmountClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = string.empty,
                descriptionResource = when (idBrand) {
                    ElSalvador.id -> string.visa_card_sv_dialog_description_available
                    CostaRica.id -> string.visa_card_cr_dialog_description_available
                    Guatemala.id -> string.visa_card_gt_dialog_description_available
                    else -> string.empty
                },
                positiveResource = string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun callNovoGetFavoriteCard() {
        uiState = uiState.copy(
            isNfcAvailable = nfcHelper.isNfcSupported(),
            isCardTokenize = NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY && NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY_TWO
        )
    }

    private fun onSeeDataClick(fragmentActivity: FragmentActivity) {
        if (isBiometricActive) {
            onShowBiometricPromptForDecryption(fragmentActivity)
        } else {
            onShowPasswordBottomSheet(isPasswordMessage = false)
        }
    }

    private fun onPasswordChange(value: String) {
        uiState = uiState.copy(
            password = value,
            isPasswordConfirmButtonEnabled = value.isNotBlank(),
            passwordError = Pair(false, R.string.error_empty)
        )
    }

    private fun onPasswordConfirmClick() {
        password = uiState.password
        callCognitoSignIn()
    }

    private fun onPasswordForgotPassword() {
        // TODO: Move to password flow
    }

    private fun onShowPasswordBottomSheet(isPasswordMessage: Boolean) {
        uiState = uiState.copy(
            password = "",
            isPasswordConfirmButtonEnabled = false,
            isPasswordMessage = isPasswordMessage,
            passwordTitle = if (isPasswordMessage) {
                R.string.visa_card_password_message_title
            } else {
                R.string.visa_card_password_title
            },
            bottomSheetVisibleState = ModalBottomSheetState(Expanded)
        )
    }

    private fun onHidePasswordBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(Hidden))
    }

    private fun onTryWithPassword() {
        uiState = uiState.copy(isPasswordMessage = false, passwordTitle = R.string.visa_card_password_title)
    }

    private fun callCognitoSignIn() {
        uiState = uiState.copy(isLoading = true)
        Amplify.Auth.signOut({
            Amplify.Auth.signIn(email, password, { authSignInResult ->
                if (authSignInResult.isSignInComplete) {
                    Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                        val session = authSessionSuccess as AWSCognitoAuthSession
                        when (session.identityId.type) {
                            AuthSessionResult.Type.SUCCESS -> {
                                viewModelScope.launch {
                                    passwordAttempts = INIT_PASSWORD_ATTEMPTS
                                    dataStorePreferences.setAuthToken(session.userPoolTokens.value?.idToken ?: "")
                                    uiState = uiState.copy(isLoading = false, isCardTextVisible = true)
                                    onHidePasswordBottomSheet()
                                }
                            }
                            AuthSessionResult.Type.FAILURE -> cognitoError()
                        }
                    }, {
                        cognitoError()
                    })
                } else {
                    cognitoError()
                }
            }, {
                cognitoError()
            })
        }, {
            cognitoError()
        })
    }

    private fun cognitoError() {
        passwordAttempts++
        if (passwordAttempts < MAX_PASSWORD_ATTEMPTS) {
            uiState = uiState.copy(
                passwordError = Pair(true, R.string.visa_card_password_error),
                isLoading = false
            )
        } else {
            signOut()
        }
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
        // Interactions
        val isCardTextVisible: Boolean = false,
        val isNfcAvailable: Boolean = false,
        val isCardTokenize: Boolean = false,
        val passwordTitle: Int = R.string.empty,
        val isPasswordMessage: Boolean = false,
        val password: String = "",
        val passwordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val isPasswordConfirmButtonEnabled: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
        val dialogParameters: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnAvailableAmountClick -> onAvailableAmountClick()
            is OnNavigateToVisaTokenizationScreen -> navigateTo(
                "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$phone/${
                encodeData(
                    cardInformation
                )
                }"
            )
            is OnOpenDialogConfirmToStartTokenizationProcess -> uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = string.visa_card_dialog_title,
                    descriptionResource = string.visa_card_dialog_description,
                    positiveResource = string.link,
                    negativeResource = string.cancel,
                    positiveAction = {
                        onUIEvent(OnNavigateToVisaTokenizationScreen)
                    },
                    isActive = mutableStateOf(true)
                )
            )
            is OnStart -> onStart()
            is OnInitializeBiometricPrompt -> initializeBiometricPrompt(
                uiEvent.biometricPromptTitle,
                uiEvent.biometricPromptDescription,
                uiEvent.biometricPromptNegative
            )
            is OnSeeDataClick -> onSeeDataClick(uiEvent.fragmentActivity)
            is OnHidePasswordBottomSheet -> onHidePasswordBottomSheet()
            is OnShowPasswordBottomSheet -> onShowPasswordBottomSheet(uiEvent.isPasswordMessage)
            is OnPasswordChange -> onPasswordChange(uiEvent.value)
            is OnPasswordConfirmClick -> onPasswordConfirmClick()
            is OnPasswordForgotPassword -> onPasswordForgotPassword()
            is OnTryWithPassword -> onTryWithPassword()
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        data class OnInitializeBiometricPrompt(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String
        ) : UIEvent()

        data class OnSeeDataClick(val fragmentActivity: FragmentActivity) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnAvailableAmountClick : UIEvent()
        object OnNavigateToVisaTokenizationScreen : UIEvent()
        object OnOpenDialogConfirmToStartTokenizationProcess : UIEvent()
        object OnHidePasswordBottomSheet : UIEvent()
        object OnTryWithPassword : UIEvent()
        data class OnShowPasswordBottomSheet(val isPasswordMessage: Boolean) : UIEvent()
        data class OnPasswordChange(val value: String) : UIEvent()
        object OnPasswordConfirmClick : UIEvent()
        object OnPasswordForgotPassword : UIEvent()
    }

    companion object {
        const val NOVO_CARD_TOKEN_EMPTY = ""
        const val NOVO_CARD_TOKEN_EMPTY_TWO = "-1"
        const val MAX_PASSWORD_ATTEMPTS = 3
        const val INIT_PASSWORD_ATTEMPTS = 0
    }
}
