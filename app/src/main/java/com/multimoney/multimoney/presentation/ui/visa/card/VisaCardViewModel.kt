package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.activity.result.ActivityResult
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
import com.multimoney.domain.interaction.virtualcard.MutationCardBlockingUseCase
import com.multimoney.domain.interaction.virtualcard.MutationCardUnblockingUseCase
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.BaseEvent.OnOpenNfcConfig
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.BaseEvent.OnOpenTapAndPayConfig
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnAvailableAmountClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnBlockUnblockCardClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnCallNovoGetFavoriteCard
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHandleTapAndPayIntentResult
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHidePasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnInitializeBiometricPrompt
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigatePreferences
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateToVisaTokenizationScreen
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnOpenDialogConfirmToStartTokenizationProcess
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordChange
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordConfirmClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordForgotPassword
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnSeeDataClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnShowPasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnStartPaymentProcess
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnTryWithPassword
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.CardType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.util.BiometricHelper
import com.multimoney.multimoney.util.CognitoHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class VisaCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val countDownTimer: MMCountDownTimer,
    val nfcHelper: NfcHelper,
    private val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences,
    private val cognitoHelper: CognitoHelper,
    private val mutationCardBlockingUseCase: MutationCardBlockingUseCase,
    private val mutationCardUnblockingUseCase: MutationCardUnblockingUseCase
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
    var balanceCardInformation: BalanceCardInformation? = null
    var availableBalanceLabel: String? = null
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var isBiometricActive = false
    private var password = ""
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""
    private var passwordAttempts = INIT_PASSWORD_ATTEMPTS
    private var isNavigateBackRefresh = false
    private var isSignOut = false

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND)?.toInt() ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
    }

    private fun onStart() {
        blockUnblock(balanceCardInformation?.status == CardType.Blocked.status)
        viewModelScope.launch {
            isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
        }
    }

    private fun blockUnblock(isCardBlocked: Boolean) {
        uiState = uiState.copy(
            isLoading = false,
            isCardBlocked = isCardBlocked,
            blockUnblockButtonText = if (isCardBlocked) {
                string.unlocked
            } else {
                string.locked
            },
            blockUnblockButtonIcon = if (isCardBlocked) {
                R.drawable.ic_unlocked
            } else {
                R.drawable.ic_locked
            }
        )
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

    fun deviceHasNFC() = nfcHelper.isNfcSupported()

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
            isNfcAvailable = deviceHasNFC(),
            isCardTokenize = NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY && NovoVTS.getFavoriteCard() != NOVO_CARD_TOKEN_EMPTY_TWO
        )
    }

    private fun startPaymentProcess() {
        if (nfcHelper.isNfcEnabled()) {
            checkIfMultimoneyIsTheDefaultPaymentMethod()
        } else {
            uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = if (idBrand == Guatemala.id) string.visa_nfc_required_dialog_title_gt else string.visa_nfc_required_dialog_title,
                    descriptionResource = string.visa_nfc_required_dialog_description,
                    positiveResource = string.activate,
                    negativeResource = string.cancel,
                    positiveAction = { emitBaseEvent(OnOpenNfcConfig) },
                    isActive = mutableStateOf(true)
                )
            )
        }
    }

    private fun checkIfMultimoneyIsTheDefaultPaymentMethod() {
        if (NovoVTS.isDefaultPaymentService()) {
            // todo start Payment Process
        } else {
            uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = string.visa_multimoney_default_payment_method_required_dialog_title,
                    descriptionResource = string.visa_nfc_required_dialog_description,
                    positiveResource = string.select,
                    negativeResource = string.cancel,
                    positiveAction = { emitBaseEvent(OnOpenTapAndPayConfig) },
                    isActive = mutableStateOf(true)
                )
            )
        }
    }

    private fun onHandleTapAndPayIntentResult(result: ActivityResult) {
        if (nfcHelper.isNfcEnabled() && NovoVTS.isDefaultPaymentService()) {
            // todo start Payment Process
        }
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

    private fun onPasswordForgotPassword() = navigateTo(
        route = Screen.RequestForgotPassword.baseRoute
            .plus(
                getNavParam(PREVIOUS_SCREEN, Screen.VisaCardScreen.route)
            )
    )

    private fun onShowPasswordBottomSheet(isPasswordMessage: Boolean) {
        uiState = uiState.copy(
            password = "",
            isPasswordConfirmButtonEnabled = false,
            isPasswordMessage = isPasswordMessage,
            passwordTitle = if (isPasswordMessage) {
                string.visa_card_password_message_title
            } else {
                string.visa_card_password_title
            },
            bottomSheetVisibleState = ModalBottomSheetState(Expanded)
        )
    }

    private fun onHidePasswordBottomSheet() {
        if (isSignOut) {
            signOut()
        }
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(Hidden))
    }

    private fun onTryWithPassword() {
        uiState = uiState.copy(isPasswordMessage = false, passwordTitle = string.visa_card_password_title)
    }

    private fun callCognitoSignIn() {
        uiState = uiState.copy(isLoading = true)
        Amplify.Auth.signOut({
            isSignOut = true
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
                                    isSignOut = false
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

    private fun onCallMutationCardBlockingUseCase() = executeUseCase {
        mutationCardBlockingUseCase.invoke(
            blockType = CardType.Blocked.blockType,
            observations = CardType.Blocked.observation,
            idClient = idClient,
            userApp = email,
            cardToken = balanceCardInformation?.cardInformation?.cardToken ?: "",
            source = CardType.Blocked.source,
            idLoan = idLoanClient,
            user = email,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                isNavigateBackRefresh = true
                blockUnblock(true)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallMutationCardUnblockingUseCase() = executeUseCase {
        mutationCardUnblockingUseCase.invoke(
            observations = CardType.Unblocked.observation,
            idClient = idClient,
            userApp = email,
            cardToken = balanceCardInformation?.cardInformation?.cardToken ?: "",
            source = CardType.Unblocked.source,
            idLoan = idLoanClient,
            user = email,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                isNavigateBackRefresh = true
                blockUnblock(false)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onBlockUnblockCardClick() {
        when {
            uiState.isCardBlocked.not() -> uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = string.visa_card_block_dialog_title,
                    descriptionResource = if (idBrand == Guatemala.id) {
                        string.visa_card_block_dialog_subtitle_gt
                    } else {
                        string.visa_card_block_dialog_subtitle
                    },
                    positiveResource = string.locked,
                    negativeResource = string.cancel,
                    positiveAction = { onCallMutationCardBlockingUseCase() },
                    isActive = mutableStateOf(true)
                ),
                visaCardBlockDisclaimer = if (idBrand == Guatemala.id) {
                    string.visa_card_block_disclaimer_gt
                } else {
                    string.visa_card_block_disclaimer
                }
            )
            uiState.isCardBlocked && uiState.isNfcAvailable.not() && balanceCardInformation?.allowUnLock == true ->
                uiState =
                    uiState.copy(
                        dialogParameters = DialogParameters(
                            titleResource = string.visa_card_unblock_without_nfc_dialog_title,
                            descriptionResource = string.visa_card_unblock_without_nfc_dialog_subtitle,
                            positiveResource = string.unlocked,
                            negativeResource = string.cancel,
                            positiveAction = { onCallMutationCardUnblockingUseCase() },
                            isActive = mutableStateOf(true)
                        )
                    )
            uiState.isCardBlocked && uiState.isNfcAvailable && balanceCardInformation?.allowUnLock == true ->
                uiState =
                    uiState.copy(
                        dialogParameters = DialogParameters(
                            titleResource = string.visa_card_unblock_with_nfc_dialog_title,
                            descriptionResource = string.visa_card_unblock_with_nfc_dialog_subtitle,
                            positiveResource = string.unlocked,
                            negativeResource = string.cancel,
                            positiveAction = { onCallMutationCardUnblockingUseCase() },
                            isActive = mutableStateOf(true)
                        )
                    )
            else -> uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    titleResource = string.visa_card_unblock_not_allowed_dialog_title,
                    descriptionResource = string.visa_card_unblock_not_allowed_dialog_subtitle,
                    positiveResource = string.accept,
                    negativeResource = string.empty,
                    positiveAction = { },
                    isActive = mutableStateOf(true)
                )
            )
        }
    }

    data class UIState(
        // Interactions
        val isCardTextVisible: Boolean = false,
        val isNfcAvailable: Boolean = false,
        val isCardBlocked: Boolean = false,
        val isCardTokenize: Boolean = false,
        val passwordTitle: Int = string.empty,
        val isPasswordMessage: Boolean = false,
        val blockUnblockButtonText: Int = string.locked,
        val blockUnblockButtonIcon: Int = R.drawable.ic_locked,
        val password: String = "",
        val passwordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val isPasswordConfirmButtonEnabled: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
        val dialogParameters: DialogParameters = DialogParameters(),
        val visaCardBlockDisclaimer: Int = R.string.empty,
        val isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCallNovoGetFavoriteCard -> callNovoGetFavoriteCard()
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, isNavigateBackRefresh)
            is OnNavigatePreferences -> navigateTo(Screen.VisaPreferencesScreen.baseRoute)
            is OnAvailableAmountClick -> onAvailableAmountClick()
            is OnNavigateToVisaTokenizationScreen -> navigateTo(
                "${Screen.VisaTokenizationWaitingScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$phone/${
                encodeData(
                    balanceCardInformation
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
            is OnStartPaymentProcess -> startPaymentProcess()
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
            is OnBlockUnblockCardClick -> onBlockUnblockCardClick()
            is OnHandleTapAndPayIntentResult -> onHandleTapAndPayIntentResult(uiEvent.result)
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        data class OnInitializeBiometricPrompt(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String
        ) : UIEvent()

        object OnCallNovoGetFavoriteCard : UIEvent()
        data class OnSeeDataClick(val fragmentActivity: FragmentActivity) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigatePreferences : UIEvent()
        object OnAvailableAmountClick : UIEvent()
        object OnNavigateToVisaTokenizationScreen : UIEvent()
        object OnOpenDialogConfirmToStartTokenizationProcess : UIEvent()
        object OnStartPaymentProcess : UIEvent()
        object OnHidePasswordBottomSheet : UIEvent()
        object OnTryWithPassword : UIEvent()
        object OnBlockUnblockCardClick : UIEvent()
        data class OnShowPasswordBottomSheet(val isPasswordMessage: Boolean) : UIEvent()
        data class OnPasswordChange(val value: String) : UIEvent()
        object OnPasswordConfirmClick : UIEvent()
        object OnPasswordForgotPassword : UIEvent()
        data class OnHandleTapAndPayIntentResult(val result: ActivityResult) : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenTapAndPayConfig : BaseEvent()
        object OnOpenNfcConfig : BaseEvent()
    }

    companion object {
        const val NOVO_CARD_TOKEN_EMPTY = ""
        const val NOVO_CARD_TOKEN_EMPTY_TWO = "-1"
        const val MAX_PASSWORD_ATTEMPTS = 3
        const val INIT_PASSWORD_ATTEMPTS = 0
    }
}
