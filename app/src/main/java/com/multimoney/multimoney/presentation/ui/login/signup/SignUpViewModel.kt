package com.multimoney.multimoney.presentation.ui.login.signup

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.SignUpStep.Search
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCase
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCountryCountryCodeValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFirstLastNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFirstNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnHidePasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNationalityValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOnFidoVerifiedChanged
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenSplashComeBack
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPhoneVerifiedChanged
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSecondLastNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSecondNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSharedIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnShowPasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUpdateUserNames
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUseDataValueChange
import com.multimoney.multimoney.presentation.util.SIM_CODE_EL_SALVADOR
import com.multimoney.multimoney.presentation.util.SIM_CODE_GUATEMALA
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CognitoErrorCode
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SignUpViewModel @Inject constructor(
    private val mutationUpdateUserRegisterUseCase: MutationUpdateUserRegisterUseCase,
    private val queryCountryContactUseCase: QueryCountryContactUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var isOnFidoVerified = true
    var isPhoneVerified = false
    var userData: UserData? = null
    var idBrand: Int? = null
    var strIdIdentification = ""
    var countryCode = ""
    var nextAction: () -> Unit = {}
    private var pass = ""
    private var nextStep: Int = SignUpStep.One.id
    private var previousStep: Int = SignUpStep.One.id
    var whatsAppLink: String? = ""

    private fun nextStep() {
        if (nextStep <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep
            )
        } else {
            completedProcessAction()
        }
    }

    private fun previousStep() {
        if (previousStep > SignUpStep.One.id || uiState.currentStep == SignUpStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep
            )
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    private fun onExit() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpScreen.route
        )
    }

    private fun navigateToSplashComeBack(step: Int) {
        navigateTo("${Screen.SignUpSplashComeBackScreen.baseRoute}/".plus(step).plus("/$idBrand"))
    }

    private fun moveToStep(step: Int) {
        if (step <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step
            )
        }
    }

    private fun completedProcessAction() {
        registerAdjustEvent(
            adjustEventType = AdjustEventType.SIGNUP_SUCCESS_2008,
            isLoggedIn = false,
            data = userData?.toJson() ?: "",
            applyAdjust = false
        )
        popAndNavigateTo(
            route = "${Screen.SignUpCompleted.baseRoute}/${userData?.email}/$pass",
            popTo = Screen.SignUpScreen.route
        )
    }

    private fun onPhoneNumberChange(phoneNumber: String) {
        userData?.phoneNumber = phoneNumber
    }

    private fun onCountryCodeChange(
        countryCode: String,
        countryPhoneCode: String,
        isResetPhoneNumber: Boolean
    ) {
        userData?.let {
            it.countryCode = countryPhoneCode
            it.phoneNumber = if (isResetPhoneNumber) {
                null
            } else {
                it.phoneNumber
            }
        }
        this.countryCode = countryCode
        this.idBrand = Brand.Search.getIdBrandByCountryCode(countryCode)
        onGetWhatsAppLink()
    }

    private fun onNationalityChange(nationality: String, idBrand: Int) {
        this.idBrand = idBrand
        userData = userData?.copy(
            nationality = nationality,
            identification = "",
            firstName = "",
            secondName = "",
            firstLastName = "",
            secondLastName = "",
            fullName = ""
        )
    }

    private fun callMutationUpdateUserRegisterUseCase() = executeUseCase {
        mutationUpdateUserRegisterUseCase.invoke(
            pkUser = userData?.pkUser ?: "",
            user = userData?.email ?: "",
            email = userData?.email ?: "",
            phoneNumber = userData?.phoneNumber,
            fullName = userData?.fullName,
            firstName = userData?.firstName,
            secondName = userData?.secondName,
            lastName = userData?.firstLastName,
            secondLastName = userData?.secondLastName,
            nationality = userData?.nationality,
            identification = userData?.identification,
            countryCode = userData?.countryCode,
            currentStep = userData?.currentStep ?: "",
            idBrand = idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                nextStep = Search.getIdByName(it?.currentStep)
                onUIEvent(OnLoadingValueChange(false))
                onUIEvent(OnUseDataValueChange(it))
                onUIEvent(OnNextStep)
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = if (idBrand == Brand.CostaRica.id) string.sign_up_general_close_dialog_title_costa_rica else string.sign_up_general_close_dialog_title,
                descriptionResource = if (idBrand == Brand.CostaRica.id) string.sign_up_close_dialog_description_costa_rica else string.sign_up_close_dialog_description_el_salvador,
                positiveResource = string.sign_up_close_dialog_positive_button_text,
                negativeResource = string.sign_up_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = Screen.SignInScreen.route,
                        popTo = Screen.SignUpScreen.route
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onShowPasswordBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHidePasswordBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onSetIdBrand(idBrand: Int) {
        this.idBrand = idBrand
    }

    fun logEvents(fireBaseEvents: FireBaseEvents?, adjustEventType: AdjustEventType) {
        fireBaseEvents?.let {
            provideFireBaseEventHelper.logEvent(it)
        }
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit =
        when (adjustEventType) {
            AdjustEventType.SIGNUP_1_2001 -> {
                suspend {
                    if (dataStorePreferences.isAdjustSingUp1EventRegister().first()) {
                        registerAdjustEvent(
                            adjustEventType = AdjustEventType.SIGNUP_1_2001,
                            isLoggedIn = false,
                            data = userData?.toJson() ?: ""
                        )
                        dataStorePreferences.isAdjustSingUp1EventRegister(false)
                    }
                }
            }
            AdjustEventType.SIGNUP_2_2002 -> {
                suspend {
                    if (dataStorePreferences.isAdjustSingUp2EventRegister().first()) {
                        registerAdjustEvent(
                            adjustEventType = AdjustEventType.SIGNUP_2_2002,
                            isLoggedIn = false,
                            data = userData?.toJson() ?: ""
                        )
                        dataStorePreferences.isAdjustSingUp2EventRegister(false)
                    }
                }
            }
            AdjustEventType.SIGNUP_3_2003 -> {
                suspend {
                    if (dataStorePreferences.isAdjustSingUp3EventRegister().first()) {
                        registerAdjustEvent(
                            adjustEventType = AdjustEventType.SIGNUP_3_2003,
                            isLoggedIn = false,
                            data = userData?.toJson() ?: ""
                        )
                        dataStorePreferences.isAdjustSingUp3EventRegister(false)
                    }
                }
            }
            AdjustEventType.SIGNUP_4_2004 -> {
                suspend {
                    if (dataStorePreferences.isAdjustSingUp4EventRegister().first()) {
                        registerAdjustEvent(
                            adjustEventType = AdjustEventType.SIGNUP_4_2004,
                            isLoggedIn = false,
                            data = userData?.toJson() ?: ""
                        )
                        dataStorePreferences.isAdjustSingUp4EventRegister(false)
                    }
                }
            }
            AdjustEventType.SIGNUP_5_2007 -> {
                suspend {
                    if (dataStorePreferences.isAdjustSingUp5EventRegister().first()) {
                        registerAdjustEvent(
                            adjustEventType = AdjustEventType.SIGNUP_5_2007,
                            isLoggedIn = false,
                            data = userData?.toJson() ?: ""
                        )
                        dataStorePreferences.isAdjustSingUp5EventRegister(false)
                    }
                }
            }
            AdjustEventType.SECURITY_SIGN_UP_CHANGE_DEVICE_9001 -> {
                suspend {
                    registerAdjustEvent(
                        adjustEventType = AdjustEventType.SECURITY_SIGN_UP_CHANGE_DEVICE_9001,
                        isLoggedIn = false,
                        data = userData?.toJson() ?: "",
                        applyAdjust = false
                    )
                }
            }
            else -> suspend {}
        }

    private fun navigateToRegisteredUser(userData: UserData?) {
        onChangeRestartEvent(true)
        navigateTo(
            route = Screen.RegisteredUserOtpOptionsScreen.baseRoute
                .plus(
                    getNavParam(PREVIOUS_SCREEN, Screen.SignUpScreen.baseRoute)
                )
                .plus(
                    getNavParam(ID_BRAND, userData?.idBrand ?: 0)
                )
                .plus(
                    getNavParam(USER_DATA, encodeData(userData))
                )
        )
    }

    private fun onChangeRestartEvent(shouldBeOnRestart: Boolean) {
        uiState = uiState.copy(
            shouldChangeOnRestart = shouldBeOnRestart
        )
    }

    private fun onGetWhatsAppLink() {
        executeUseCase {
            queryCountryContactUseCase.invoke(
                user = SignInViewModel.GUEST_USER,
                idBrand = idBrand ?: Brand.Default.id
            ).collectLatest { result ->
                result.onSuccess { contactInfo ->
                    whatsAppLink = contactInfo?.whatsappLink ?: ""
                }
            }
        }
    }

    fun getOnUserDataValidationMessageDialog(userData: UserData?, context: Context) =
        if (userData?.status == CognitoErrorCode.BlacklistedDevice.code.toIntOrNull()) {
            val country = context.getUserCountry()
            DialogParameters(
                titleResource = if (country == SIM_CODE_EL_SALVADOR || country == SIM_CODE_GUATEMALA) {
                    string.sign_up_session_blacklisted_title
                } else {
                    string.sign_up_session_blacklisted_title_cr
                },
                descriptionResource = if (country == SIM_CODE_EL_SALVADOR || country == SIM_CODE_GUATEMALA) {
                    string.sign_up_session_blacklisted_message_sv
                } else {
                    string.sign_up_session_blacklisted_message_cr
                },
                positiveResource = string.sign_in_session_blacklisted_contact_support,
                positiveAction = {
                    whatsAppLink?.let { context.openWhatsAppDeepLink(it) }
                },
                isActive = mutableStateOf(true)
            )
        } else {
            DialogParameters(
                title = userData?.message.orEmpty(),
                description = userData?.detail.orEmpty(),
                isActive = mutableStateOf(true),
                positiveResource = if (userData?.status == STATUS_EMAIL_OR_PHONE_EMPTY) string.contact_support else string.accept,
                positiveAction = {
                    if (userData?.status == STATUS_EMAIL_OR_PHONE_EMPTY) {
                        whatsAppLink?.let { context.openWhatsAppDeepLink(it) }
                    }
                }
            )
        }

    data class UIState(
        // Interactions
        val currentStep: Int = SignUpStep.One.id,
        val isCloseVisible: Boolean = true,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden
        ),
        val country: String = "",
        val shouldChangeOnRestart: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSetNavigation -> onSetNavigation(
                event.nextAction,
                event.nextStep,
                event.previousStep
            )
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog ->
                uiState = uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnNextStep -> nextStep()
            is OnUseDataValueChange -> {
                if (event.idBrand != null) {
                    idBrand = event.idBrand
                }
                userData = event.userData
            }
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
            is OnPhoneNumberValueChange -> onPhoneNumberChange(event.phoneNumber)
            is OnSharedIdentificationValueChange ->
                userData = userData?.copy(identification = event.identificationValue)
            is OnNationalityValueChange -> onNationalityChange(event.nationality, event.idBrand)
            is OnCountryCountryCodeValueChange -> onCountryCodeChange(
                event.countryCode,
                event.countryPhoneCode,
                event.isResetPhoneNumber
            )
            is OnCallMutationUpdateUserRegisterUseCase -> callMutationUpdateUserRegisterUseCase()
            is OnFirstNameValueChange -> userData?.firstName = event.firstName
            is OnSecondNameValueChange -> userData?.secondName = event.secondName
            is OnFirstLastNameValueChange -> userData?.firstLastName = event.firstLastName
            is OnSecondLastNameValueChange -> userData?.secondLastName = event.secondLastName
            is OnUpdateUserNames -> userData = userData?.copy(
                fullName = event.fullName,
                firstName = event.firstName,
                secondName = event.secondName,
                firstLastName = event.firstLastName,
                secondLastName = event.secondLastName
            )
            is OnOpenSplashComeBack -> navigateToSplashComeBack(event.step)
            is OnPhoneVerifiedChanged -> isPhoneVerified = event.isPhoneVerified
            is OnOnFidoVerifiedChanged -> isOnFidoVerified = event.isOnFidoVerified
            is UIEvent.OnShowCloseIcon -> uiState = uiState.copy(isCloseVisible = event.showIcon)
            is OnHidePasswordBottomSheet -> onHidePasswordBottomSheet()
            is OnShowPasswordBottomSheet -> onShowPasswordBottomSheet()
            is UIEvent.OnSetIdBrand -> onSetIdBrand(event.idBrand)
            is UIEvent.OnExit -> onExit()
            is UIEvent.OnUpdateCountry -> {
                uiState = uiState.copy(country = event.country)
                idBrand = Brand.Search.getIdBrandByCountryCode(event.country)
            }
            is UIEvent.OnUpdatePassword -> pass = event.pass
            is UIEvent.OnCheckIfEmailExists -> navigateToRegisteredUser(event.userData)
            is UIEvent.OnChangeRestartEvent -> onChangeRestartEvent(event.shouldBeOnRestart)
            is UIEvent.OnGetWhatsAppLink -> onGetWhatsAppLink()
        }
    }

    sealed class UIEvent {
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        data class OnUseDataValueChange(val userData: UserData?, val idBrand: Int? = null) :
            UIEvent()

        data class OnMoveToStep(val step: Int) : UIEvent()
        data class OnSharedIdentificationValueChange(val identificationValue: String) : UIEvent()
        data class OnPhoneNumberValueChange(val phoneNumber: String) : UIEvent()
        data class OnNationalityValueChange(val nationality: String, val idBrand: Int) : UIEvent()
        data class OnCountryCountryCodeValueChange(
            val countryCode: String,
            val countryPhoneCode: String,
            val isResetPhoneNumber: Boolean
        ) : UIEvent()

        data class OnFirstNameValueChange(val firstName: String) : UIEvent()
        data class OnSecondNameValueChange(val secondName: String) : UIEvent()
        data class OnFirstLastNameValueChange(val firstLastName: String) : UIEvent()
        data class OnSecondLastNameValueChange(val secondLastName: String) : UIEvent()
        data class OnUpdateUserNames(
            val fullName: String,
            val firstName: String,
            val secondName: String,
            val firstLastName: String,
            val secondLastName: String
        ) : UIEvent()

        data class OnOpenSplashComeBack(val step: Int) : UIEvent()
        data class OnPhoneVerifiedChanged(val isPhoneVerified: Boolean) : UIEvent()
        data class OnOnFidoVerifiedChanged(val isOnFidoVerified: Boolean) : UIEvent()

        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()

        object OnCallMutationUpdateUserRegisterUseCase : UIEvent()
        data class OnShowCloseIcon(val showIcon: Boolean) : UIEvent()
        object OnHidePasswordBottomSheet : UIEvent()
        object OnShowPasswordBottomSheet : UIEvent()
        data class OnSetIdBrand(val idBrand: Int) : UIEvent()
        object OnExit : UIEvent()
        data class OnUpdateCountry(val country: String) : UIEvent()
        data class OnUpdatePassword(val pass: String) : UIEvent()
        data class OnCheckIfEmailExists(val userData: UserData?) : UIEvent()
        data class OnChangeRestartEvent(val shouldBeOnRestart: Boolean) : UIEvent()
        object OnGetWhatsAppLink : UIEvent()
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 6
        const val SIGN_UP_INDICATOR_TOTAL_STEPS = 5
        const val STATUS_EMAIL_OR_PHONE_EMPTY = 3108
    }
}
