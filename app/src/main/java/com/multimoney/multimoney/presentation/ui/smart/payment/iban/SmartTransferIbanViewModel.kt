package com.multimoney.multimoney.presentation.ui.smart.payment.iban

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnCallQueryListSinpeAccountUseCaseImpl
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartTransferIbanViewModel @Inject constructor(
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var idClient: String = ""
    private var idLoanClient: String = ""
    private var identification: String? = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun callQueryListSinpeAccountUseCaseImpl() = executeUseCase {
        queryListSinpeAccountUseCaseImpl.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand.toInt(),
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
                        uiState = uiState.copy(sinpeAccountList = it)
                    }
                }
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
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

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.TransferIbanAccountScreen.route
        )
    }

    private fun navigateToAddIbanAccount() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    private fun onAddAccountClick() {
        // TODO Implement add sinpeAccount navigation
    }

    private fun onAccountClick(selectedSinpeAccount: SinpeAccount) {
        // TODO Implement selected sinpeAccount navigation
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var sinpeAccountList: List<SinpeAccount?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
            OnCallQueryListSinpeAccountUseCaseImpl -> callQueryListSinpeAccountUseCaseImpl()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        object OnCallQueryListSinpeAccountUseCaseImpl : UIEvent()
        data class OnAccountClick(val account: SinpeAccount) : UIEvent()
    }
}