package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCallValidateUserStatus
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase
) :
    BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // TODO: Remove hardcoded parameters
    private fun callQueryBalanceUseCase(
        user: String = "ecruzGrapqhql",
        identification: String = "303190775",
        idBrand: Int,
        idClient: String = "192656",
        idLoanClient: Int = 223034
    ) {
        executeUseCase {
            queryBalanceUseCase.invoke(
                user = user,
                identification = identification,
                idBrand = idBrand,
                idClient = idClient,
                idLoanClient = idLoanClient
            ).collectLatest { result ->
                result.onSuccess { balance ->
                    uiState = uiState.copy(isLoading = false)
                    balance?.let {
                        uiState = uiState.copy(balanceCredit = balance)
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
    }


    private fun callQueryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ) {
        viewModelScope.launch {
            queryValidateUserStatusUseCase.invoke(
                pkUser,
                identification,
                email,
                idBrand
            ).collectLatest { result ->
                result.onSuccess { validateUserStatus ->
                    uiState = uiState.copy(isLoading = false)
                    validateUserStatus?.let {
                        onValidateUserStatusSuccess(it)
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
    }

    private fun onValidateUserStatusSuccess(userStatus: ValidateUserStatus) {
        uiState = uiState.copy(userStatus = userStatus)
        // TODO: Send parameters to balance from userStatus
        callQueryBalanceUseCase(idBrand = userStatus.infoUser?.idBrand ?: 0)
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(isLoading = false)
        openDialog = DialogParameters(
            description = error.getError() ?: "",
            isActive = mutableStateOf(true)
        )
    }

    data class UIState(
        //Fields
        var balanceCredit: Balance? = null,
        var userStatus: ValidateUserStatus? = null,
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBalanceSuccess -> uiState.balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> onValidateUserStatusSuccess(uiEvent.userStatus)
            is OnCallValidateUserStatus -> callQueryValidateUserStatus(
                uiEvent.pkUser,
                uiEvent.identification,
                uiEvent.email,
                uiEvent.idBrand
            )
        }
    }

    sealed class UIEvent {
        data class OnBalanceSuccess(val balance: Balance) : UIEvent()
        data class OnValidateUserSuccess(val userStatus: ValidateUserStatus) : UIEvent()

        // TODO: Remove hardcoded parameters
        data class OnCallValidateUserStatus(
            val pkUser: Int = 229913,
            val identification: String = "207100330",
            val email: String = "popics93@gmail.com",
            val idBrand: Int = 5
        ) : UIEvent()
    }
}