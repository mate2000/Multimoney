package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnChipQuotaClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnLastStepChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToProfileScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToVisaActivateScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.*
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val helper: ShareHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var lastStep: Int = 1
    var balanceCredit: Balance? = null
    var configurationVersion: ConfigurationVersion? = null
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var userName: String = ""
    var productProgress = 0F
    var isExpiredTitle = R.string.home_product_expiration

    private fun onSetUserData(
        idBrand: String,
        balanceCredit: Balance?,
        pkUser: String,
        identification: String,
        email: String,
        userName: String,
        validateUserStatus: ValidateUserStatus?,
        configurationVersion: ConfigurationVersion?
    ) {
        this.pkUser = pkUser
        this.identification = identification
        this.email = email
        this.userName = userName
        this.configurationVersion = configurationVersion
        uiState = uiState.copy(idBrand = idBrand)
        setBalance(balanceCredit)
        setValidateUserStatus(validateUserStatus)
    }

    private fun setBalance(balance: Balance?) {
        balance?.let {
            balanceCredit = it

            val productPageList = mutableListOf<ProductPage>()

            // Default credit
            productPageList.add(
                ProductPage(
                    product = ProductType.Credit.value,
                    enabled = true
                )
            )
            // If idBrand is different from Guatemala enable Smart
            if (uiState.idBrand != Brand.Guatemala.id.toString()) {
                if (it.balanceAccountSmart.isNullOrEmpty().not()) {
                    // Add the amount of account smart that user has
                    it.balanceAccountSmart?.forEachIndexed { index, _ ->
                        productPageList.add(
                            ProductPage(
                                product = ProductType.Smart.value,
                                productSmartIndex = index,
                                enabled = true
                            )
                        )
                    }
                    // If user has smart activated he can enable crypto
                    productPageList.add(
                        ProductPage(
                            product = ProductType.Crypto.value,
                            enabled = true
                        )
                    )
                } else {
                    // If user doesn't have smart we have to add one empty card to activate the product
                    productPageList.add(
                        ProductPage(
                            product = ProductType.Smart.value,
                            enabled = true
                        )
                    )
                    productPageList.add(
                        ProductPage(
                            product = ProductType.Crypto.value,
                            enabled = false
                        )
                    )
                }
            }
            uiState = uiState.copy(
                productPageList = productPageList,
                canExpandCredit = it.getFirstSummary()?.canExpandState ?: false && it.getFirstSummary()?.isProductActive ?: false,
                scheduleChipIconResource = if ((balanceCredit?.getExpiredDays() ?: 0) > 0) {
                    R.drawable.ic_alert_expired_payment
                } else if (balanceCredit?.isBalanceCreditSummaryMultiple() == true) {
                    R.drawable.info_blue_icon
                } else {
                    null
                }
            )
        }
    }

    private fun setValidateUserStatus(userStatus: ValidateUserStatus?) {
        lastStep = CreditStep.Search.getIdByName(userStatus?.infoCredit?.infoPreApprove?.currentStep)
        uiState = uiState.copy(userStatus = userStatus)
    }

    private fun onNavigateToCreditScreen(creditStep: String) {
        when (creditStep) {
            CREDIT_FIRM_INCOMPLETE, CREDIT_ONFIDO_REJECTED, CREDIT_FIRM_REJECTED -> {
                // todo call the new endpoint to get the evicertia url
            }

            else -> {
                navigateTo(
                    "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                        "${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}/${uiState.userStatus?.infoUser?.firstName}/" +
                        "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/" +
                        "${uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint}"
                )
            }
        }
    }

    private fun onNavigateToSmartFlow() {
        navigateTo("${Screen.SmartScreen.baseRoute}/$userName/${uiState.idBrand}/$pkUser")
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
            }/$identification/$userName/${balanceCredit?.getFirstSummary()?.paymentDate}"
        } else if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            "${Screen.PaymentAccountScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(
                listOf(creditSummary?.firstOrNull { (it.currentBalance ?: ZERO) > ZERO })
            )
            }/$identification/$userName/${balanceCredit?.getFirstSummary()?.paymentDate}/${Screen.HomeScreen.route}"
        } else {
            "${Screen.PaymentOptionsScreen.baseRoute}/${uiState.idBrand}/${balanceCredit?.getFirstCredit()?.creditNumber}/${
            encodeData(configurationVersion?.configuration?.credit?.paymentMethod?.filter { it?.active == true })
            }/${encodeData(configurationVersion?.configuration?.credit?.transferAccount)}" +
                "/${balanceCredit?.getFirstSummary()?.minPaymentLabel}/$identification/$email"
        }
        navigateTo(route)
    }

    private fun onNavigateToAutomaticPaymentScheduleScreen() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo(
            route = "${Screen.PaymentScheduleScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(
                ClientBankAccount()
            )
            }/${balanceCredit?.getFirstSummary()?.paymentDate}/${false}/${Screen.HomeScreen.route}/${false}"
        )
    }

    private fun onChipQuotaClick() {
        uiState = uiState.copy(
            openDialog = if ((balanceCredit?.getExpiredDays() ?: 0) > 0) {
                DialogParameters(
                    titleResource = R.string.schedule_automatic_payment_credit_expired_payment_dialog_title,
                    descriptionResource = R.string.schedule_automatic_payment_credit_expired_payment_dialog_description,
                    positiveResource = R.string.schedule_automatic_payment_credit_expired_payment_dialog_button,
                    isActive = mutableStateOf(true)
                )
            } else if (balanceCredit?.isBalanceCreditSummaryMultiple() == true) {
                DialogParameters(
                    titleResource = R.string.schedule_automatic_payment_credit_multiple_payment_dialog_title,
                    descriptionResource = R.string.schedule_automatic_payment_credit_multiple_payment_dialog_description,
                    positiveResource = R.string.schedule_automatic_payment_credit_multiple_payment_dialog_button,
                    isActive = mutableStateOf(true)
                )
            } else {
                DialogParameters()
            }
        )
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

    private fun onNavigateToProfileScreen() {
        navigateTo("${Screen.ProfileScreen.baseRoute}/${uiState.idBrand}")
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

                CREDIT_INFO_INCOMPLETE -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status &&
                        infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.PENDING.status &&
                        (CreditStep.Search.getIdByName(infoCredit?.infoPreApprove?.currentStep) < CreditStep.Eight.id)
                }

                CREDIT_IDENTITY_INCOMPLETE -> {
                    infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status && (
                        CreditStep.Search.getIdByName(infoCredit?.infoPreApprove?.currentStep) == CreditStep.Eight.id
                        )
                }

                CREDIT_FIRM_INCOMPLETE -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.PENDING.status &&
                        infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.PENDING.status
                }

                CREDIT_FIRMED_ONFIDO_PENDING -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.FIRMED.status &&
                        infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING?.status
                }

                CREDIT_FIRM_REJECTED -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.REJECTED.status
                }

                CREDIT_FIRM_MAX_ATTEMPTS -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.OVER_COUNTER.status
                }

                CREDIT_ONFIDO_REJECTED -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.REJECTED.status
                }

                CREDIT_ONFIDO_MAX_ATTEMPTS -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.OVER_COUNTER.status
                }

                CREDIT_ERROR_CREATE_ACCOUNT -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.FAILED.status ||
                        infoCredit?.infoPreApprove?.status == ERROR_CREDIT
                }

                else -> false
            }
        }
    }

    private fun shareIbanAccount(clientLabel: String, accountLabel: String, ibanAccount: String) {
        helper.shareTextPlain("$clientLabel: ${userName.uppercase()}\n$accountLabel: $ibanAccount")
    }

    private fun onNavigateToDisbursement() =
        navigateTo(
            route = "${Screen.DisbursementAmountScreen.baseRoute}/${uiState.idBrand}/$email/${uiState.userStatus?.infoCredit?.idClient}/${
            encodeData(
                balanceCredit?.getFirstCredit()?.summary
            )
            }/$pkUser/${balanceCredit?.getFirstCredit()?.creditNumber}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}"
        )

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
                "2",
                "Solicitar Credito",
                "4,000",
                "Solicitar",
                "",
                ""
            ),
            CreditOfferAndTip(
                "3",
                "Solicitar Credito",
                "4,000",
                "Solicitar",
                "",
                ""
            )
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

    fun canSendMoney(smartAccountIndex: Int?): Boolean {
        val balanceSmart = balanceCredit?.balanceAccountSmart ?: emptyList()
        val totalBalance = if (smartAccountIndex != null && balanceSmart.size > smartAccountIndex) {
            balanceCredit?.balanceAccountSmart?.get(smartAccountIndex)?.totalBalance ?: 0.0
        } else {
            0.0
        }
        return totalBalance > 0
    }

    fun getSchedulePaymentAmount(balance: Balance?): String {
        var amount = ""
        balance?.balanceCredit?.forEach { balanceCredit ->
            if (balance.isBalanceCreditSummaryMultiple()) {
                balanceCredit?.summary?.forEachIndexed { index, summary ->
                    amount = if (index < (balanceCredit.summary?.lastIndex ?: 0)) {
                        amount.plus(summary.monthlyQuotaLabel).plus(
                            SEPARATOR
                        )
                    } else {
                        amount.plus(summary.monthlyQuotaLabel)
                    }
                }
            } else if (balanceCredit?.summary?.isNotEmpty() == true && balanceCredit.summary?.firstOrNull() != null) {
                val summary = balanceCredit.summary?.first()
                amount = summary?.monthlyQuotaLabel.orEmpty()
            }
        }
        return amount
    }

    private fun onQuickActionClicked(flow: String) {
        when (flow) {
            QuickActionFlow.ACTIVATE_MM_VISA.flow -> onNavigateToVisaActivateScreen()
            QuickActionFlow.PAY_FEE.flow -> onNavigateToPaymentScreen()
        }
    }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var userStatus: ValidateUserStatus? = null,
        var productPageList: List<ProductPage>? = null,
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isExpanded: Boolean = false,
        val canExpandCredit: Boolean = false,
        val scheduleChipIconResource: Int? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnUpdateIsExpanded -> uiState = uiState.copy(isExpanded = uiEvent.isExpanded)
            is OnBalanceSuccess -> balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> setValidateUserStatus(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen(uiEvent.creditStep)
            is OnNavigateToSmartOriginationFlow -> onNavigateToSmartFlow()
            is OnNavigateToPaymentProcess -> onNavigateToPaymentScreen()
            is UIEvent.OnNavigateToSendMoneyFlow -> onNavigateToSendMoneyScreen()
            is UIEvent.OnNavigateToPaymentSmartFlow -> onNavigateToPaymentSmartScreen()
            is OnNavigateToVisaActivateScreen -> onNavigateToVisaActivateScreen()
            is OnNavigateToProfileScreen -> onNavigateToProfileScreen()
            is OnNavigateToDisbursement -> onNavigateToDisbursement()
            is OnSetUserData -> onSetUserData(
                idBrand = uiEvent.idBrand,
                balanceCredit = uiEvent.balanceCredit,
                pkUser = uiEvent.pkUser,
                identification = uiEvent.identification,
                email = uiEvent.email,
                userName = uiEvent.userName,
                validateUserStatus = uiEvent.validateUserStatus,
                configurationVersion = uiEvent.configurationVersion
            )
            is OnMaxAttemptsCardClick -> openWhatsAppLink(
                uiEvent.context,
                uiEvent.whatsAppLink
            )

            is OnLastStepChange -> lastStep = uiEvent.lastStep
            is OnShareIbanAccount -> shareIbanAccount(
                uiEvent.clientLabel,
                uiEvent.accountLabel,
                uiEvent.ibanAccount
            )
            is OnProgressCalculation -> getProgress()
            is IsPaymentExpired -> isExpired()
            is OnChipQuotaClick -> onChipQuotaClick()
            is OnNavigateToScheduleAutomaticPaymentScreen -> onNavigateToAutomaticPaymentScheduleScreen()
            is UIEvent.OnQuickActionClicked -> onQuickActionClicked(uiEvent.flow)
        }
    }

    private fun onNavigateToPaymentSmartScreen() {
        // TODO: Navigate to PaymentSmart screen
    }

    private fun onNavigateToSendMoneyScreen() {
        // TODO: Navigate to SendMoney screen
    }

    sealed class UIEvent {
        data class OnUpdateIsExpanded(val isExpanded: Boolean) : UIEvent()
        data class OnBalanceSuccess(val balance: Balance) : UIEvent()
        data class OnValidateUserSuccess(val userStatus: ValidateUserStatus) : UIEvent()
        data class OnMaxAttemptsCardClick(
            val whatsAppLink: String,
            val context: Context
        ) : UIEvent()

        data class OnLastStepChange(val lastStep: Int) : UIEvent()
        object OnNavigateToSmartOriginationFlow : UIEvent()
        object OnNavigateToPaymentProcess : UIEvent()
        object OnNavigateToVisaActivateScreen : UIEvent()
        object OnNavigateToProfileScreen : UIEvent()
        object OnNavigateToDisbursement : UIEvent()
        object OnNavigateToSendMoneyFlow : UIEvent()
        object OnNavigateToPaymentSmartFlow : UIEvent()
        object OnProgressCalculation : UIEvent()
        object IsPaymentExpired : UIEvent()
        data class OnNavigateToCreditScreen(val creditStep: String) : UIEvent()
        object OnChipQuotaClick : UIEvent()
        object OnNavigateToScheduleAutomaticPaymentScreen : UIEvent()

        data class OnSetUserData(
            val idBrand: String,
            val balanceCredit: Balance?,
            val pkUser: String,
            val identification: String,
            val email: String,
            val userName: String,
            val validateUserStatus: ValidateUserStatus?,
            val configurationVersion: ConfigurationVersion?
        ) : UIEvent()

        data class OnShareIbanAccount(
            val clientLabel: String,
            val accountLabel: String,
            val ibanAccount: String
        ) : UIEvent()

        data class OnQuickActionClicked(val flow: String) : UIEvent()
    }

    companion object {
        const val ERROR_CREDIT = "Error"
        const val DEFAULT_PRODUCT_PAGES = 1
        const val DEFAULT_PROGRESS = 1F
        const val ZERO = 0.0
        const val CREDIT_STEP_PRE_APPROVED = "CREDIT_STEP_PREAPROBADO"
        const val CREDIT_INITIAL_CARD = "CREDIT_INITIAL_CARD"
        const val CREDIT_FIRM_MAX_ATTEMPTS = "CREDIT_MAX_ATTEMPTS"
        const val CREDIT_IDENTITY_INCOMPLETE = "CREDIT_IDENTITY_INCOMPLETE"
        const val CREDIT_INFO_INCOMPLETE = "CREDIT_INFO_INCOMPLETE"
        const val CREDIT_FIRM_INCOMPLETE = "CREDIT_FIRM_INCOMPLETE"
        const val CREDIT_FIRM_REJECTED = "CREDIT_REJECTED"
        const val CREDIT_FIRMED_ONFIDO_PENDING = "CREDIT_FIRMED_ONFIDO_PENDING"
        const val CREDIT_ONFIDO_REJECTED = "CREDT_ONFIFO_REJECTED"
        const val CREDIT_ONFIDO_MAX_ATTEMPTS = "CREDIT_ONFIDO_MAX_ATTEMPTS"
        const val CREDIT_ERROR_CREATE_ACCOUNT = "CREDIT_ERROR_CREATE_ACCOUNT"
        const val SEPARATOR = " + "
    }
}
