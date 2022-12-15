package com.multimoney.multimoney.presentation.ui.smart

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartStatus
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCase
import com.multimoney.domain.interaction.accountsmart.MutationInitialRequestUseCase
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCase
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationInitialRequest
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallSaveAutomatedSmartAccount
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnClickBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseAlertClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCtaAlertClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnOnFidoVerifiedChanged
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.util.ISO_8601_API_FORMAT_PATTERN
import com.multimoney.multimoney.presentation.util.YEAR_MONTH_DAY_PATTERN
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateString
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class SmartViewModel @Inject constructor(
    private val queryStepByStepUseCase: QueryStepByStepUseCase,
    private val mutationGlobalRequestUseCase: MutationGlobalRequestUseCase,
    private val mutationInitialRequestUseCase: MutationInitialRequestUseCase,
    private val mutationSaveSmartAccount: MutationSaveAutomatedSmartAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // bundle parameters
    val pkUser = savedStateHandle[PK_USER] ?: ""
    val idBrand = savedStateHandle[ID_BRAND] ?: ""
    val user = savedStateHandle[USER] ?: ""
    val comingFromCrypto = savedStateHandle[COMING_FROM_CRYPTO] ?: ""
    val idBrandAsInt = idBrand.toIntOrNull() ?: DEFAULT_ID_BRAND_ERROR
    val identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    val email: String = savedStateHandle[EMAIL] ?: ""
    val firstName: String = savedStateHandle[FIRST_NAME] ?: ""
    val lastName: String = savedStateHandle[LAST_NAME] ?: ""

    // Stateless
    var nextAction: () -> Unit = {}
    private var overridePreviousAction: (() -> Unit)? = null
    private var closeDialogDescription: String = ""
    var accountSmartData: AccountSmartData? = null
    private var nextStep: Int = SmartSteps.One.id
    private var previousStep: Int = SmartSteps.One.id
    var globalRequestId = 0
    private var idSysRequest: Long = 0
    var isOnFidoVerified = true

    // UIState
    var uiState by mutableStateOf(UIState())

    init {
        accountSmartData = AccountSmartData(
            pkUser = pkUser,
            idBrand = idBrandAsInt,
            user = user
        )
    }

    private fun callQueryStepByStepUseCase() = executeUseCase {
        queryStepByStepUseCase.invoke(
            user = accountSmartData?.user ?: "",
            idBrand = accountSmartData?.idBrand ?: 0,
            idRequest = globalRequestId
        ).collectLatest { result ->
            result.onSuccess { stepByStep ->
                stepByStep?.let {
                    Timber.d( "callQueryStepByStepUseCase(): $it")
                    navigateToScreenOnStepFetched(it)
                }
                onUIEvent(OnLoadingValueChange(false))
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

    /**
     * this function will collect the data coming from the backend and will be set on the
     * AccountSmartData object, then an event will be sent to the smart origination step
     * by step screens in order to preload the data on the current UI and also to navigate
     * to the right step.
     */
    private fun navigateToScreenOnStepFetched(stepByStep: StepByStep) {
        // setting up the data coming from the backend
        accountSmartData = AccountSmartData(
            pkUser = pkUser,
            status = SmartStatus.Search.getIdByName(stepByStep.statusRequest),
            idProfessionType = stepByStep.idProfessionType,
            idCivilStatusType = stepByStep.idMaritalStatus?.toLong(),
            birthday = stepByStep.birthdate.orEmpty(),
            expirationDate = stepByStep.expirationDate,
            idGender = stepByStep.idGenre?.toLong(),
            strGenre = stepByStep.strGenre,
            stringProfessionType = stepByStep.stringProfessionType,
            strMaritalStatus = stepByStep.strMaritalStatus,
            idAddressLevel1 = stepByStep.idAddressLevel1?.toLong(),
            idAddressLevel2 = stepByStep.idAddressLevel2?.toLong(),
            idAddressLevel3 = stepByStep.idAddressLevel3?.toLong(),
            strAddressLevel1 = stepByStep.strAddressLevel1,
            strAddressLevel2 = stepByStep.strAddressLevel2,
            strAddressLevel3 = stepByStep.strAddressLevel3,
            positionJob = stepByStep.positionJob,
            idEconomicActivity = stepByStep.idEconomicActivity?.toLong(),
            institutionPension = stepByStep.institutionalPesion.orEmpty(),
            income = stepByStep.income,
            addressDetail = stepByStep.addressDetail,
            user = user,
            idBrand = idBrandAsInt,
            currentStep = stepByStep.currentStep,
            aboutCompany = stepByStep.aboutCompany,
            companyName = stepByStep.nameCompany,
            specifiesIncomeSource = stepByStep.specifiesIncomeSource,
            listBeneficiaries = stepByStep.beneficiary.orEmpty(),
            entrepreneurship = stepByStep.entrepreneurship.orEmpty(),
            legalID = stepByStep.legalID,
            isPEP = stepByStep.isPEP,
            isUSCitizen = stepByStep.isUSCitizen,
            isActivityOfArt15 = stepByStep.isActivityOfArt15,
            isUSTaxPayer = stepByStep.isUSTaxPayer,
            isTaxPayer = stepByStep.isTaxPayer,
            idJobLevel1 = stepByStep.idJobLevel1,
            idJobLevel2 = stepByStep.idJobLevel2,
            idJobLevel3 = stepByStep.idJobLevel3,
            fullJobAddress = stepByStep.fullJobAddress
        )

        // update the current step coming from the backend in order to navigate to the proper screen
        uiState = uiState.copy(currentStep = SmartSteps.Search.getIdByName(stepByStep.currentStep))
    }

    private fun callMutationInitialRequestUseCase() = executeUseCase(
        action = {
            mutationInitialRequestUseCase.invoke(
                pkUser = accountSmartData?.pkUser?.toInt()?.toLong() ?: 0,
                idBrand = accountSmartData?.idBrand ?: 0,
                user = accountSmartData?.user ?: ""
            ).collectLatest { result ->
                result.onSuccess {
                    globalRequestId = it?.idGlobalRequest ?: 0
                    onUIEvent(OnLoadingValueChange(false))
                    callQueryStepByStepUseCase()
                }
                result.onFailure {
                    onUIEvent(OnLoadingValueChange(false))
                    uiState = uiState.copy(
                        isAlertResultVisible = true,
                        alertResultDescription = it.getError()
                    )
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        }
    )

    private fun callMutationGlobalRequestUseCase(isLastStep: Boolean = false) = executeUseCase(
        action = {
            mutationGlobalRequestUseCase.invoke(
                pkUser = accountSmartData?.pkUser?.toInt() ?: 0,
                status = accountSmartData?.status ?: 0,
                idProfessionType = accountSmartData?.idProfessionType ?: 0,
                idAddressLevel1 = accountSmartData?.idAddressLevel1 ?: 0,
                idAddressLevel2 = accountSmartData?.idAddressLevel2 ?: 0,
                idAddressLevel3 = accountSmartData?.idAddressLevel3 ?: 0,
                positionJob = accountSmartData?.positionJob ?: "",
                idEconomicActivity = accountSmartData?.idEconomicActivity ?: 0,
                income = accountSmartData?.income?.toDouble() ?: 0.0,
                addressDetail = accountSmartData?.addressDetail ?: "",
                user = accountSmartData?.user ?: "",
                idBrand = accountSmartData?.idBrand ?: 0,
                currentStep = accountSmartData?.currentStep ?: "",
                idCivilStatusType = accountSmartData?.idCivilStatusType ?: 0,
                birthday = accountSmartData?.birthday?.ifEmpty {
                    getFormatDateByString(
                        getCurrentDateString(),
                        YEAR_MONTH_DAY_PATTERN,
                        ISO_8601_API_FORMAT_PATTERN
                    )
                }.orEmpty(),
                expirationDate = accountSmartData?.expirationDate?.ifEmpty {
                    getFormatDateByString(
                        getCurrentDateString(),
                        YEAR_MONTH_DAY_PATTERN,
                        ISO_8601_API_FORMAT_PATTERN
                    )
                }.orEmpty(),
                idGender = accountSmartData?.idGender ?: 0,
                companyName = accountSmartData?.companyName.orEmpty(),
                fullJobAddress = accountSmartData?.fullJobAddress.orEmpty(),
                aboutCompany = accountSmartData?.aboutCompany.orEmpty(),
                institutionPension = accountSmartData?.institutionPension.orEmpty(),
                specifiesIncomeSource = accountSmartData?.specifiesIncomeSource ?: "",
                entrepreneurship = accountSmartData?.entrepreneurship ?: "",
                legalID = accountSmartData?.legalID ?: "",
                isActivityOfArt15 = accountSmartData?.isActivityOfArt15 ?: false,
                isUSCitizen = accountSmartData?.isUSCitizen ?: false,
                isPEP = accountSmartData?.isPEP ?: false,
                isUSTaxPayer = accountSmartData?.isUSTaxPayer ?: false,
                isTaxPayer = accountSmartData?.isTaxPayer ?: false,
                beneficiaries = accountSmartData?.listBeneficiaries.orEmpty(),
                idJobLevel2 = accountSmartData?.idJobLevel2 ?: 0,
                idJobLevel3 = accountSmartData?.idJobLevel3 ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    if (isLastStep) {
                        idSysRequest = it?.idSysRequest?.toLong() ?: 0L
                        globalRequestId = it?.idGlobalRequest ?: 0
                        callMutationSaveSmartAccount()
                    } else {
                        onUIEvent(OnLoadingValueChange(false))
                        onUIEvent(OnNextStep)
                    }
                }
                result.onFailure {
                    onUIEvent(OnLoadingValueChange(false))
                    uiState = uiState.copy(
                        isAlertResultVisible = true,
                        alertResultDescription = it.getError()
                    )
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        },
        noInternetAction = {
            uiState = uiState.copy(
                isAlertResultVisible = true,
                alertResultTitle = null,
                alertResultDescription = null
            )
        }
    )

    private suspend fun callMutationSaveSmartAccount() {
        mutationSaveSmartAccount.invoke(
            user = user,
            idBrand = idBrandAsInt,
            identificationNumber = identification,
            idRequest = globalRequestId.toLong()
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
                onUIEvent(OnNextStep)
            }
            result.onFailure { error ->
                onUIEvent(OnLoadingValueChange(false))
                uiState = uiState.copy(
                    isAlertResultVisible = true,
                    alertResultDescription = error.getError()
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    /**
     * After last step of origination (Before OnFido/Evicertia)
     * Saves account smart
     */
    private fun onCallMutationSaveSmartAccount(accountData: AccountSmartData?) {
        accountSmartData = accountData
        callMutationGlobalRequestUseCase(true)
    }

    /**
     * Each country has different smart origination flow, so the total
     * of screen is different on both. This function will return the total
     * of pages based on the country id.
     */
    fun getTotalStepperCounter(): Int {
        val counter = if (idBrandAsInt == Brand.ElSalvador.id) {
            SMART_INDICATOR_SV_TOTAL_STEPS
        } else {
            SMART_INDICATOR_CR_TOTAL_STEPS
        }
        return counter
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = string.smart_close_origination_dialog_title,
                description = closeDialogDescription,
                positiveResource = string.common_leave,
                negativeResource = string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    /**
     * close the alert screen after tapping on the cta button and
     * re-execute the OnContinueClick action from the current step
     */
    private fun onCtaAlertClick(focusManager: FocusManager) {
        uiState = uiState.copy(isAlertResultVisible = false)
        onUIEvent(OnContinueClick(focusManager))
    }

    /**
     * send the user to the home screen, without saving the actual step
     */
    private fun onCloseAlertClick() {
        uiState = uiState.copy(isAlertResultVisible = false)
        navigateBackToHome()
    }

    /**
     * due to the internal navigation present on the step tree (economical activity options)
     * we need to override the previous back button action on each of the internal screens in order to
     * return to this main options, the "overridePreviousAction" MUST not come null in that case,
     * otherwise, the normal step navigation logic will be executed.
     */
    private fun previousStep() {
        if (overridePreviousAction != null) {
            overridePreviousAction?.invoke()
        } else {
            if (previousStep > SmartSteps.One.id || uiState.currentStep == SmartSteps.Two.id) {
                uiState = uiState.copy(
                    currentStep = previousStep,
                    isCloseVisible = previousStep >= SmartSteps.One.id
                )
            } else {
                navigateBackToHome()
            }
        }
    }

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartScreen.route
        )
    }

    private fun nextStep() {
        if (nextStep <= getTotalStepperCounter()) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= SmartSteps.One.id
            )
        } else {
            navigateToOnfido()
        }
    }

    private fun navigateToOnfido() {
        popAndNavigateTo(
            "${Screen.SmartOnfidoScreen.baseRoute}/$user/$idBrand/$pkUser/$identification/$email/$firstName/$lastName/$idSysRequest/$globalRequestId/${URL_EMPTY}",
            Screen.SmartScreen.route
        )
    }

    private fun onSetNavigation(
        nextAction: () -> Unit,
        overridePreviousAction: (() -> Unit)?,
        nextStep: Int,
        previousStep: Int
    ) {
        this.nextAction = nextAction
        this.overridePreviousAction = overridePreviousAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onClickBottomSheet() {
        uiState = if (uiState.bottomSheetState.isVisible) {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
            )
        } else {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        }
    }

    /**
     * this function will update the accountSmartData object with the new data coming from
     * the child screen after tapping on the "continue" button, also it will trigger the
     * API call by calling the callMutationGlobalRequestUseCase() function.
     */
    private fun onUpdateAccountSmartData(accountData: AccountSmartData?) {
        accountSmartData = accountData
        callMutationGlobalRequestUseCase()
    }

    private fun onInitializeTexts(description: String) {
        closeDialogDescription = description
    }

    data class UIState(
        // Interactions
        val currentStep: Int = SmartSteps.One.id,
        val isCloseVisible: Boolean = true,
        val isContinueEnabled: Boolean = false,
        val buttonTextRes: Int = string.button_continue,
        val isLoading: Boolean = false,
        val isContinueVisible: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String? = null,
        val alertResultDescription: String? = null,
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {}
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnClickBottomSheet -> onClickBottomSheet()
            is OnSetNavigation -> onSetNavigation(
                event.nextAction,
                event.overridePreviousAction,
                event.nextStep,
                event.previousStep
            )
            is OnInitializeText -> onInitializeTexts(event.description)
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnCloseAlertClick -> onCloseAlertClick()
            is OnCtaAlertClick -> onCtaAlertClick(event.focusManager)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnNextStep -> nextStep()
            is OnPreviousStep -> previousStep()
            is OnContinueVisible ->
                uiState =
                    uiState.copy(isContinueVisible = event.visible, buttonTextRes = event.textResId)
            is OnCallMutationUpdateGlobalRequestUseCase -> onUpdateAccountSmartData(event.accountSmartData)
            is OnCallMutationInitialRequest -> callMutationInitialRequestUseCase()
            is OnOnFidoVerifiedChanged -> isOnFidoVerified = event.isOnFidoVerified
            is OnCallSaveAutomatedSmartAccount -> onCallMutationSaveSmartAccount(event.accountSmartData)
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val description: String) : UIEvent()
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnCtaAlertClick(val focusManager: FocusManager) : UIEvent()
        object OnCloseAlertClick : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val overridePreviousAction: (() -> Unit)? = null,
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        data class OnContinueVisible(
            val visible: Boolean,
            val textResId: Int = string.button_continue
        ) : UIEvent()

        data class OnCallMutationUpdateGlobalRequestUseCase(val accountSmartData: AccountSmartData?) :
            UIEvent()

        data class OnOnFidoVerifiedChanged(val isOnFidoVerified: Boolean) : UIEvent()

        data class OnCallSaveAutomatedSmartAccount(val accountSmartData: AccountSmartData?) :
            UIEvent()

        object OnClickBottomSheet : UIEvent()

        object OnCallMutationInitialRequest : UIEvent()
    }

    companion object {
        const val SMART_INDICATOR_SV_TOTAL_STEPS = 5
        const val SMART_INDICATOR_CR_TOTAL_STEPS = 3
        const val DEFAULT_ID_BRAND_ERROR = -1
        const val URL_EMPTY = "url"
        const val STEP_BY_STEP_EVENT_DELAY = 1500L
        const val ONE_SEC_DELAY = 1000L
    }
}
