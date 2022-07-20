package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
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

    var onSuccessBalance by mutableStateOf<Balance?>(null)

    fun callQueryBalanceUseCase() {
        viewModelScope.launch {
            queryBalanceUseCase.invoke(
                user = "ecruzGrapqhql",
                identification = "303190775",
                idBrand = Brand.Revamp.id,
                idClient = "192656",
                idLoanClient = 223034
            ).collectLatest { result ->
                result.onSuccess {
                    isLoading = false
                    onSuccessBalance = it
                }
                result.onFailure {
                    isLoading = false
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }


    fun callQueryValidateUserStatus(
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
                result.onSuccess {
                    isLoading = false
                }
                result.onFailure {
                    isLoading = false
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }
}