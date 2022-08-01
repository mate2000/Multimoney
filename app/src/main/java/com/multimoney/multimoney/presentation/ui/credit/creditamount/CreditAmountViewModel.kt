package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.BaseEvent.OnOpenConditionOfCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenConditionCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenTermAndCondition
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallQueryPaymentAmountUseCase
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreditAmountViewModel @Inject constructor(
    private val queryCreditOfferUseCase: QueryCreditOfferUseCase,
    private val queryPaymentAmountUseCase: QueryPaymentAmountUseCase
) : BaseViewModel() {

    private fun callQueryCreditOfferUseCase(
        pkUser: Int,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCreditOfferUseCase.invoke(pkUser = pkUser, idBrand = idBrand).collectLatest { result ->
            result.onSuccess {
                // TODO: Set fields
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun callQueryPaymentAmountUseCase(
        amount: Int,
        months: String,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryPaymentAmountUseCase.invoke(
            amount = amount,
            months = months,
            idProduct = idProduct,
            currencySymbol = currencySymbol,
            user = user,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                // TODO: Set fields
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var conditionModalDescription: String = ""

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction.invoke()
    }

    private fun onOpenConditionCreditDialog() {
        emitBaseEvent(
            OnOpenConditionOfCreditDialog(
                DialogParameters(
                    title = R.string.credit_amount_condition_of_credit_info,
                    description = conditionModalDescription,
                    isActive = mutableStateOf(true),
                    positiveText = R.string.accept
                )
            )
        )
    }

    private fun onOpenTermAndCondition() {

    }

    data class UIState(
        val isTermAndConditionChecked: Boolean = false,
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnInitializeText -> conditionModalDescription = uiEvent.dialogDescription
            is OnCallQueryCreditOfferUseCase -> callQueryCreditOfferUseCase(
                uiEvent.pkUser,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnCallQueryPaymentAmountUseCase -> callQueryPaymentAmountUseCase(
                uiEvent.amount,
                uiEvent.months,
                uiEvent.idProduct,
                uiEvent.currencySymbol,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnTermAndConditionCheckedChange -> uiState = uiState.copy(isTermAndConditionChecked = uiEvent.isChecked)
            is OnOpenConditionCreditDialog -> onOpenConditionCreditDialog()
            is OnOpenTermAndCondition -> onOpenTermAndCondition()
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        data class OnCallQueryCreditOfferUseCase(
            val pkUser: Int = 229913,
            val idBrand: Int = Brand.Revamp.id,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) :
            UIEvent()

        data class OnCallQueryPaymentAmountUseCase(
            val amount: Int,
            val months: String,
            val idProduct: String,
            val currencySymbol: String,
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnTermAndConditionCheckedChange(val isChecked: Boolean) : UIEvent()
        object OnOpenConditionCreditDialog : UIEvent()
        object OnOpenTermAndCondition : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOpenConditionOfCreditDialog(val dialogParameters: DialogParameters) : BaseEvent()
    }
}