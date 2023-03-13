package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.networking.graphql.apollomodel.ValidateUserStatusQuery
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class ApprovedByOnfidoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess
    var userSmartAccounts: List<SmartAccountID>? = listOf()
    var accountToken: String = ""
    var idCurrency: Int = 0
    var idClient: Int = 0
    var idLoanClient: Int = 0
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idBrand: Int = 0
    var comingFromCrypto: Boolean = false

    init {
        pkUser = savedStateHandle[PK_USER] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        comingFromCrypto = savedStateHandle[COMING_FROM_CRYPTO] ?: false
    }

    private fun onNavigateToHome() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnFirsButtonClick -> onFirsButtonClick()
            is UIEvent.OnSecondButtonClick -> onSecondButtonClick()
            is UIEvent.OnCallQueryGetUserStatusInfo -> onCallQueryGetUserInfo()
            is UIEvent.OnSetUpDialogData -> onSetUpDialog()
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
                uiState.copy(userStatus = validateUserStatus)
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
                uiState = uiState.copy(isLoading = false)
                balance?.let {
                    userSmartAccounts = balance.balanceAccountSmart?.map { account ->
                        SmartAccountID(
                            tokenAccount = account?.tokenNumber,
                            currencyID = account?.idCurrencyAccount,
                            accountNumber = account?.accountNumber ?: "",
                            ibanAccountNumber = account?.ibanAccountNumber
                        )
                    }
                }
            }
            result.onFailure { onFailure(it) }
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

    private fun onFirsButtonClick() {
        if (comingFromCrypto) {
            navigateToHomeCrypto()
        } else {
            navigateToFirstSavingTransfer()
        }
    }

    private fun onSecondButtonClick() {
        if (comingFromCrypto) {
            navigateToFirstSavingTransfer()
        } else {
            onNavigateToHome()
        }
    }

    private fun navigateToHomeCrypto() {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.EXPANDED)
    }

    private fun navigateToFirstSavingTransfer() {
        if (idBrand == Brand.CostaRica.id) {
            navigateTo("${Screen.SmartPaymentOptionsScreenCR.baseRoute}/${encodeData(userSmartAccounts)}/$pkUser/$idBrand/$identification/$idClient/$idLoanClient")
        } else {
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/${encodeData(userSmartAccounts?.firstOrNull())}")
        }
    }

    private fun onSetUpDialog() {
        uiState = uiState.copy(
            alertTitleResource = if (comingFromCrypto) R.string.crypto_finish_smart_alert_title else R.string.approved_sign_by_onfido_title,
            alertButtonTextResource = if (comingFromCrypto) R.string.crypto_finish_smart_alert_btn_discover_crypto else R.string.approved_by_onfido_buttton_text,
            alertMessageResource = if (comingFromCrypto) R.string.crypto_finish_smart_alert_description else R.string.empty,
            alertSecondButtonTextResource = if (comingFromCrypto) R.string.crypto_finish_smart_alert_btn_saving_smart else R.string.finalize
        )
    }

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val alertTitleResource: Int = R.string.approved_by_onfido_title,
        val alertMessageResource: Int = R.string.empty,
        val alertButtonTextResource: Int = R.string.approved_by_onfido_buttton_text,
        val alertSecondButtonTextResource: Int = R.string.finalize,
        var userStatus: ValidateUserStatus? = null,
    )

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnFirsButtonClick : UIEvent()
        object OnSecondButtonClick : UIEvent()
        object OnCallQueryGetUserStatusInfo : UIEvent()
        object OnSetUpDialogData: UIEvent()
    }
}
