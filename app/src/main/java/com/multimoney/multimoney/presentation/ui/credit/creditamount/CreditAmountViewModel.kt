package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.model.credit.Product
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallQueryPaymentAmountUseCase
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnInitializeErrorMessages
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnValidateDisbursement
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.stringToIntegerFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreditAmountViewModel @Inject constructor(
    private val queryCreditOfferUseCase: QueryCreditOfferUseCase,
    private val queryPaymentAmountUseCase: QueryPaymentAmountUseCase
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var products: List<Product?>? = listOf()
    private var currencyItems: List<String>? = listOf()
    private var minimumDisbursement: String = ""
    private var minimumDisbursementErrorMessage = 0
    private var maximumDisbursement: String = ""
    private var maximumDisbursementErrorMessage = 0

    private fun callQueryCreditOfferUseCase(
        pkUser: Int,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCreditOfferUseCase.invoke(pkUser = pkUser, idBrand = idBrand).collectLatest { result ->
            result.onSuccess { creditOffer ->
                products = creditOffer?.products
                currencyItems = products?.map { it?.currency ?: "" }
                setCreditOffer(INITIAL_CURRENCY_INDEX)
                onLoadingValueChange.invoke(false)
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

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction.invoke()
    }

    private fun setCreditOffer(productIndex: Int) {
        // Set initial conditions
        products?.get(productIndex)?.apply {
            this@CreditAmountViewModel.minimumDisbursement = minimumDisbursement
            this@CreditAmountViewModel.maximumDisbursement = maximumDisbursement
            uiState = uiState.copy(
                currencyIndex = productIndex,
                currencyItems = currencyItems ?: listOf(),
                feeLabel = feeLabel,
                disbursement = maximumDisbursement.stringToIntegerFormat(),
                minimumDisbursementLabel = minimumDisbursementLabel,
                maximumDisbursementLabel = maximumDisbursementLabel
            )
        }
    }

    private fun onValidateDisbursement(value: Double) = if (value < minimumDisbursement.toDouble()) {
        Pair(true, minimumDisbursementErrorMessage)
    } else if (value > maximumDisbursement.toDouble()) {
        Pair(true, maximumDisbursementErrorMessage)
    } else {
        uiState.disbursementError
    }

    private fun onInitializeErrorMessages(
        minimumDisbursementErrorMessage: Int,
        maximumDisbursementErrorMessage: Int
    ) {
        this.minimumDisbursementErrorMessage = minimumDisbursementErrorMessage
        this.maximumDisbursementErrorMessage = maximumDisbursementErrorMessage
    }

    data class UIState(
        // Fields
        val currencyIndex: Int = 0,
        val currencyItems: List<String> = listOf("", ""),
        val feeLabel: String = "",
        val disbursement: String = "",
        val disbursementError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val minimumDisbursementLabel: String = "",
        val maximumDisbursementLabel: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
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
            is OnCurrencyIndexChanged -> setCreditOffer(uiEvent.index)
            is OnDisbursementValueChange -> uiState = uiState.copy(disbursement = uiEvent.value)
            is OnValidateDisbursement -> onValidateDisbursement(uiEvent.value.toDouble())
            is OnInitializeErrorMessages -> onInitializeErrorMessages(
                minimumDisbursementErrorMessage = uiEvent.minimumDisbursementErrorMessage,
                maximumDisbursementErrorMessage = uiEvent.maximumDisbursementErrorMessage
            )
        }
    }

    sealed class UIEvent {
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
        data class OnCurrencyIndexChanged(val index: Int) : UIEvent()
        data class OnDisbursementValueChange(val value: String) : UIEvent()
        data class OnValidateDisbursement(val value: String) : UIEvent()
        data class OnInitializeErrorMessages(
            val minimumDisbursementErrorMessage: Int,
            val maximumDisbursementErrorMessage: Int
        ) : UIEvent()
    }

    companion object {
        const val INITIAL_CURRENCY_INDEX = 0
        const val CURRENCY_SEPARATOR = ','
    }
}