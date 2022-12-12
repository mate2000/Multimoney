package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCase
import com.multimoney.domain.interaction.mmvisa.QueryCardIssuanceNVUseCase
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnChipQuotaClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCloseCardIssuanceError
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnLastStepChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMiniCardsClicked
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditMovementsScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToProfileScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartMovements
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.QuickActionFlow
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val helper: ShareHelper,
    private val nfcHelper: NfcHelper,
    private val cardIssuanceNVUseCase: QueryCardIssuanceNVUseCase,
    private val balanceCardInformationUseCase: QueryBalanceCardInformationUseCase
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
    val firstName : String? = null
    var isExpiredTitle = R.string.home_product_expiration
    var smartMovementsList: List<SmartMovementsResult> = emptyList()

    private fun onSetUserData(
        idBrand: String,
        balanceCredit: Balance?,
        pkUser: String,
        identification: String,
        email: String,
        userName: String,
        validateUserStatus: ValidateUserStatus?,
        configurationVersion: ConfigurationVersion?,
        productPageList: List<ProductPage>,
        smartMovements: List<SmartMovementsResult>
    ) {
        this.pkUser = pkUser
        this.identification = identification
        this.email = email
        this.userName = userName
        this.configurationVersion = configurationVersion
        uiState = uiState.copy(idBrand = idBrand, productPageList = productPageList)
        setBalance(balanceCredit)
        setValidateUserStatus(validateUserStatus)
        this.smartMovementsList = smartMovements
    }

    private fun setBalance(balance: Balance?) {
        balance?.let {
            balanceCredit = it
            val isCreditAvailable =
                (balanceCredit?.getFirstSummary()?.availableBalance ?: 0.0) > 0.0
            uiState = uiState.copy(
                canExpandCredit = it.getFirstSummary()?.canExpandState ?: false && it.getFirstSummary()?.isProductActive ?: false,
                scheduleChipIconResource = if ((balanceCredit?.getExpiredDays() ?: 0) > 0) {
                    R.drawable.ic_alert_expired_payment
                } else if (balanceCredit?.isBalanceCreditSummaryMultiple() == true) {
                    R.drawable.info_blue_icon
                } else {
                    null
                },
                isCreditAvailable = isCreditAvailable,
                onGoingCreditCardTitle = if (isCreditAvailable) {
                    R.string.home_product_title
                } else {
                    R.string.detail
                }
            )
        }
    }

    private fun setValidateUserStatus(userStatus: ValidateUserStatus?) {
        lastStep =
            CreditStep.Search.getIdByName(userStatus?.infoCredit?.infoPreApprove?.currentStep)
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
        navigateTo(
            Screen.AnswerQuestionsScreen.baseRoute + "/" + pkUser + "/" + email + "/" + uiState.idBrand + "/" + (uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest
                ?: 0)
        )
        /*navigateTo(
            "${Screen.SmartScreen.baseRoute}/$userName/${uiState.idBrand}/$pkUser/$identification/$email/" +
                    "${uiState.userStatus?.infoUser?.firstName}/" +
                    "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}"
        )*/
    }

    private fun onNavigateToSmartSave() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo("${Screen.SmartPaymentAccountScreen.baseRoute}/$email/${uiState.idBrand}/$identification/${Screen.SmartPaymentAccountScreen.baseRoute}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}")
        //navigateTo("${Screen.SmartScreen.baseRoute}/$userName/${uiState.idBrand}/$pkUser")
        navigateTo(
            Screen.AnswerQuestionsScreen.baseRoute + "/" + pkUser + "/" + email + "/" + uiState.idBrand + "/" + (uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest
                ?: 0)
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
        navigateTo("${Screen.VisaIssuanceScreen.baseRoute}/${uiState.idBrand}/${encodeData(balanceCredit?.balanceCardInformation)}")

    private fun onNavigateToHomeMultimoneyVisa() =
        navigateTo("${Screen.VisaCardScreen.baseRoute}/${uiState.idBrand}/${encodeData(balanceCredit?.balanceCardInformation)}")

    private fun onNavigateToProfileScreen() {
        navigateTo("${Screen.ProfileScreen.baseRoute}/${uiState.idBrand}/${uiState.userStatus?.infoUser?.firstName}/${email}/${uiState.userStatus?.infoUser?.phone}/${identification}/${uiState.idBrand}/${uiState.userStatus?.infoUser?.userName}")
    }

    private fun onNavigateToSmartMovements(accountToken: String) =
        navigateTo("${Screen.SmartMovementsScreen.baseRoute}/$userName/${uiState.idBrand}/$identification/$accountToken")

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
    }

    private fun onNavigateToCreditMovements() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo("${Screen.CreditMovementsScreen.baseRoute}/${uiState.idBrand}/${infoCredit?.idLoanClient ?: 0}/$email/${balanceCredit?.getFirstCredit()?.creditNumber}")
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
        isExpiredTitle = if ((balanceCredit?.getFirstSummary()?.daysExpired ?: 0) > 0) R.string.home_product_expired else R.string.home_product_expiration
    }

    private fun getIfIsPep() =
        uiState.userStatus?.infoCredit?.infoPreApprove?.status == PENDING_TO_CHECK_STATUS

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

                CREDIT_EL_SALVADOR_MANUAL_PROCESS -> {
                    uiState.idBrand.toInt() == Brand.ElSalvador.id && infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.APPROVED.status
                }

                CREDIT_PEP_PROCESS -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.APPROVED.status && infoCredit?.infoPreApprove?.idPrint == 0L && getIfIsPep()
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
            Screen.AnswerQuestionsScreen.baseRoute + "/" + pkUser + "/" + email + "/" + uiState.idBrand + "/" + (uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest
                ?: 0)
        )
        /*navigateTo(
            route = "${Screen.DisbursementAmountScreen.baseRoute}/${uiState.idBrand}/$email/${uiState.userStatus?.infoCredit?.idClient}/${
                encodeData(
                    balanceCredit?.getFirstCredit()?.summary
                )
            }/$pkUser/${balanceCredit?.getFirstCredit()?.creditNumber}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/$identification"
        )*/

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

    private fun onQuickActionClicked(flow: String, onLoadingValueChange: (isLoading: Boolean) -> Unit) {
        when (flow) {
            QuickActionFlow.ACTIVATE_MM_VISA.flow -> onCreateMultimoneyVisa(onLoadingValueChange)
            QuickActionFlow.PAY_FEE.flow -> onNavigateToPaymentScreen()
            QuickActionFlow.SAVE_SMART.flow -> onNavigateToSmartSave()
        }
    }

    private fun onDeleteAutomaticPayment(onAcceptClick: () -> Unit) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.automatic_payment_edit_bottom_sheet_delete_dialog_title,
                descriptionResource = R.string.automatic_payment_edit_bottom_sheet_delete_dialog_description,
                positiveResource = R.string.automatic_payment_edit_bottom_sheet_delete_dialog_accept,
                negativeResource = R.string.cancel,
                positiveAction = { onAcceptClick() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateToPaymentSmartScreen(account: Account?) {
        navigateTo("${Screen.SmartPaymentScreen.baseRoute}/${account?.accountNumber}")
    }

    private fun onNavigateToSendMoneyScreen() {
        // FIXME: Navigate to correct payment flow screen
        navigateTo("${Screen.PaymentSmartCardsScreen.baseRoute}/$userName/${uiState.idBrand}/$identification")
    }

    private fun onCreateMultimoneyVisa(onLoadingValueChange: (isLoading: Boolean) -> Unit) {
        // todo request token to know if the user already has a device enrolled
        executeUseCase {
            cardIssuanceNVUseCase.invoke(
                idClient = uiState.userStatus?.infoUser?.idClient?.toLong() ?: 0,
                identification = identification,
                idLoanClient = uiState.userStatus?.infoCredit?.idLoanClient ?: 0,
                user = email,
                idBrand = uiState.idBrand.toInt()
            ).collectLatest { result ->
                result.onSuccess {
                    onCallQueryBalanceCardInformation(onLoadingValueChange)
                }.onFailure {
                    onLoadingValueChange(false)
                    uiState = uiState.copy(showCardIssuanceError = true)
                }.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }
    }

    fun getCardIssuanceDescriptionError() = if (uiState.idBrand.toInt() == Brand.Guatemala.id) {
        R.string.card_issuance_error_description_gt
    } else {
        R.string.card_issuance_error_description
    }

    private fun onCallQueryBalanceCardInformation(onLoadingValueChange: (isLoading: Boolean) -> Unit) {
        executeUseCase {
            balanceCardInformationUseCase.invoke(
                email,
                identification,
                uiState.idBrand.toInt(),
                uiState.userStatus?.infoUser?.idClient ?: 0,
                uiState.userStatus?.infoCredit?.idLoanClient ?: 0,
                CARD_INFORMATION_STATUS
            ).collectLatest { result ->
                result.onSuccess {
                    balanceCredit?.balanceCardInformation = it
                    onLoadingValueChange(false)
                    if (nfcHelper.isNfcSupported()) {
                        onNavigateToVisaActivateScreen()
                    } else {
                        onNavigateToHomeMultimoneyVisa()
                    }
                }.onFailure {
                    onLoadingValueChange(false)
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }
    }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var userStatus: ValidateUserStatus? = null,
        var productPageList: List<ProductPage>? = null,
        val openDialog: DialogParameters = DialogParameters(),
        val isExpanded: Boolean = false,
        val onGoingCreditCardTitle: Int = R.string.home_product_title,
        val isCreditAvailable: Boolean = false,
        val canExpandCredit: Boolean = false,
        val scheduleChipIconResource: Int? = null,
        val phoneNumber : String? = null,
        val showCardIssuanceError: Boolean = false
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
            is OnNavigateToHomeMultimoneyVisa -> onNavigateToHomeMultimoneyVisa()
            is UIEvent.OnNavigateToPaymentSmartFlow -> onNavigateToPaymentSmartScreen(uiEvent.account)
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
                configurationVersion = uiEvent.configurationVersion,
                productPageList = uiEvent.productPageList,
                smartMovements = uiEvent.smartMovements
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
            is UIEvent.OnQuickActionClicked -> onQuickActionClicked(
                flow = uiEvent.flow,
                onLoadingValueChange = uiEvent.onLoadingValueChange
            )
            is OnMiniCardsClicked -> onQuickActionClicked(flow = uiEvent.flow, onLoadingValueChange = {})
            is OnDeleteAutomaticPayment -> onDeleteAutomaticPayment(uiEvent.onAcceptClick)
            is OnNavigateToSmartMovements -> onNavigateToSmartMovements(uiEvent.accountToken)
            is OnNavigateToCreditMovementsScreen -> onNavigateToCreditMovements()
            is OnCreateMultimoneyVisa -> onCreateMultimoneyVisa(uiEvent.onLoadingValueChange)
            is OnCloseCardIssuanceError -> uiState = uiState.copy(showCardIssuanceError = false)
        }
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
        object OnNavigateToHomeMultimoneyVisa : UIEvent()
        object OnNavigateToProfileScreen : UIEvent()
        object OnNavigateToDisbursement : UIEvent()
        object OnNavigateToSendMoneyFlow : UIEvent()
        data class OnNavigateToPaymentSmartFlow(val account: Account?) : UIEvent()
        data class OnNavigateToSmartMovements(val accountToken: String) : UIEvent()
        object OnNavigateToCreditMovementsScreen : UIEvent()
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
            val configurationVersion: ConfigurationVersion?,
            val productPageList: List<ProductPage>,
            val smartMovements: List<SmartMovementsResult>
        ) : UIEvent()

        data class OnShareIbanAccount(
            val clientLabel: String,
            val accountLabel: String,
            val ibanAccount: String
        ) : UIEvent()

        data class OnQuickActionClicked(val flow: String, val onLoadingValueChange: (isLoading: Boolean) -> Unit) :
            UIEvent()

        data class OnMiniCardsClicked(val flow: String) : UIEvent()
        data class OnDeleteAutomaticPayment(val onAcceptClick: () -> Unit) : UIEvent()
        data class OnCreateMultimoneyVisa(val onLoadingValueChange: (isLoading: Boolean) -> Unit) : UIEvent()
        object OnCloseCardIssuanceError : UIEvent()
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
        const val CREDIT_EL_SALVADOR_MANUAL_PROCESS = "CREDIT_EL_SALVADOR_MANUAL_PROCESS"
        const val CREDIT_PEP_PROCESS = "CREDIT_PEP_PROCESS"
        const val SEPARATOR = " + "
        const val PENDING_TO_CHECK_STATUS = "Pendiente Revision"
        private const val CARD_INFORMATION_STATUS = 1
    }
}
