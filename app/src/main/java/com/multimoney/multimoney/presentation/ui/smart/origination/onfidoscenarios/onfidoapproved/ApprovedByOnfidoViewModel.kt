package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class ApprovedByOnfidoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess
    var userSmartAccount: String = ""
    var accountToken: String = ""
    var idCurrency: Int = 0
    var idClient: Int = 0
    var idLoanClient: Int = 0
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idBrand: Int = 0

    init {
        pkUser = savedStateHandle[PK_USER] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
    }

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ApprovedByOnfidoScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnMakeFirstSavingTransfer -> onMakeFirstSavingTransfer()
            is UIEvent.OnCallQueryGetUserStatusInfo -> onCallQueryGetUserInfo()
        }
    }

    private fun onCallQueryGetUserInfo() = executeUseCase {
        queryValidateUserStatusUseCase.invoke(
            pkUser = pkUser.toInt(),
            identification = identification,
            email = email,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess { validateUserStatus ->
                callQueryBalanceUseCase(
                    user = email,
                    identification = identification,
                    idBrand = idBrand,
                    idClient = validateUserStatus?.infoUser?.idClient ?: 0,
                    idLoanClient = validateUserStatus?.infoCredit?.idLoanClient ?: 0,
                    creditStatus = validateUserStatus?.infoCredit?.status ?: 0,
                    accountStatus = validateUserStatus?.infoBankAccount?.status ?: 0,
                    cryptoStatus = validateUserStatus?.infoCrypto?.status ?: 0,
                    cardStatus = validateUserStatus?.infoVirtualCard?.status ?: 0
                )
            }
            result.onFailure {
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callQueryBalanceUseCase(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        creditStatus: Int,
        accountStatus: Int,
        cryptoStatus: Int,
        cardStatus: Int
    ) = executeUseCase {
        queryBalanceUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            creditStatus = creditStatus,
            accountStatus = accountStatus,
            cryptoStatus = cryptoStatus,
            cardStatus = cardStatus
        ).collectLatest { result ->
            result.onSuccess { balance ->
                balance?.let {
                    balance.balanceAccountSmart?.firstOrNull()?.let { account ->
                        userSmartAccount = account.accountNumber.orEmpty()
                        accountToken = account.tokenNumber.orEmpty()
                        idCurrency = account.idCurrencyAccount ?: 0
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

    private fun onMakeFirstSavingTransfer() {
        if (idBrand == Brand.CostaRica.id) {
            navigateTo("${Screen.SmartPaymentOptionsScreenCR.baseRoute}/$email/$idBrand/$identification/${Screen.SmartPaymentOptionsScreenCR.baseRoute}/$idClient/$idLoanClient")
        } else {
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/$userSmartAccount/$accountToken/$idCurrency")
        }
    }

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnMakeFirstSavingTransfer : UIEvent()
        object OnCallQueryGetUserStatusInfo : UIEvent()
    }
}