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
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartStatus
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.data.util.catalog.SmartWorkflow
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
import com.multimoney.multimoney.presentation.navigation.WORK_FLOW
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_GLOBAL_REQUEST_NAV
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeState
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
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OverridePreviousAction
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class SmartViewModel @Inject constructor(
    private val queryStepByStepUseCase: QueryStepByStepUseCase,
    private val mutationGlobalRequestUseCase: MutationGlobalRequestUseCase,
    private val mutationInitialRequestUseCase: MutationInitialRequestUseCase,
    private val mutationSaveSmartAccount: MutationSaveAutomatedSmartAccountUseCase,
    private val dataStorePreferences: DataStorePreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // bundle parameters
    val pkUser = savedStateHandle[PK_USER] ?: ""
    val idBrand = savedStateHandle[ID_BRAND] ?: ""
    val user = savedStateHandle[USER] ?: ""
    val comingFromCrypto = savedStateHandle[COMING_FROM_CRYPTO] ?: false
    val idBrandAsInt = idBrand.toIntOrNull() ?: DEFAULT_ID_BRAND_ERROR
    val identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    val email: String = savedStateHandle[EMAIL] ?: ""
    val firstName: String = savedStateHandle[FIRST_NAME] ?: ""
    val lastName: String = savedStateHandle[LAST_NAME] ?: ""
    var idGlobalRequest: Long = savedStateHandle[ID_GLOBAL_REQUEST_NAV] ?: 0
    var idSysRequest: Long = savedStateHandle[ID_USER_REQUEST] ?: 0
    var workflow: String = savedStateHandle[WORK_FLOW] ?: ""
    var evicertiaStatus: String = savedStateHandle[EVICERTIA_STATUS] ?: ""

    // Stateless
    private var overridePreviousAction: (() -> Unit)? = null
    private var closeDialogDescription: String = ""
    private var nextStep: Int = SmartSteps.One.id
    private var previousStep: Int = SmartSteps.One.id

    var accountSmartData: AccountSmartData? = null
    var isOnFidoVerified = true
    var nextAction: () -> Unit = {}

    // UIState
    var uiState by mutableStateOf(UIState())

    init {
        accountSmartData = AccountSmartData(
            pkUser = pkUser,
            idBrand = idBrandAsInt,
            status = 0,
            user = user
        )
    }

    private fun callQueryStepByStepUseCase() = executeUseCase {
        if (idGlobalRequest != 0L) {
            queryStepByStepUseCase.invoke(
                user = accountSmartData?.user ?: "",
                idBrand = accountSmartData?.idBrand ?: 0,
                idRequest = idGlobalRequest
            ).collectLatest { result ->
                result.onSuccess { stepByStep ->
                    stepByStep?.let {
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
            birthday = stepByStep.birthdate,
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
            institutionPension = stepByStep.institutionalPesion,
            income = stepByStep.income,
            addressDetail = stepByStep.addressDetail,
            user = user,
            idBrand = idBrandAsInt,
            currentStep = stepByStep.currentStep,
            aboutCompany = stepByStep.aboutCompany,
            companyName = stepByStep.nameCompany,
            specifiesIncomeSource = stepByStep.specifiesIncomeSource,
            listBeneficiaries = stepByStep.beneficiary,
            entrepreneurship = stepByStep.entrepreneurship,
            legalID = stepByStep.legalID,
            isPEP = stepByStep.isPEP,
            isUSCitizen = stepByStep.isUSCitizen,
            isActivityOfArt15 = stepByStep.isActivityOfArt15,
            isUSTaxPayer = stepByStep.isUSTaxPayer,
            isTaxPayer = stepByStep.isTaxPayer,
            idJobLevel1 = stepByStep.idJobLevel1,
            idJobLevel2 = stepByStep.idJobLevel2,
            idJobLevel3 = stepByStep.idJobLevel3,
            fullJobAddress = stepByStep.fullJobAddress,
            idGlobalRequest = stepByStep.idRequest?.toLong() ?: 0
        )

        val currentStep = SmartSteps.Search.getIdByName(stepByStep.currentStep)

        when {
            // navigate to onfido screen after the last step obtained and idBrand matches the country id
            currentStep == SmartSteps.Four.id && idBrandAsInt == Brand.CostaRica.id -> navigateToOnfidoOrEvicertia()
            currentStep == SmartSteps.Six.id && idBrandAsInt == Brand.ElSalvador.id -> navigateToOnfidoOrEvicertia()
            else -> {
                // update the current step coming from the backend in order to navigate to the proper screen
                uiState = uiState.copy(
                    currentStep = SmartSteps.Search.getIdByName(stepByStep.currentStep)
                )
            }
        }
    }

    private fun callMutationInitialRequestUseCase() {
        if (idGlobalRequest == 0L) {
            executeUseCase(
                action = {
                    mutationInitialRequestUseCase.invoke(
                        pkUser = accountSmartData?.pkUser?.toLongOrNull() ?: 0,
                        idBrand = accountSmartData?.idBrand ?: 0,
                        user = accountSmartData?.user ?: ""
                    ).collectLatest { result ->
                        result.onSuccess {
                            accountSmartData?.idGlobalRequest = it?.idGlobalRequest ?: 0
                            idGlobalRequest = it?.idGlobalRequest ?: 0
                            onUIEvent(OnLoadingValueChange(false))
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
        } else {
            accountSmartData?.idGlobalRequest = idGlobalRequest
            callQueryStepByStepUseCase()
        }
    }

    private fun callMutationGlobalRequestUseCase(isLastStep: Boolean = false) = executeUseCase(
        action = {
            mutationGlobalRequestUseCase.invoke(
                pkUser = accountSmartData?.pkUser?.toIntOrNull() ?: 0,
                status = accountSmartData?.status ?: 0,
                user = accountSmartData?.user ?: "",
                idBrand = accountSmartData?.idBrand ?: 0,
                idGlobalRequest = accountSmartData?.idGlobalRequest ?: 0,
                currentStep = accountSmartData?.currentStep,
                birthday = accountSmartData?.birthday,
                idGender = accountSmartData?.idGender,
                idCivilStatusType = accountSmartData?.idCivilStatusType,
                idProfessionType = accountSmartData?.idProfessionType,
                expirationDate = accountSmartData?.expirationDate,
                nationality = accountSmartData?.placeOfIssue,
                dateOfEmission = accountSmartData?.dateOfIssue,
                idAddressLevel1 = accountSmartData?.idAddressLevel1,
                idAddressLevel2 = accountSmartData?.idAddressLevel2,
                idAddressLevel3 = accountSmartData?.idAddressLevel3,
                addressDetail = accountSmartData?.addressDetail,
                idEconomicActivity = accountSmartData?.idEconomicActivity,
                income = accountSmartData?.income?.toDouble(),
                positionJob = accountSmartData?.positionJob,
                companyName = accountSmartData?.companyName,
                aboutCompany = accountSmartData?.aboutCompany,
                institutionPension = accountSmartData?.institutionPension,
                specifiesIncomeSource = accountSmartData?.specifiesIncomeSource,
                entrepreneurship = accountSmartData?.entrepreneurship,
                idJobLevel1 = accountSmartData?.idJobLevel1,
                idJobLevel2 = accountSmartData?.idJobLevel2,
                idJobLevel3 = accountSmartData?.idJobLevel3,
                fullJobAddress = accountSmartData?.fullJobAddress,
                beneficiaries = accountSmartData?.listBeneficiaries,
                isActivityOfArt15 = accountSmartData?.isActivityOfArt15,
                isUSCitizen = accountSmartData?.isUSCitizen,
                isPEP = accountSmartData?.isPEP,
                isUSTaxPayer = accountSmartData?.isUSTaxPayer,
                isTaxPayer = accountSmartData?.isTaxPayer,
                legalID = accountSmartData?.legalID
            ).collectLatest { result ->
                result.onSuccess {
                    trackAdjustEvents()

                    idSysRequest = it?.idSysRequest?.toLong() ?: 0L
                    idGlobalRequest = it?.idGlobalRequest ?: 0
                    onUIEvent(OnLoadingValueChange(false))
                    if (isLastStep) callMutationSaveSmartAccount() else onUIEvent(OnNextStep)
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

    /**
     * After last step of origination (Before OnFido/Evicertia)
     * Saves account smart
     */
    private fun onCallMutationSaveSmartAccount(accountData: AccountSmartData?) {
        accountSmartData = accountData
        callMutationGlobalRequestUseCase()
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

    private fun overridePreviousAction(overridePreviousAction: (() -> Unit)?) {
        this.overridePreviousAction = overridePreviousAction
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )

    private fun nextStep() {
        if (nextStep <= getTotalStepperCounter()) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= SmartSteps.One.id,
                isLoading = false
            )
        } else {
            accountSmartData = accountSmartData?.copy(
                currentStep = SmartSteps.Search.getNameById(nextStep)
            )
            callMutationGlobalRequestUseCase(true)
        }
    }

    private fun navigateToOnfidoOrEvicertia() {
        if (workflow == SmartWorkflow.SMART_CONTRACT_PROCESS.workflow) {
            onNavigateToSignDocumentScreen()
        } else {
            popAndNavigateTo(
                "${Screen.SmartOnfidoScreen.baseRoute}/$user/$idBrand/$pkUser/$identification/$email/$firstName/$lastName/$idSysRequest/$idGlobalRequest/$URL_EMPTY/$comingFromCrypto/$evicertiaStatus/$workflow",
                Screen.SmartScreen.route
            )
        }
    }

    private fun onNavigateToSignDocumentScreen() {
        popAndNavigateTo(
            route = "${Screen.SmartSignScreen.baseRoute}/${SignDocumentStep.GENERATE_DOCUMENT_STEP.value}/$URL_EMPTY/" +
                    "$idBrand/$pkUser/$identification/$email/$idSysRequest/$firstName/$lastName/${true}/$idGlobalRequest/$user/$comingFromCrypto/${true}",
            popTo = Screen.SmartOnfidoScreen.route
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

    private fun callMutationSaveSmartAccount() {
        executeUseCase {
            mutationSaveSmartAccount.invoke(
                user = user,
                idBrand = idBrand.toIntOrNull() ?: 0,
                identificationNumber = identification,
                idRequest = idGlobalRequest
            ).collectLatest { result ->
                result.onSuccess {
                    onUIEvent(OnLoadingValueChange(false))
                    idSysRequest = it?.idAccount ?: 0L
                    navigateToOnfidoOrEvicertia()
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
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

    private fun trackAdjustEvents() {
        val parametersList = buildList<Pair<String, String>> {
            add(STATUS to accountSmartData?.status.toString())
            add(ID_GLOBAL_REQUEST to accountSmartData?.idGlobalRequest.toString())
            add(ID_PROFESSION_TYPE to accountSmartData?.idProfessionType.toString())
            add(BIRTHDATE to (accountSmartData?.birthday ?: ""))
            add(EXPIRATION_DATE to (accountSmartData?.expirationDate ?: ""))
            add(ID_GENRE to accountSmartData?.idGender.toString())
            add(ID_MARITAL_STATUS to accountSmartData?.idCivilStatusType.toString())
            add(INSTITUTION_PENSION to (accountSmartData?.institutionPension ?: ""))
            add(NAME_COMPANY to (accountSmartData?.companyName ?: ""))
            add(ABOUT_COMPANY to (accountSmartData?.aboutCompany ?: ""))
            add(ID_ADDRESS_LEVEL_1 to accountSmartData?.idAddressLevel1.toString())
            add(ID_ADDRESS_LEVEL_2 to accountSmartData?.idAddressLevel2.toString())
            add(ID_ADDRESS_LEVEL_3 to accountSmartData?.idAddressLevel3.toString())
            add(POSITION_JOB to (accountSmartData?.positionJob ?: ""))
            add(ID_ECONOMIC_ACTIVITY to accountSmartData?.idEconomicActivity.toString())
            add(INCOME to accountSmartData?.income.toString())
            add(ADDRESS_DETAIL to (accountSmartData?.addressDetail ?: ""))
            add(FULL_JOB_ADDRESS to (accountSmartData?.fullJobAddress ?: ""))
            add(CURRENT_STEP to (accountSmartData?.currentStep ?: ""))
            add(SPECIFIES_INCOME_SOURCE to (accountSmartData?.specifiesIncomeSource ?: ""))
            add(ENTREPRENEURSHIP to (accountSmartData?.entrepreneurship ?: ""))
            add(LEGAL_ID to (accountSmartData?.legalID ?: ""))
            add(IS_ACTIVITY_OF_ART_15 to accountSmartData?.isActivityOfArt15.toString())
            add(IS_US_CITIZEN to (accountSmartData?.isUSCitizen.toString()))
            add(IS_PEP to (accountSmartData?.isPEP.toString()))
            add(IS_US_TAX_PAYER to (accountSmartData?.isUSTaxPayer.toString()))
            add(IS_TAX_PAYER to (accountSmartData?.isTaxPayer.toString()))
            add(LIST_BENEFICIARIES to (accountSmartData?.listBeneficiaries?.toJson() ?: ""))
            add(ID_JOB_LEVEL_1 to accountSmartData?.idJobLevel1.toString())
            add(ID_JOB_LEVEL_2 to accountSmartData?.idJobLevel2.toString())
            add(ID_JOB_LEVEL_3 to accountSmartData?.idJobLevel3.toString())
        }

        trackAdjustEventsByIdBrand(parametersList, accountSmartData?.currentStep ?: "")
    }

    private fun trackAdjustEventsByIdBrand(parameters: List<Pair<String, String>>, data: String) {
        viewModelScope.launch {
            if (accountSmartData?.idBrand == Brand.ElSalvador.id) {
                trackSvAdjustOriginationEvents(parameters, data)
            } else {
                trackCrAdjustOriginationEvents(parameters, data)
            }
        }
    }

    private fun trackSvAdjustOriginationEvents(
        parameters: List<Pair<String, String>>,
        data: String
    ) {
        viewModelScope.launch {
            when (accountSmartData?.currentStep) {
                SmartSteps.One.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimePersonal()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimePersonal(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_PERSONAL,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Two.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimeHome()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimeHome(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_HOME,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Three.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimeIncomeInformation()
                            .first(),
                        reset = {
                            dataStorePreferences.setAdjustSmartFirstTimeIncomeInformation(
                                false
                            )
                        },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_INCOME_INFORMATION,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Four.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimeBeneficiary()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimeBeneficiary(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_BENEFICIARY,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Six.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimePep()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimePep(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_PEP,
                        parameters = parameters,
                        data = data
                    )
                }
            }
        }
    }

    private fun trackCrAdjustOriginationEvents(
        parameters: List<Pair<String, String>>,
        data: String
    ) {
        viewModelScope.launch {
            when (accountSmartData?.currentStep) {
                SmartSteps.One.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimeHome()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimeHome(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_HOME,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Two.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimeIncomeInformation()
                            .first(),
                        reset = {
                            dataStorePreferences.setAdjustSmartFirstTimeIncomeInformation(
                                false
                            )
                        },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_INCOME_INFORMATION,
                        parameters = parameters,
                        data = data
                    )
                }
                SmartSteps.Four.name -> {
                    trackAdjustEvent(
                        firstTimeCondition = dataStorePreferences.isAdjustSmartFirstTimePep()
                            .first(),
                        reset = { dataStorePreferences.setAdjustSmartFirstTimePep(false) },
                        adjustEventType = AdjustEventType.ORIGINATION_SMART_FIRST_TIME_PEP,
                        parameters = parameters,
                        data = data
                    )
                }
            }
        }
    }

    private fun trackAdjustEvent(
        firstTimeCondition: Boolean,
        reset: suspend () -> Unit,
        adjustEventType: AdjustEventType,
        parameters: List<Pair<String, String>>,
        data: String
    ) {
        if (firstTimeCondition) {
            viewModelScope.launch { reset() }
            registerAdjustEvent(
                adjustEventType,
                listParameters = parameters,
                data = data
            )
        }
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
            is OnFailureWithDialog -> uiState = uiState.copy(
                isLoading = event.isLoading,
                openDialog = event.openDialog
            )
            is OnNextStep -> nextStep()
            is OnPreviousStep -> previousStep()
            is OnContinueVisible -> uiState = uiState.copy(
                isContinueVisible = event.visible,
                buttonTextRes = event.textResId
            )
            is OnCallMutationUpdateGlobalRequestUseCase -> onUpdateAccountSmartData(event.accountSmartData)
            is OnCallMutationInitialRequest -> callMutationInitialRequestUseCase()
            is OnOnFidoVerifiedChanged -> isOnFidoVerified = event.isOnFidoVerified
            is OnCallSaveAutomatedSmartAccount -> onCallMutationSaveSmartAccount(event.accountSmartData)
            is OverridePreviousAction -> overridePreviousAction(event.action)
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

        data class OverridePreviousAction(val action: (() -> Unit)?) : UIEvent()
    }

    companion object {
        const val SMART_INDICATOR_SV_TOTAL_STEPS = 5
        const val SMART_INDICATOR_CR_TOTAL_STEPS = 3
        const val DEFAULT_ID_BRAND_ERROR = -1
        const val URL_EMPTY = "url"

        private const val STATUS = "status"
        private const val ID_GLOBAL_REQUEST = "idGlobalRequest"
        private const val ID_PROFESSION_TYPE = "idProfessionType"
        private const val BIRTHDATE = "birthdate"
        private const val EXPIRATION_DATE = "expirationDate"
        private const val ID_GENRE = "idGenre"
        private const val ID_MARITAL_STATUS = "idMaritalStatus"
        private const val INSTITUTION_PENSION = "institutionPension"
        private const val NAME_COMPANY = "nameCompany"
        private const val ABOUT_COMPANY = "aboutCompany"
        private const val ID_ADDRESS_LEVEL_1 = "idAddressLevel1"
        private const val ID_ADDRESS_LEVEL_2 = "idAddressLevel2"
        private const val ID_ADDRESS_LEVEL_3 = "idAddressLevel3"
        private const val POSITION_JOB = "positionJob"
        private const val ID_ECONOMIC_ACTIVITY = "idEconomicActivity"
        private const val INCOME = "income"
        private const val ADDRESS_DETAIL = "addressDetail"
        private const val FULL_JOB_ADDRESS = "fullJobAddress"
        private const val CURRENT_STEP = "currentStep"
        private const val SPECIFIES_INCOME_SOURCE = "specifiesIncomeSource"
        private const val ENTREPRENEURSHIP = "entrepreneurship"
        private const val LEGAL_ID = "legalID"
        private const val IS_ACTIVITY_OF_ART_15 = "isActivityOfArt15"
        private const val IS_US_CITIZEN = "isUSCitizen"
        private const val IS_PEP = "isPEP"
        private const val IS_US_TAX_PAYER = "isUSTaxPayer"
        private const val IS_TAX_PAYER = "isTaxPayer"
        private const val LIST_BENEFICIARIES = "listBeneficiaries"
        private const val ID_JOB_LEVEL_1 = "idJobLevel1"
        private const val ID_JOB_LEVEL_2 = "idJobLevel2"
        private const val ID_JOB_LEVEL_3 = "idJobLevel3"
    }
}
