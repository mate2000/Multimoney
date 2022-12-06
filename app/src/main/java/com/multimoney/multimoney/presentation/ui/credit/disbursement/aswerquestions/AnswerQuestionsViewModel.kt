package com.multimoney.multimoney.presentation.ui.credit.disbursement.aswerquestions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryEmploymentSituationUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateMinusYears
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AnswerQuestionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    private val queryEmploymentSituationUseCase: QueryEmploymentSituationUseCase,
    private val mutationSaveCreditFlowStepUseCase: MutationSaveCreditFlowStepUseCase,
    private val mutationSaveCreditOfferUseCase: MutationSaveCreditOfferUseCase
): BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idBrand: Int? = null
    private var pkUser: Int? = null
    private var identification: String? = ""
    private var email: String? = ""
    private var idUserRequest: Int? = null
    private var employmentSituation: CreditCatalog? = null
    private var employmentSituationList: List<CreditCatalogOption?>? = listOf()
    var minYear: Int? = null
    var minMonth: Int? = null
    var minDay: Int? = null
    private var birthdayMinDate: LocalDate? = null
    private var birthdayMaxDate: LocalDate? = null
    private var birthdateFormatter: DateTimeFormatter? = null

    init {
        idBrand = savedStateHandle[ID_BRAND]
        pkUser = savedStateHandle[PK_USER]
        identification = savedStateHandle[IDENTIFICATION]
        email = savedStateHandle[EMAIL]
        idUserRequest = savedStateHandle[ID_USER_REQUEST]
        getTextResources()
        setBirthdayMinAndMaxDates(minDate = 120, maxDate = 18)
    }

    private fun setBirthdayMinAndMaxDates(minDate: Long, maxDate: Long) {
        birthdateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
        birthdayMinDate = getCurrentDateMinusYears(minDate)
        birthdayMaxDate = getCurrentDateMinusYears(maxDate)
    }

    private fun setMinDate(minusYears: Long){
        val dateMinus = getCurrentDateMinusYears(minusYears)
        Log.v("CITERIO", "Date ${dateMinus}")
        minYear = dateMinus.year
        minMonth = dateMinus.monthValue - 1
        minDay = dateMinus.dayOfMonth
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.ElSalvador.id -> R.string.disbursement_answer_questions_title_sv
                Brand.Guatemala.id -> R.string.disbursement_answer_questions_title_gt
                else -> R.string.disbursement_answer_questions_title_sv
            }
        )
    }

    private fun onStart(){
        onCallQueryEmploymentSituation(
            pkUser = pkUser ?: 0,
            user = email.orEmpty(),
            idBrand = idBrand ?: 0,
            idUserRequest = idUserRequest ?: 0,
            list = saveCreditStepsHelper.inputTextInfoList,
            onLoadingValueChange = { isLoading ->
                onUIEvent(UIEvent.OnLoadingValueChange(isLoading))
            },
            onFailureWithDialog = { isLoading, dialogParameter ->
                onUIEvent(
                    UIEvent.OnFailureWithDialog(
                        isLoading,
                        dialogParameter
                    )
                )
            }
        )
    }

    private fun onCallQueryEmploymentSituation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        list: List<CreditCatalog?>?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryEmploymentSituationUseCase.invoke(pkUser, user, idBrand, idUserRequest).collectLatest { result ->
            result.onSuccess {
                Log.v("CITERIO", "Employment Situation list ${it}")
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
                    val employmentSituationSelected = employmentSituationList?.find { it?.pkCatalog == employmentSituation?.pkCatalog }
                    uiState = uiState.copy(
                        employmentSituationSelected = employmentSituationSelected
                    )
                    onUIEvent(
                        UIEvent.OnEmploymentSituationValueChanged(
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
                Pair(true, R.string.disbursement_answer_questions_birthdate_minus_120_error)
            }else if (datePicked.isAfter(birthdayMaxDate)){
                Pair(true, R.string.disbursement_answer_questions_birthdate_minus_18_error)
            }else{
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
            employmentSituationSelected = employmentSituationSelected,
        )
        validateForm()
    }

    private fun validateForm() {
        uiState = uiState.copy(isContinueEnabled = uiState.employmentSituationSelected != null && uiState.birthDate.isNotEmpty() && uiState.paymentAmount.isNotEmpty())
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        saveAdditionalQuestions()
        onCallMutationSaveCreditFlowStep()
    }

    private fun saveAdditionalQuestions(){
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
                currentStep = CreditStep.Search.getNameById(1)
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
                pkUser,
                idUserRequest,
                idBrand
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
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


    data class UIState(
        val titleResource: Int = R.string.empty,
        val birthDate: String = "",
        val birthDateError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val employmentSituationList: List<CreditCatalogOption?>? = listOf(),
        val employmentSituationSelected: CreditCatalogOption? = null,
        val paymentAmount: String = "",
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnCallQueryEmploymentSituation -> onStart()
            is UIEvent.OnValidateForm -> validateForm()
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnBirthDateValueChange -> onBirthDateValueChange(event.date)
            is UIEvent.OnPaymentAmountValueChange -> onAmountValueChange(event.paymentAmount)
            is UIEvent.OnEmploymentSituationValueChanged -> onEmploymentSituationValueChanged(event.employmentSituationSelected)
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is UIEvent.OnBackClick -> navigateBackToHome()
            is UIEvent.OnContinueClick -> onContinueClick(event.focusManager)
        }
    }

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.AnswerQuestionsScreen.route
        )
    }

    sealed class UIEvent {
        object OnCallQueryEmploymentSituation : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnBirthDateValueChange(val date: String) : UIEvent()
        data class OnPaymentAmountValueChange(val paymentAmount: String) : UIEvent()
        data class OnEmploymentSituationValueChanged(val employmentSituationSelected: CreditCatalogOption?) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
    }

    companion object {
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val VISUAL_DATE_SYMBOL = " | "
        const val BIRTH_DATE_MIN_YEAR = 0
        const val BIRTH_DATE_MIN_MONTH = 0
        const val BIRTH_DATE_MIN_DAY = 1
        const val MIDDLE_DASH = "-"
    }
}
