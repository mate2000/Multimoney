package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.Transfer365Account
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnCallQueryACHTransferFavoriteListUseCase
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnOptionsClick
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SmartTransferFavoriteViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var identification: String? = ""
    var selectedSmartAccount: SmartAccountID? = null

    init {
        selectedSmartAccount = savedStateHandle[SMART_ACCOUNT]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun callQueryACHTransferFavoriteListUseCase() = executeUseCase {
        queryACHTransferFavoriteListUseCase.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand.toInt(),
            isFavorite = true
        ).collectLatest { result ->
            result.onSuccess { ACHFavoriteAccountList ->
                uiState = uiState.copy(isLoading = false)
                ACHFavoriteAccountList?.data?.let { ACHFavoriteAccounts ->
                    uiState =
                        uiState.copy(
                            ACHFavoriteAccountList = ACHFavoriteAccounts.sortedBy { ACHFavoriteAccount ->
                                ACHFavoriteAccount?.description.orEmpty()
                            }.groupBy { ACHFavoriteAccount ->
                                ACHFavoriteAccount?.accountNumber.orEmpty()
                            })
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
            isLoading = false, openDialog = DialogParameters(
                description = error.getError() ?: "", isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = navigateBack(
        popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false
    )

    private fun onShowOptionsClick(selectedACHFavorite: ACHAccount?) {
      // TODO REV-3466
    }

    private fun onACHFavoriteClick(selectedACHFavoriteAccount: ACHAccount?) {
        if (idBrand == Brand.CostaRica.id.toString()) {
            val ibanAccount = encodeData(
                IbanAccountID(
                    bank = selectedACHFavoriteAccount?.destinationBankDescription,
                    clientIdentification = selectedACHFavoriteAccount?.accountForAchTransferId.toString(),
                    sinpeAccount = selectedACHFavoriteAccount?.accountNumber,
                    currencyId = selectedACHFavoriteAccount?.destinationAccountCurrencyId,
                    nameAccount = selectedACHFavoriteAccount?.description
                )
            )
            navigateTo(
                "${Screen.SmartTransferAmountScreen.baseRoute}/" +
                        "${encodeData(selectedSmartAccount)}/$ibanAccount/" +
                        "${SmartTransferTypes.SmartToIban.id}/${Screen.SmartTransferFavoriteAccountScreen.baseRoute}"
            )
        }
        else if (idBrand == Brand.ElSalvador.id.toString()) {
            val savedAccount = Transfer365Account(
                accountNumber = selectedACHFavoriteAccount?.accountNumber,
                name = selectedACHFavoriteAccount?.description ?: "",
                bankId = selectedACHFavoriteAccount?.idBank.toString(),
                bankName = selectedACHFavoriteAccount?.destinationBankDescription ?: "",
                accountTypeId = selectedACHFavoriteAccount?.idTypeAccount.toString(),
                isFavorite = selectedACHFavoriteAccount?.isFavorite ?: true
            )
            navigateTo(
                "${Screen.SmartTransfer365EditAmountScreen.baseRoute}/${
                    encodeData(selectedSmartAccount)
                }/${encodeData(savedAccount)}/${SmartTransferTypes.SmartToOtherBank.id}/${Screen.SmartTransferFavoriteAccountScreen.baseRoute}"
            )
        }
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var ACHFavoriteAccountList: Map<String, List<ACHAccount?>> = mapOf(),
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
             is OnOptionsClick -> onShowOptionsClick(uiEvent.ACHFavorite)
            is OnNavigateBack -> onNavigateBack()
            is OnFavoriteClick -> onACHFavoriteClick(uiEvent.ACHFavorite)
            is OnNavigateToHome -> onNavigateToHome()
            OnCallQueryACHTransferFavoriteListUseCase -> callQueryACHTransferFavoriteListUseCase()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToHome : UIEvent()
        data class OnOptionsClick(val ACHFavorite: ACHAccount?) : UIEvent()
        object OnCallQueryACHTransferFavoriteListUseCase : UIEvent()
        data class OnFavoriteClick(val ACHFavorite: ACHAccount?) : UIEvent()
    }
}