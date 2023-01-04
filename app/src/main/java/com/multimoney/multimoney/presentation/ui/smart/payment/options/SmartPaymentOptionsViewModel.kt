package com.multimoney.multimoney.presentation.ui.smart.payment.options

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.BaseSelectSmartAccountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartPaymentOptionsViewModel @Inject constructor(
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseSelectSmartAccountViewModel(savedStateHandle) {

    private fun callQueryBalanceUseCase() = executeUseCase {
        queryListSinpeAccountUseCaseImpl.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand,
            country = "",
            idAccount = 0,
            accountNumber = ""
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                if (accountList?.data?.isEmpty() == true) {
                    navigateToAddIbanAccount()
                } else {
                    accountList?.data?.let {
                        navigateToSmartAccount(it)
                    }
                }
            }
            result.onFailure {
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
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

    private fun navigateToAddIbanAccount() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    private fun navigateToSmartAccount(clientBankAccounts: List<SinpeAccount?>) {
        navigateTo(
            route = "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$user/$idBrand/$identification/${Screen.SmartPaymentOptionsScreenCR.baseRoute}/" +
                    "$idClient/$idLoanClient/${encodeData(clientBankAccounts)}/${
                        encodeData(
                            selectedSmartAccount
                        )
                    }"
        )
    }

    override fun onSelectSmartAccount(currencyType: CurrencyType) {
        super.onSelectSmartAccount(currencyType)
        callQueryBalanceUseCase()
    }
}
