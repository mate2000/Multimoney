package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.interaction.accountsmart.QueryLocalTransferFavoriteUseCase
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.LocalFavorite
import com.multimoney.domain.model.accountsmart.PhoneSmart
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
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnACHFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnCallLocalFavorites
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnCallQueryACHTransferFavoriteList
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnLocalFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnOptionsAchClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnOptionsLocalClick
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartTransferFavoriteSVViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
    private val queryLocalTransferFavorite: QueryLocalTransferFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var identification: String = ""
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
            identificationNumber = identification,
            idBrand = idBrand.toIntOrNull() ?: 0,
            isFavorite = true
        ).collectLatest { result ->
            result.onSuccess { aCHFavoriteAccountList ->
                uiState = uiState.copy(isLoading = false)
                aCHFavoriteAccountList?.data?.let { aCHFavoriteAccounts ->
                    uiState =
                        uiState.copy(
                            aCHFavoriteAccountList = aCHFavoriteAccounts.sortedBy { aCHFavoriteAccount ->
                                aCHFavoriteAccount?.description.orEmpty()
                            }
                        )
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

    private fun callLocalFavorites() = executeUseCase {
        queryLocalTransferFavorite.invoke(
            idBrand.toIntOrNull() ?: 0,
            user,
            true,
            selectedSmartAccount?.customerId ?: 0
        ).collectLatest { result ->
            result.onSuccess { localList ->
                uiState = uiState.copy(
                    isLoading = false,
                    localFavoriteList = localList ?: listOf()
                )
            }
            result.onLoading {
                uiState = uiState.copy(
                    isLoading = true
                )
            }
            result.onFailure {
                uiState = uiState.copy(
                    isLoading = false
                )
                onFailure(it)
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

    private fun onNavigateBack() = navigateBack(
        popTo = Screen.SmartSelectSendingTypeScreen.route,
        isRestart = false
    )

    private fun onOptionsAchClick(selectedACHFavorite: ACHAccount?) {
        // TODO REV-345
    }

    private fun onOptionsLocalClick(localFavorite: LocalFavorite?) {
        // TODO REV-345
    }

    private fun onLocalFavoriteClick(localFavorite: LocalFavorite?) {
        val phoneAccount = PhoneSmart(
            number = localFavorite?.phoneNumber,
            titular = localFavorite?.accountName.orEmpty(),
            bankName = "",
            identification = identification,
            accountNumber = localFavorite?.accountNumber,
            email = localFavorite?.email,
            idCurrency = localFavorite?.idCurrencyAccount.toString(),
            currency = localFavorite?.currencyAccount,
            ibanNumber = localFavorite?.ibanNumber
        )
        navigateTo(
            "${Screen.MyContactsTransferAmountScreen.baseRoute}/${encodeData(selectedSmartAccount)}/" +
                "${encodeData(phoneAccount)}/${SmartTransferTypes.SmartToContact.id}/" +
                Screen.SmartTransferFavoriteAccountSVScreen.baseRoute
        )
    }

    private fun onACHFavoriteClick(selectedACHFavoriteAccount: ACHAccount?) {
        val achAccount = Transfer365Account(
            accountNumber = selectedACHFavoriteAccount?.accountNumber,
            name = selectedACHFavoriteAccount?.description ?: "",
            bankId = selectedACHFavoriteAccount?.idBank.toString(),
            bankName = selectedACHFavoriteAccount?.destinationBankDescription ?: "",
            accountTypeId = selectedACHFavoriteAccount?.idTypeAccount.toString(),
            isFavorite = selectedACHFavoriteAccount?.isFavorite ?: true
        )
        navigateTo(
            "${Screen.SmartTransfer365EditAmountScreen.baseRoute}/${encodeData(selectedSmartAccount)}/" +
                "${encodeData(achAccount)}/${SmartTransferTypes.SmartToOtherBank.id}/" +
                Screen.SmartTransferFavoriteAccountSVScreen.baseRoute
        )
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
        val isLoading: Boolean = false,
        val aCHFavoriteAccountList: List<ACHAccount?> = listOf(),
        val localFavoriteList: List<LocalFavorite?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnOptionsAchClick -> onOptionsAchClick(uiEvent.aCHFavorite)
            is OnNavigateBack -> onNavigateBack()
            is OnACHFavoriteClick -> onACHFavoriteClick(uiEvent.aCHFavorites)
            is OnNavigateToHome -> onNavigateToHome()
            is OnCallQueryACHTransferFavoriteList -> callQueryACHTransferFavoriteListUseCase()
            is OnCallLocalFavorites -> callLocalFavorites()
            is OnLocalFavoriteClick -> onLocalFavoriteClick(uiEvent.localFavorite)
            is OnOptionsLocalClick -> onOptionsLocalClick(uiEvent.localFavorite)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnCallQueryACHTransferFavoriteList : UIEvent()
        object OnCallLocalFavorites : UIEvent()
        data class OnOptionsAchClick(val aCHFavorite: ACHAccount?) : UIEvent()
        data class OnACHFavoriteClick(val aCHFavorites: ACHAccount?) : UIEvent()
        data class OnLocalFavoriteClick(val localFavorite: LocalFavorite?) : UIEvent()
        data class OnOptionsLocalClick(val localFavorite: LocalFavorite?) : UIEvent()
    }
}
