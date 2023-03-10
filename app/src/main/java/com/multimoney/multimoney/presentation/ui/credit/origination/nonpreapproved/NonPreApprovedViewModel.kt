package com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryEmploymentSituationUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.metrics.OriginationEventDataDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CROSSELING
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnBirthDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnEmploymentSituationValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnPaymentAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnRequestClick
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnUpdateScreenConfigData
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateMinusYears
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NonPreApprovedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    private val queryEmploymentSituationUseCase: QueryEmploymentSituationUseCase,
    private val queryScreenConfigUseCase: QueryScreenConfigUseCase,
    private val mutationSaveCreditFlowStepUseCase: MutationSaveCreditFlowStepUseCase,
    private val mutationSaveCreditOfferUseCase: MutationSaveCreditOfferUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idBrand: Int? = null
    private var pkUser: Int? = null
    private var identification: String? = ""
    private var email: String? = ""
    private var idUserRequest: Int? = null
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var idPrint: Long? = 0
    private var crosseling: Boolean? = false
    private var statusOnfido: String? = ""
    private var statusEvicertia: String? = ""
    private var lastStep: Int? = null
    private var employmentSituation: CreditCatalog? = null
    private var employmentSituationList: List<CreditCatalogOption?>? = listOf()
    private var birthdayMinDate: LocalDate? = null
    private var birthdayMaxDate: LocalDate? = null
    private var birthdateFormatter: DateTimeFormatter? = null

    init {
        idBrand = savedStateHandle[ID_BRAND]
        pkUser = savedStateHandle[PK_USER]
        identification = savedStateHandle[IDENTIFICATION]
        email = savedStateHandle[EMAIL]
        idUserRequest = savedStateHandle[ID_USER_REQUEST]
        firstName = savedStateHandle[FIRST_NAME]
        lastName = savedStateHandle[LAST_NAME]
        statusOnfido = savedStateHandle[ONFIDO_STATUS]
        statusEvicertia = savedStateHandle[EVICERTIA_STATUS]
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT]
        lastStep = savedStateHandle[CREDIT_STEP]
        crosseling = savedStateHandle[CROSSELING]
        getTextResources()
        setBirthdayMinAndMaxDates(minDate = DATE_MIN_YEARS, maxDate = DATE_MAX_YEARS)
    }

    private fun setBirthdayMinAndMaxDates(minDate: Long, maxDate: Long) {
        birthdateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
        birthdayMinDate = getCurrentDateMinusYears(minDate)
        birthdayMaxDate = getCurrentDateMinusYears(maxDate)
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.CostaRica.id -> R.string.non_pre_approved_additional_questions_title
                else -> R.string.non_pre_approved_additional_questions_title_sv
            }
        )
    }

    private fun onStart() {
        onCallQueryEmploymentSituation(
            pkUser = pkUser ?: 0,
            user = email.orEmpty(),
            idBrand = idBrand ?: 0,
            idUserRequest = idUserRequest ?: 0,
            onLoadingValueChange = { isLoading ->
                onUIEvent(OnLoadingValueChange(isLoading))
            },
            onFailureWithDialog = { isLoading, dialogParameter ->
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading,
                        dialogParameter
                    )
                )
            }
        )

        queryCreditSteps()
    }

    fun queryCreditSteps() {
        executeUseCase {
            queryScreenConfigUseCase(
                pkUser = pkUser.toString(),
                user = email.orEmpty(),
                idBrand = idBrand ?: 0,
                idUserRequest = idUserRequest ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    onUIEvent(OnUpdateScreenConfigData(it))
                }
            }
        }
    }

    private fun onCallQueryEmploymentSituation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryEmploymentSituationUseCase.invoke(
            pkUser = pkUser,
            user = user,
            idBrand = idBrand,
            idUserRequest = idUserRequest
        ).collectLatest { result ->
            result.onSuccess {
                if (employmentSituation == null) {
                    employmentSituation = it?.first()
                }
                employmentSituationList = employmentSituation?.subOptions?.filter { filter ->
                    filter?.description != MIDDLE_DASH
                }
                uiState = uiState.copy(
                    employmentSituationList = employmentSituationList
                )
                if (!employmentSituation?.pkCatalog.isNullOrEmpty()) {
                    val employmentSituationSelected =
                        employmentSituationList?.find { it?.pkCatalog == employmentSituation?.pkCatalog }
                    uiState = uiState.copy(
                        employmentSituationSelected = employmentSituationSelected
                    )
                    onUIEvent(
                        OnEmploymentSituationValueChanged(
                            employmentSituationSelected
                        )
                    )
                }
                onLoadingValueChange(false)
            }.onLoading {
                onLoadingValueChange(true)
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    private fun onBirthDateValueChange(birthdate: String) {
        val datePicked = LocalDate.parse(birthdate, birthdateFormatter)
        uiState = uiState.copy(
            birthDate = birthdate.replace(MIDDLE_DASH, VISUAL_DATE_SYMBOL),
            birthDateError = if (datePicked.isBefore(birthdayMinDate)) {
                Pair(true, R.string.non_pre_approved_additional_questions_birthdate_minus_120_error)
            } else if (datePicked.isAfter(birthdayMaxDate)) {
                Pair(true, R.string.non_pre_approved_additional_questions_birthdate_minus_18_error)
            } else {
                Pair(false, R.string.empty)
            }
        )
        validateForm()
    }

    private fun onAmountValueChange(paymentAmount: String) {
        if (validateDecimalIncome(paymentAmount)) {
            uiState = uiState.copy(paymentAmount = paymentAmount)
        }
        validateForm()
    }

    private fun onEmploymentSituationValueChanged(employmentSituationSelected: CreditCatalogOption?) {
        uiState = uiState.copy(
            employmentSituationSelected = employmentSituationSelected
        )
        validateForm()
    }

    private fun validateForm() {
        uiState =
            uiState.copy(isContinueEnabled = uiState.employmentSituationSelected != null && uiState.birthDate.isNotEmpty() && uiState.paymentAmount.isNotEmpty())
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        onSaveAdditionalQuestions()
        onCallMutationSaveCreditFlowStep()
    }

    private fun onSaveAdditionalQuestions() {
        logEvents(AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_INFORMATION_5019)
        saveCreditStepsHelper.saveAdditionalQuestionsForNonPreApprovedFlow(
            user = email,
            monthlyIncomeValue = uiState.paymentAmount,
            birthDate = getFormatDateByString(
                uiState.birthDate.replace(VISUAL_DATE_SYMBOL, MIDDLE_DASH),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            ),
            employmentSituation = employmentSituation,
            employmentSituationSelected = uiState.employmentSituationSelected
        )
    }

    private fun onCallMutationSaveCreditFlowStep() =
        executeUseCase {
            mutationSaveCreditFlowStepUseCase.invoke(
                user = email ?: "",
                idBrand = idBrand ?: 0,
                infoQuestion = saveCreditStepsHelper.creditFlowData,
                idLogUserRequest = idUserRequest ?: 0,
                idUser = pkUser ?: 0,
                currentStep = CreditStep.Search.getNameById(0)
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    onCallMutationSaveCreditOffer(
                        pkUser = pkUser?.toLong() ?: 0,
                        idUserRequest = idUserRequest?.toLong() ?: 0,
                        idBrand = idBrand ?: 0
                    )
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    private fun onCallMutationSaveCreditOffer(
        pkUser: Long,
        idUserRequest: Long,
        idBrand: Int
    ) = executeUseCase {
        mutationSaveCreditOfferUseCase.invoke(
            pkUser = pkUser,
            idUserRequest = idUserRequest,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                if (it?.rejectedBlaze?.not() == true) {
                    logEvents(AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_APPROVED_5020)
                    setSuccessAlertResult(it.products?.firstOrNull()?.maximumDisbursementLabel ?: "")
                } else {
                    logEvents(AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_REJECTED_5021)
                    setErrorAlertResult()
                }
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun navigateBack(isRestart: Boolean) =
        navigateBack(
            isRestart = isRestart,
            popTo = Screen.HomeScreen.route
        )

    private fun onNavigateToOrigination() = popAndNavigateTo(
        route = "${Screen.CreditScreen.baseRoute}/${idBrand ?: 0}/${pkUser ?: 0}/${identification.orEmpty()}/${email.orEmpty()}/${lastStep ?: CreditStep.One.id}/" +
            "${idUserRequest ?: 0}/${firstName.orEmpty()}/" +
            "${lastName.orEmpty()}/${statusOnfido.orEmpty()}/" +
            "${statusEvicertia.orEmpty()}/${idPrint ?: 0}/" +
            "${crosseling ?: false}",
        popTo = Screen.NonPreApprovedScreen.route
    )

    private fun setSuccessAlertResult(amount: String) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = true,
            maxDisbursementAmount = amount,
            alertResultIconResource = R.drawable.ic_success_symbol,
            alertResultTitleResource = R.string.non_pre_approved_additional_questions_success_alert_result_title,
            alertResultDescriptionResource = R.string.non_pre_approved_additional_questions_success_alert_result_description,
            alertResultButtonResource = R.string.non_pre_approved_additional_questions_request_button_label,
            isLoading = false
        )
    }

    private fun setErrorAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = false,
            maxDisbursementAmount = "",
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.non_pre_approved_additional_questions_error_alert_result_title,
            alertResultDescriptionResource = R.string.non_pre_approved_additional_questions_error_alert_result_description,
            alertResultButtonResource = R.string.understood,
            isLoading = false
        )
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val originationDto = OriginationEventDataDto(
            user = email,
            idBrand = idBrand,
            identification = identification,
            idUserRequest = idUserRequest,
            pkUser = pkUser.toString(),
            idPrint = idPrint
        )
        return when (adjustEventType) {
            AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_INFORMATION_5019 -> {
                getInfoExtraFromNonPreApprovedOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_APPROVED_5020 -> {
                getApprovedFromNonPreApprovedOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_REJECTED_5021 -> {
                getRejectedFromNonPreApprovedOriginationEvent(originationDto)
            }
            else -> suspend {}
        }
    }

    private fun getInfoExtraFromNonPreApprovedOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstOriginationNonPreApprovedInfoExtraEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_INFORMATION_5019,
                    data = originationDto.toJson()
                )
                dataStorePreferences.isAdjustFirstOriginationNonPreApprovedInfoExtraEventRegister(false)
            }
        }

    private fun getApprovedFromNonPreApprovedOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstOriginationNonPreApprovedApprovedEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_APPROVED_5020,
                    data = originationDto.toJson()
                )
                dataStorePreferences.isAdjustFirstOriginationNonPreApprovedApprovedEventRegister(false)
            }
        }

    private fun getRejectedFromNonPreApprovedOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstOriginationNonPreApprovedRejectedEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.ORIGINATION_FIRST_NON_PRE_APPROVED_IS_REJECTED_5021,
                    data = originationDto.toJson()
                )
                dataStorePreferences.isAdjustFirstOriginationNonPreApprovedRejectedEventRegister(false)
            }
        }

    data class UIState(
        val titleResource: Int = R.string.empty,
        val birthDate: String = "",
        val birthDateError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val employmentSituationList: List<CreditCatalogOption?>? = listOf(),
        val employmentSituationSelected: CreditCatalogOption? = null,
        val paymentAmount: String = "",
        val maxDisbursementAmount: String = "",
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart()
            is OnValidateForm -> validateForm()
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnBirthDateValueChange -> onBirthDateValueChange(event.date)
            is OnPaymentAmountValueChange -> onAmountValueChange(event.paymentAmount)
            is OnEmploymentSituationValueChanged -> onEmploymentSituationValueChanged(event.employmentSituationSelected)
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnBackClick -> navigateBack(false)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnUpdateScreenConfigData -> {
                saveCreditStepsHelper.start(event.screenConfigData)
            }
            is OnRequestClick -> onNavigateToOrigination()
            is OnCloseClick -> navigateBack(true)
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnBirthDateValueChange(val date: String) : UIEvent()
        data class OnPaymentAmountValueChange(val paymentAmount: String) : UIEvent()
        data class OnEmploymentSituationValueChanged(val employmentSituationSelected: CreditCatalogOption?) :
            UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnRequestClick(val focusManager: FocusManager) : UIEvent()
        data class OnUpdateScreenConfigData(val screenConfigData: List<CreditCatalog?>?) : UIEvent()
    }

    companion object {
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val VISUAL_DATE_SYMBOL = " | "
        const val BIRTH_DATE_MIN_YEAR = 0
        const val BIRTH_DATE_MIN_MONTH = 0
        const val BIRTH_DATE_MIN_DAY = 1
        const val MIDDLE_DASH = "-"
        const val DATE_MIN_YEARS = 120L
        const val DATE_MAX_YEARS = 18L
    }
}
