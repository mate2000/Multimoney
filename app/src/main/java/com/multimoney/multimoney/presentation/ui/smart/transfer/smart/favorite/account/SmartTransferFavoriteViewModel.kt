package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnCallQueryListSinpeAccountUseCaseImpl
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartTransferFavoriteViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCaseImpl: QueryACHTransferFavoriteListUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var idClient: String = ""
    private var identification: String? = ""
    private var smartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccount = savedStateHandle[SMART_ACCOUNT]
    }

    private fun callQueryListSinpeAccountUseCaseImpl() = executeUseCase {
//        queryListSinpeAccountUseCaseImpl.invoke(
//            user = user,
//            identification = identification ?: "",
//            idBrand = idBrand.toInt(),
//            country = "",
//            idAccount = 0,
//            accountNumber = ""
//        ).collectLatest { result ->
//            result.onSuccess { accountList ->
//                uiState = uiState.copy(isLoading = false)
//                if (accountList?.data?.isEmpty() == true) {
//                    navigateToAddIbanAccount()
//                } else {
//                    accountList?.data?.let {
//                        uiState = uiState.copy(sinpeAccountList = it)
//                    }
//                }
//            }
//            result.onFailure {
//                uiState = uiState.copy(isLoading = false)
//                onFailure(it)
//            }
//            result.onLoading {
//                uiState = uiState.copy(isLoading = true)
//            }
//        }
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

    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.UNEXPANDED
        )

    private fun navigateToAddIbanAccount() {
        navigateTo(
            "${Screen.SmartTransferRegisterIbanScreen.baseRoute}/$user/$idBrand/$identification/${Screen.SmartTransferIbanAccountScreen.baseRoute}/$idClient/${
            encodeData(
                smartAccount
            )
            }"
        )
    }

    private fun onAccountClick(selectedSinpeAccount: SinpeAccount?) {
        val ibanAccount = encodeData(
            IbanAccountID(
                bank = selectedSinpeAccount?.bank,
                clientIdentification = selectedSinpeAccount?.clientIdentification,
                sinpeAccount = selectedSinpeAccount?.sinpeAccount,
                currencyId = selectedSinpeAccount?.currencyId,
                nameAccount = selectedSinpeAccount?.nameAccount
            )
        )
        navigateTo(
            "${Screen.SmartTransferAmountScreen.baseRoute}/" +
                "${encodeData(smartAccount)}/$ibanAccount/" +
                "${SmartTransferTypes.SmartToIban.id}"
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var sinpeAccountList: List<SinpeAccount?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            OnAddAccountClick -> navigateToAddIbanAccount()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
            OnCallQueryListSinpeAccountUseCaseImpl -> callQueryListSinpeAccountUseCaseImpl()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        object OnCallQueryListSinpeAccountUseCaseImpl : UIEvent()
        data class OnAccountClick(val account: SinpeAccount?) : UIEvent()
    }
}