package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.domain.model.credit.ProductMovement
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetIdBrand
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnLastStepChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToVisaActivateScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.sendAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var lastStep: Int = 1
    var balanceCredit: Balance? = null
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var userName: String = ""
    var productProgress = 0F
    var isExpiredTitle = R.string.home_product_expiration

    private fun onGetUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(idBrand = dataStorePreferences.getIdBrand().first())
            pkUser = dataStorePreferences.getPkUser().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            userName = dataStorePreferences.getUserName().first()

            callQueryValidateUserStatus(
                pkUser.toInt(),
                identification,
                email,
                uiState.idBrand.toInt()
            )
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
    ) {
        executeUseCase {
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
                        balanceCredit = it
                        val currentBalance = it.getFirstSummary()?.currentBalance ?: 0.0
                        uiState = uiState.copy(hasBalance = (currentBalance > 0.0))
                    }
                }
                result.onFailure {
                    onFailure(it)
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
        lastStep = CreditStep.Search.getIdByName(userStatus.infoCredit?.infoPreApprove?.currentStep)
        uiState = uiState.copy(userStatus = userStatus)
        callQueryBalanceUseCase(
            user = email,
            identification = identification,
            idBrand = uiState.idBrand.toInt(),
            idClient = uiState.userStatus?.infoUser?.idClient ?: 0,
            idLoanClient = uiState.userStatus?.infoCredit?.idLoanClient ?: 0,
            creditStatus = uiState.userStatus?.infoCredit?.status ?: 0,
            accountStatus = uiState.userStatus?.infoBankAccount?.status ?: 0,
            cryptoStatus = uiState.userStatus?.infoCrypto?.status ?: 0,
            cardStatus = uiState.userStatus?.infoVirtualCard?.status ?: 0
        )
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

    private fun onNavigateToCreditScreen() {
        navigateTo(
            "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}"
        )
    }

    private fun onNavigateToPaymentScreen() {
        val creditSummary = balanceCredit?.balanceCredit?.first()?.summary
        val infoCredit = uiState.userStatus?.infoCredit
        val route = if ((
            creditSummary?.size
                ?: 0
            ) > 1 && validateQuotas(creditSummary) && uiState.idBrand.toInt() == Brand.CostaRica.id
        ) {
            "${Screen.PaymentFeeScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(
                creditSummary
            )
            }"
        } else if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            "${Screen.PaymentAccountScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(
                listOf(creditSummary?.firstOrNull { (it.currentBalance ?: ZERO) > ZERO })
            )
            }"
        } else {
            // TODO: Send to appropriate screen when is implemented
            "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}"
        }
        navigateTo(route)
    }

    private fun validateQuotas(summaryList: List<Summary>?): Boolean {
        summaryList?.let {
            for (summary in summaryList) {
                if (summary.currentBalance == ZERO) {
                    return false
                }
            }
            return true
        } ?: kotlin.run {
            return false
        }
    }

    private fun onNavigateToVisaActivateScreen() =
        navigateTo("${Screen.VisaIssuanceScreen.baseRoute}/${uiState.idBrand}")

    private fun onProductClick(context: Context, whatsAppLink: String) {
        when {
            uiState.userStatus?.infoCredit?.status == CreditStatus.CREDIT_NOT_PRE_APPROVED.status || uiState.userStatus?.infoCredit?.status == CreditStatus.CREDIT_REJECTED.status -> {
                openWhatsAppLink(context, whatsAppLink)
            }
            uiState.userStatus?.infoCredit?.status == CreditStatus.APPROVED_CREDIT.status -> onNavigateToCreditScreen()
            uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm != CreditOnFidoOrFirmStatus.APPROVED.status -> onNavigateToCreditScreen()
            else -> navigateTo(Screen.CreditScreen.route)
        }
    }

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
    }

    private fun getProgress() {
        productProgress = (
            balanceCredit?.getFirstSummary()?.currentBalance?.toFloat()
                ?: DEFAULT_PROGRESS
            ) / (
            balanceCredit?.getFirstCredit()?.creditLimit?.toFloat()
                ?: DEFAULT_PROGRESS
            )
    }

    private fun isExpired() {
        isExpiredTitle = if ((
            balanceCredit?.getFirstSummary()?.daysExpired
                ?: 0
            ) > 0
        ) R.string.home_product_expired else R.string.home_product_expiration
    }

    fun evaluateCardCondition(action: String, validateUserStatus: ValidateUserStatus): Boolean {
        validateUserStatus.apply {
            return when (action) {
                CREDIT_INITIAL_CARD -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status &&
                        infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.PENDING.status &&
                        (infoCredit?.infoPreApprove?.currentStep.isNullOrEmpty() || validateUserStatus.infoCredit?.infoPreApprove?.currentStep == CREDIT_STEP_PRE_APPROVED)
                }
                CREDIT_MAX_ATTEMPTS -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.OVER_COUNTER.status
                }
                CREDIT_IDENTITY_INCOMPLETE -> {
                    (infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status) && (
                        CreditStep.Search.getIdByName(
                            infoCredit?.infoPreApprove?.currentStep
                        ) == CreditStep.Seven.id
                        )
                }
                CREDIT_INFO_INCOMPLETE -> {
                    (infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status) && (
                        CreditStep.Search.getIdByName(
                            infoCredit?.infoPreApprove?.currentStep
                        ) < CreditStep.Seven.id
                        )
                }
                CREDIT_REJECTED -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.REJECTED.status
                }
                else -> false
            }
        }
    }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var userStatus: ValidateUserStatus? = null,
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var hasBalance: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBalanceSuccess -> balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> onValidateUserStatusSuccess(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen()
            is OnNavigateToPaymentProcess -> onNavigateToPaymentScreen()
            is OnNavigateToVisaActivateScreen -> onNavigateToVisaActivateScreen()
            is OnProductClick -> onProductClick(uiEvent.context, uiEvent.whatsAppLink)
            is OnGetIdBrand -> onGetUserData()
            is OnMaxAttemptsCardClick -> openWhatsAppLink(
                uiEvent.context,
                uiEvent.whatsAppLink
            )
            is OnLastStepChange -> lastStep = uiEvent.lastStep
            is OnShareIbanAccount -> shareIbanAccount(
                uiEvent.context,
                uiEvent.account
            )
            OnProgressCalculation -> getProgress()
            IsPaymentExpired -> isExpired()
        }
    }

    private fun shareIbanAccount(context: Context, account: String) {
        context.sendAccount(userName, account)
    }

    sealed class UIEvent {
        data class OnBalanceSuccess(val balance: Balance) : UIEvent()
        data class OnValidateUserSuccess(val userStatus: ValidateUserStatus) : UIEvent()
        data class OnMaxAttemptsCardClick(
            val whatsAppLink: String,
            val context: Context
        ) : UIEvent()

        data class OnLastStepChange(val lastStep: Int) : UIEvent()
        object OnNavigateToCreditScreen : UIEvent()
        object OnNavigateToPaymentProcess : UIEvent()
        object OnNavigateToVisaActivateScreen : UIEvent()
        object OnProgressCalculation : UIEvent()
        object IsPaymentExpired : UIEvent()
        data class OnProductClick(
            val whatsAppLink: String,
            val context: Context
        ) : UIEvent()

        object OnGetIdBrand : UIEvent()
        data class OnShareIbanAccount(
            val context: Context,
            val account: String
        ) : UIEvent()
    }

    fun getCreditOfferAndTips(): List<CreditOfferAndTip> {
        return listOf(
            CreditOfferAndTip(
                "1",
                "Ahorra Smart",
                "La mejor tasa del 3.5% anual",
                "Solicitar",
                "",
                ""
            ),
            CreditOfferAndTip(
                "1",
                "Ahorra Smart",
                "La mejor tasa del 3.5% anual",
                "Solicitar",
                "",
                ""
            ),
            CreditOfferAndTip(
                "1",
                "Ahorra Smart",
                "La mejor tasa del 3.5% anual",
                "Solicitar",
                "",
                ""
            )
        )
    }

    fun getProductMovement(): List<ProductMovement> {
        return listOf(
            ProductMovement("Pago de cuota", "10/06/2022", "3000"),
            ProductMovement("Pago de cuota", "10/06/2022", "3000"),
            ProductMovement("Pago de cuota", "10/06/2022", "3000")
        )
    }

    fun getCreditBalanceLabel(balanceCredit: List<BalanceCredit?>?): String {
        var amount = ""
        balanceCredit?.forEach { balance ->
            amount = balance?.summary?.filter { it.currentBalance != ZERO }
                ?.joinToString(separator = SEPARATOR) { summary ->
                    summary.currentBalanceLabel ?: ""
                } ?: ""
        }
        return amount
    }

    fun getQuota(balanceCredit: List<BalanceCredit?>?): String {
        var amount = ""
        balanceCredit?.forEach { balance ->
            amount = balance?.summary?.filter { it.currentBalance != ZERO }
                ?.joinToString(separator = SEPARATOR) { summary ->
                    summary.monthlyQuotaLabel ?: ""
                } ?: ""
        }
        return amount
    }

    fun getMinPayment(balanceCredit: List<BalanceCredit?>?): String {
        var amount = ""
        balanceCredit?.forEach { balance ->
            amount = balance?.summary?.filter { it.currentBalance != ZERO }
                ?.joinToString(separator = SEPARATOR) { summary ->
                    summary.minPaymentLabel ?: ""
                } ?: ""
        }
        return amount
    }

    companion object {
        const val DEFAULT_PROGRESS = 1F
        const val ZERO = 0.0
        const val CREDIT_STEP_PRE_APPROVED = "CREDIT_STEP_PREAPROBADO"
        const val CREDIT_INITIAL_CARD = "CREDIT_INITIAL_CARD"
        const val CREDIT_MAX_ATTEMPTS = "CREDIT_MAX_ATTEMPTS"
        const val CREDIT_IDENTITY_INCOMPLETE = "CREDIT_IDENTITY_INCOMPLETE"
        const val CREDIT_INFO_INCOMPLETE = "CREDIT_INFO_INCOMPLETE"
        const val CREDIT_REJECTED = "CREDIT_REJECTED"
        const val SEPARATOR = " + "
    }
}
