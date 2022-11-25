package com.multimoney.multimoney.presentation.ui.credit.disbursement.aswerquestions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.RegularExpression
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
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getRegex
import com.multimoney.multimoney.presentation.util.matchRegex
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class AnswerQuestionsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    private val queryBanksAndRegularExpressionUseCase: QueryBanksAndRegularExpressionUseCase
): BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idBrand: Int? = null
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Int = 0

    init {
        idBrand = savedStateHandle[ID_BRAND]
        pkUser = savedStateHandle[PK_USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
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

    /*private fun onLoad(){

        onCallQueryBanksAndRegularExpressions(
            pkUser = pkUser.toInt(),
            user = email,
            idBrand = idBrand.toInt(),
            idUserRequest = idUserRequest,
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

    private fun onCallQueryBanksAndRegularExpressions(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        list: List<CreditCatalog?>?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBanksAndRegularExpressionUseCase.invoke(pkUser, user, idBrand, idUserRequest).collectLatest { result ->
            result.onSuccess {
                bank = it.banks?.first()
                accountTypeList = it.regularExpression
                bankList = bank?.subOptions?.filter { filter ->
                    filter?.description != MIDDLE_DASH
                }
                uiState = uiState.copy(bankList = bankList)
                if (!bank?.pkCatalog.isNullOrEmpty()) {
                    loadStepsInfo(list)
                }
                onLoadingValueChange(false)
            }.onLoading {
                onLoadingValueChange(true)
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }*/

    private fun onBirthDateValueChange(birthdate: String) {
        uiState = uiState.copy(birthdate = birthdate)
        validateForm()
    }

    private fun onAmountValueChange(paymentAmount: String) {
        if (validateDecimalIncome(paymentAmount)) {
            uiState = uiState.copy(paymentAmount = paymentAmount)
        }
        validateForm()
    }

    private fun onLaborSituationValueChanged(laborSituationSelected: String?) {
        uiState = uiState.copy(
            laborSituationSelected = laborSituationSelected,
        )
        validateForm()
    }

    private fun validateForm() {
        //uiState = uiState.copy(isContinueEnabled = uiState.laborSituationSelected != null && uiState.accountTypeSelected != null && uiState.accountNumber.isNotEmpty())
    }

    data class UIState(
        val titleResource: Int = R.string.empty,
        val birthdate: String = "",
        val laborSituationList: List<String> = listOf(),
        val laborSituationSelected: String? = null,
        val paymentAmount: String = "",
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            //is UIEvent.OnCallQueryBanksAndRegularExpression -> onLoad()
            is UIEvent.OnValidateForm -> validateForm()
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnBirthDateValueChange -> onBirthDateValueChange(event.date)
            is UIEvent.OnPaymentAmountValueChange -> onAmountValueChange(event.paymentAmount)
            is UIEvent.OnLaborSituationValueChanged -> onLaborSituationValueChanged(event.laborSituationSelected)
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is UIEvent.OnBackClick -> navigateBackToHome()
        }
    }

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.AnswerQuestionsScreen.route
        )
    }

    sealed class UIEvent {
        object OnCallQueryBanksAndRegularExpression : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnBirthDateValueChange(val date: String) : UIEvent()
        data class OnPaymentAmountValueChange(val paymentAmount: String) : UIEvent()
        data class OnLaborSituationValueChanged(val laborSituationSelected: String) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val BIRTH_DATE_MIN_YEAR = 1902
        const val BIRTH_DATE_MIN_MONTH = 0
        const val BIRTH_DATE_MIN_DAY = 1
    }

}
