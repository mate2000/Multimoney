package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.data.util.catalog.SmartAccountStatusRequest
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.APPROVED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.FAILED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.FIRMED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.OVER_COUNTER
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.PENDING
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.REJECTED
import com.multimoney.data.util.catalog.SmartAccountStatus.EXIST_IN_CORE
import com.multimoney.data.util.catalog.SmartAccountStatus.NO_EXIST
import com.multimoney.data.util.catalog.SmartAccountStatusRequest.CANCELED
import com.multimoney.data.util.catalog.SmartAccountStatusRequest.CREATED
import com.multimoney.data.util.catalog.SmartAccountStatusRequest.SENT
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCase
import com.multimoney.domain.interaction.mmvisa.QueryCardIssuanceNVUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditMovementsResult
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.BaseEvent.OnShowCardIssuanceError
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCartButtonClickWithoutSmartBalance
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnChipQuotaClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetSmartContent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnLastStepChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMiniCardsClicked
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditMovementsScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToGtSvNonPreApproved
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentSmartFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToProfileScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSendMoneyFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartMovements
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNoVoConfig
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnQuickActionClicked
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.QuickActionFlow
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentOrigin
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val helper: ShareHelper,
    private val nfcHelper: NfcHelper,
    private val cardIssuanceNVUseCase: QueryCardIssuanceNVUseCase,
    private val balanceCardInformationUseCase: QueryBalanceCardInformationUseCase,
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase
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
    var idClient: Int = 0
    var userName: String = ""
    var productProgress = 0F
    val firstName: String? = null
    var isExpiredTitle = R.string.home_product_expiration
    var smartMovementsList: List<SmartMovementsResult> = emptyList()
    var creditMovements: List<CreditMovementsResult> = emptyList()
    var smartAccount : SmartAccountID? = null

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
        smartMovements: List<SmartMovementsResult>,
        creditMovements: List<CreditMovementsResult>
    ) {
        this.pkUser = pkUser
        this.identification = identification
        this.email = email
        this.userName = userName
        this.configurationVersion = configurationVersion
        uiState = uiState.copy(idBrand = idBrand, productPageList = productPageList)
        this.idClient = validateUserStatus?.infoUser?.idClient ?: 0
        setBalance(balanceCredit)
        setValidateUserStatus(validateUserStatus)
        uiState = uiState.copy(idBrand = idBrand)
        this.smartMovementsList = smartMovements
        this.creditMovements = creditMovements
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

    private fun onNavigateToCreditScreen(
        creditStep: String
    ) {
        when (creditStep) {
            CREDIT_ONFIDO_REJECTED -> {
                if (uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm?.lowercase() == FIRMED.status.lowercase()) {
                    navigateTo(
                        "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                                "${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}/${uiState.userStatus?.infoUser?.firstName}/" +
                                "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/" +
                                "${uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint}"
                    )
                } else {
                    navigateTo(
                        "${Screen.SignDocumentProcessScreen.baseRoute}/${SignDocumentStep.GENERATE_DOCUMENT_STEP.value}/${SignDocumentOrigin.Product.value}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint ?: 0}/${uiState.idBrand.toInt()}/$pkUser/$identification/$email/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/${uiState.userStatus?.infoUser?.firstName}/${uiState.userStatus?.infoUser?.lastName}"
                    )
                }
            }
            CREDIT_FIRM_INCOMPLETE, CREDIT_FIRM_REJECTED -> {
                navigateTo(
                    "${Screen.SignDocumentProcessScreen.baseRoute}/${SignDocumentStep.GENERATE_DOCUMENT_STEP.value}/${SignDocumentOrigin.Product.value}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint ?: 0}/${uiState.idBrand.toInt()}/$pkUser/$identification/$email/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/${uiState.userStatus?.infoUser?.firstName}/${uiState.userStatus?.infoUser?.lastName}"
                )
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

    private fun onNavigateToSmartFlow(
        smartStep: String,
        comingFromCrypto: Boolean = false,
        onIntent: () -> Unit? = { }
    ) {
        // TODO Implement navigation on smart cards
        when (smartStep) {
            SMART_ONFIDO_REJECTED -> {
                // TODO get the new evicertia url
            }
            SMART_ONFIDO_MAX_ATTEMPTS -> onIntent()
            else -> {
                navigateTo(
                    "${Screen.SmartScreen.baseRoute}/$userName/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                        "${uiState.userStatus?.infoUser?.firstName}/" +
                        "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/$comingFromCrypto"
                )
            }
        }
    }

    // This function opens the saving flow from the quick actions
    private fun onNavigateToSmartSave() {
        if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
            val account = balanceCredit?.balanceAccountSmart?.first()
            val smartIds = encodeData(
                SmartAccountID(
                    tokenAccount = account?.tokenNumber,
                    accountNumber = account?.accountNumber,
                    currencyID = account?.idCurrencyAccount
                )
            )
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/$smartIds")
        } else if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            val infoCredit = uiState.userStatus?.infoCredit
            val smartIds = encodeData(balanceCredit?.balanceAccountSmart?.map {
                SmartAccountID(
                    tokenAccount = it?.tokenNumber,
                    currencyID = it?.idCurrencyAccount,
                    accountNumber = it?.accountNumber ?: "",
                    ibanAccountNumber = it?.ibanAccountNumber
                )
            })
            navigateTo("${Screen.SmartPaymentOptionsScreenCR.baseRoute}/${smartIds}/$email/${uiState.idBrand}/$identification/${infoCredit?.idClient}/${infoCredit?.idLoanClient}")
        }
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
            }/${encodeData(configurationVersion?.configuration?.credit?.transferAccount)}/" +
                "${balanceCredit?.getFirstSummary()?.minPayment}/${balanceCredit?.getFirstSummary()?.minPaymentLabel}/" +
                "${balanceCredit?.getFirstSummary()?.currentBalance}/${balanceCredit?.getFirstSummary()?.currentBalanceLabel}/" +
                "$identification/$email/$idClient/${infoCredit?.idLoanClient}/${balanceCredit?.getFirstSummary()?.idCurrency}/" +
                "${balanceCredit?.getFirstSummary()?.paymentDate}"
        }
        navigateTo(route)
    }

    private fun onNavigateToAutomaticPaymentScheduleScreen(isEditSchedule: Boolean) {
        val infoCredit = uiState.userStatus?.infoCredit
        if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            navigateTo(
                route = "${Screen.PaymentScheduleScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
                encodeData(
                    ClientBankAccount()
                )
                }/${balanceCredit?.getFirstSummary()?.paymentDate}/${false}/${Screen.HomeScreen.route}/$isEditSchedule"
            )
        } else {
            navigateTo(
                route = "${Screen.PaymentScheduleCardScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
                encodeData(
                    CardVisaDirect()
                )
                }/${balanceCredit?.getFirstSummary()?.paymentDate}/${false}/${Screen.HomeScreen.route}/$isEditSchedule/$identification"
            )
        }
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
        navigateTo(
            "${Screen.VisaIssuanceScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/${uiState.userStatus?.infoUser?.phone}/${
            encodeData(
                balanceCredit?.balanceCardInformation
            )
            }/${balanceCredit?.getFirstSummary()?.availableBalanceLabel}/$idClient/${uiState.userStatus?.infoCredit?.idLoanClient ?: 0}"
        )

    private fun onNavigateToHomeMultimoneyVisa() =
        navigateTo(
            "${Screen.VisaCardScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/${uiState.userStatus?.infoUser?.phone}/${
            encodeData(
                balanceCredit?.balanceCardInformation
            )
            }/${balanceCredit?.getFirstSummary()?.availableBalanceLabel}/$idClient/${uiState.userStatus?.infoCredit?.idLoanClient ?: 0}"
        )

    private fun onNavigateToProfileScreen() {
        navigateTo("${Screen.ProfileScreen.baseRoute}/$idClient/${uiState.idBrand}/${uiState.userStatus?.infoUser?.firstName}/$email/${uiState.userStatus?.infoUser?.phone}/$identification/$pkUser/${uiState.userStatus?.infoUser?.userName}")
    }

    private fun onNavigateToSignDocumentScreen() {
        navigateTo("${Screen.ProfileScreen.baseRoute}/$idClient/${uiState.idBrand}/${uiState.userStatus?.infoUser?.firstName}/$email/${uiState.userStatus?.infoUser?.phone}/$identification/$pkUser/${uiState.userStatus?.infoUser?.userName}")
    }


    // Todo check if the navigation to this screen is suitable for the purchase crypto flow
    private fun onNavigateToSmartPaymentAccountScreen() =
        navigateTo(Screen.SmartPaymentOptionsScreenCR.route)

    private fun onNavigateToSmartPaymentMethodScreen() =
        navigateTo(Screen.SmartPaymentMethodScreenSV.route)

    private fun onNavigateToSmartMovements(accountToken: String) =
        navigateTo("${Screen.SmartMovementsScreen.baseRoute}/$userName/${uiState.idBrand}/$identification/$accountToken")

    private fun onNavigateToCryptoWallet() {
        navigateTo(
            "${Screen.CryptoWalletScreen.baseRoute}/$email/${uiState.idBrand}/$identification/${balanceCredit?.balanceCryptoAccount?.globalBalance ?: 0.0}"
        )
    }

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
    }

    private fun onNavigateToCreditMovements() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo("${Screen.CreditMovementsScreen.baseRoute}/${uiState.idBrand}/${infoCredit?.idLoanClient ?: 0}/$email/${balanceCredit?.getFirstCredit()?.creditNumber}")
    }

    private fun getProgress() {
        productProgress = if (uiState.isCreditAvailable.not()) {
            DEFAULT_PROGRESS
        } else {
            (
                balanceCredit?.getFirstSummary()?.currentBalance?.toFloat()
                    ?: DEFAULT_PROGRESS
                ) / (
                balanceCredit?.getFirstCredit()?.creditLimit?.toFloat()
                    ?: DEFAULT_PROGRESS
                )
        }
    }

    private fun isExpired() {
        isExpiredTitle = if ((
            balanceCredit?.getFirstSummary()?.daysExpired
                ?: 0
            ) > 0
        ) R.string.home_product_expired else R.string.home_product_expiration
    }

    private fun getIfIsPep() =
        uiState.userStatus?.infoCredit?.infoPreApprove?.status == PENDING_TO_CHECK_STATUS

    fun evaluateCardCondition(action: String, validateUserStatus: ValidateUserStatus): Boolean {
        validateUserStatus.apply {
            return when (action) {
                CREDIT_INITIAL_CARD -> {
                        (infoCredit?.infoPreApprove?.currentStep.isNullOrEmpty() || validateUserStatus.infoCredit?.infoPreApprove?.currentStep == CREDIT_STEP_PRE_APPROVED)
                }

                SMART_INITIAL_CARD -> {
                    infoUser?.statusOnfido == SmartOnFidoOrFirmStatus.PENDING.status &&
                        infoBankAccount?.statusFirm == PENDING.status &&
                        (infoBankAccount?.infoRequest?.currentStep.isNullOrEmpty() || validateUserStatus.infoBankAccount?.infoRequest?.statusRequest == SMART_STEP_PENDING)
                }

                CREDIT_INFO_INCOMPLETE -> {
                        (CreditStep.Search.getIdByName(infoCredit?.infoPreApprove?.currentStep) < CreditStep.Eight.id)
                }

                CREDIT_IDENTITY_INCOMPLETE -> {
                    (CreditStep.Search.getIdByName(infoCredit?.infoPreApprove?.currentStep) == CreditStep.Eight.id)
                }

                CREDIT_EL_SALVADOR_MANUAL_PROCESS -> {
                    uiState.idBrand.toInt() == Brand.ElSalvador.id && infoUser?.statusOnfido == APPROVED.status
                }

                CREDIT_PEP_PROCESS -> {
                    infoUser?.statusOnfido == APPROVED.status && infoCredit?.infoPreApprove?.idPrint == 0L && getIfIsPep()
                }

                SMART_IDENTITY_INCOMPLETE -> {
                    infoUser?.statusOnfido != SmartOnFidoOrFirmStatus.APPROVED.status && (
                        SmartSteps.Search.getIdByName(infoBankAccount?.infoRequest?.currentStep) == SmartSteps.Six.id
                        )
                }

                CREDIT_FIRM_INCOMPLETE -> {
                    infoCredit?.infoPreApprove?.idPrint != null && infoCredit?.infoPreApprove?.idPrint != 0L && infoCredit?.infoPreApprove?.statusFirm == PENDING.status
                }

                CREDIT_FIRMED_ONFIDO_PENDING -> {
                    infoCredit?.infoPreApprove?.statusFirm == FIRMED.status &&
                        infoUser?.statusOnfido == PENDING.status
                }

                SMART_FIRMED_ONFIDO_PENDING -> {
                    infoBankAccount?.statusFirm == SmartOnFidoOrFirmStatus.FIRMED.status &&
                        infoUser?.statusOnfido == SmartOnFidoOrFirmStatus.PENDING?.status
                }

                CREDIT_FIRM_REJECTED -> {
                    infoCredit?.infoPreApprove?.statusFirm == REJECTED.status
                }

                SMART_APPROVED_BY_ONFIDO -> {
                    infoUser?.statusOnfido == SmartOnFidoOrFirmStatus.APPROVED.status
                }

                CREDIT_FIRM_MAX_ATTEMPTS -> {
                    infoCredit?.infoPreApprove?.statusFirm == OVER_COUNTER.status
                }

                CREDIT_ONFIDO_REJECTED -> {
                    infoUser?.statusOnfido == REJECTED.status
                }

                SMART_ONFIDO_REJECTED -> {
                    infoUser?.statusOnfido == SmartOnFidoOrFirmStatus.REJECTED.status
                }

                CREDIT_ONFIDO_MAX_ATTEMPTS -> {
                    infoUser?.statusOnfido == OVER_COUNTER.status
                }

                SMART_ONFIDO_MAX_ATTEMPTS -> {
                    infoUser?.statusOnfido == SmartOnFidoOrFirmStatus.OVER_COUNTER.status
                }

                CREDIT_ERROR_CREATE_ACCOUNT -> {
                    infoCredit?.infoPreApprove?.statusFirm == FAILED.status ||
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
            }/$pkUser/${balanceCredit?.getFirstCredit()?.creditNumber}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/$identification"
        )

    private fun onNavigateToGtSvNonPreApproved() =
        navigateTo(
            "${Screen.NonPreApprovedScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                    "${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/${uiState.userStatus?.infoUser?.firstName}/" +
                    "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/" +
                    "${uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint ?: 0}"
        )

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

    private fun onQuickActionClicked(
        flow: String,
        onLoadingValueChange: (isLoading: Boolean) -> Unit
    ) {
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

    private fun callQueryBalanceUseCase(account: Account?, onLoadingValueChange: (isLoading: Boolean) -> Unit) =
        executeUseCase {
            queryListSinpeAccountUseCaseImpl.invoke(
                user = email,
                identification = identification ?: "",
                idBrand = uiState.idBrand.toInt(),
                country = "",
                idAccount = 0,
                accountNumber = ""
            ).collectLatest { result ->
                result.onSuccess { accountList ->
                    onLoadingValueChange(false)
                    if (accountList?.data?.isEmpty() == true) {
                        navigateToAddIbanAccount()
                    } else {
                        accountList?.data?.let {
                            navigateToSmartAccount(account = account, clientBankAccounts = it)
                        }
                    }
                }
                result.onFailure {
                    onLoadingValueChange(false)
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }
                result.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }

    // This function opens the flow from the smart card
    private fun onSmartAccountCardClick(
        account: Account?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit
    ) {
        if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
            val smartIds = encodeData(
                SmartAccountID(
                    tokenAccount = account?.tokenNumber,
                    accountNumber = account?.accountNumber,
                    currencyID = account?.idCurrencyAccount
                )
            )
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/$smartIds")
        } else if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            callQueryBalanceUseCase(account, onLoadingValueChange)
        }
    }

    // This function opens the iban accounts list to choose to make the deposit
    private fun navigateToSmartAccount(account: Account?, clientBankAccounts: List<SinpeAccount?>) {
        val infoCredit = uiState.userStatus?.infoCredit
        val smartIds = SmartAccountID(
            tokenAccount = account?.tokenNumber,
            currencyID = account?.idCurrencyAccount,
            accountNumber = account?.accountNumber,
            ibanAccountNumber = account?.ibanAccountNumber
        )
        navigateTo(
            route = "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$email/${uiState.idBrand}/$identification/${Screen.HomeScreen.route}/$idClient/" +
                "${infoCredit?.idLoanClient}/${encodeData(clientBankAccounts)}/${encodeData(smartIds)}"
        )
    }

    private fun navigateToAddIbanAccount() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$email/${uiState.idBrand}/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/${infoCredit?.idLoanClient}"
        )
    }

    private fun onNavigateToSendMoneyScreen(account: Account?) {
        smartAccount = SmartAccountID(
            tokenAccount = account?.tokenNumber,
            currencyID = account?.idCurrencyAccount,
            accountNumber = account?.accountNumber,
            totalBalance = account?.totalBalance
        )
        if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            navigateTo(
                "${Screen.SmartSelectSendingTypeScreen.baseRoute}/${userName}/${uiState.idBrand}/${identification}/${encodeData(smartAccount)}/$idClient"
            )
        }
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
                    emitBaseEvent(OnShowCardIssuanceError)
                }.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }
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

    private fun onConfigNovoSdk() {
        if (nfcHelper.isNfcSupported()) {
            if (NovoVTS.isConfiguredForVts().not()) {
                NovoVTS.setConfigurations(
                    BuildConfig.NOVO_CLIENT_ID,
                    BuildConfig.NOVO_API_KEY,
                    BuildConfig.NOVO_API_URL,
                    BuildConfig.NOVO_VCEH_URL,
                    BuildConfig.NOVO_SOCKET_URL
                )
            }
        }
    }

    private fun onCartButtonClickWithoutSmartBalance(onSavingCLick: () -> Unit) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.crypto_footer_expanded_dialog_title,
                descriptionResource = R.string.crypto_footer_expanded_dialog_description,
                positiveResource = R.string.crypto_footer_expanded_dialog_btn_saving,
                negativeResource = R.string.crypto_footer_expanded_dialog_btn_cancel,
                positiveAction = { onSavingCLick() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun getSmartContent() {
        val statusRequest = uiState.userStatus?.infoBankAccount?.infoRequest?.statusRequest
        val statusFirm = uiState.userStatus?.infoBankAccount?.statusFirm
        val status = uiState.userStatus?.infoBankAccount?.status
        uiState = uiState.copy(
            smartContent = when (statusRequest) {
                SmartAccountStatusRequest.PENDING.status, SENT.status, CANCELED.status, CREATED.status,
                SMART_INITIAL_CARD, SMART_IDENTITY_INCOMPLETE, SMART_FIRMED_ONFIDO_PENDING, SMART_ONFIDO_REJECTED,
                SMART_APPROVED_BY_ONFIDO, SMART_ONFIDO_MAX_ATTEMPTS -> {
                    Pair(
                        status?.equals(NO_EXIST.status) == true,
                        statusRequest
                    )
                }
                else -> {
                    when (statusFirm) {
                        PENDING.status, APPROVED.status, FIRMED.status, REJECTED.status, OVER_COUNTER.status, FAILED.status -> {
                            Pair(
                                status?.equals(NO_EXIST.status) == true,
                                statusFirm
                            )
                        }
                        else -> {
                            if (status == NO_EXIST.status || status == EXIST_IN_CORE.status) {
                                Pair(status == NO_EXIST.status, status.toString())
                            } else {
                                Pair(null, "")
                            }
                        }
                    }
                }
            }
        )
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
        val phoneNumber: String? = null,
        val smartContent: Pair<Boolean?, String> = Pair(null, "")
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnUpdateIsExpanded -> uiState = uiState.copy(isExpanded = uiEvent.isExpanded)
            is OnBalanceSuccess -> balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> setValidateUserStatus(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen(uiEvent.creditStep)
            is OnNavigateToSmartOriginationFlow -> onNavigateToSmartFlow(
                smartStep = uiEvent.smartStep,
                comingFromCrypto = uiEvent.comingFromCrypto,
                onIntent = uiEvent.onIntent
            )
            is OnNavigateToPaymentProcess -> onNavigateToPaymentScreen()
            is OnNavigateToSendMoneyFlow -> onNavigateToSendMoneyScreen(uiEvent.account)
            is OnNavigateToHomeMultimoneyVisa -> onNavigateToHomeMultimoneyVisa()
            is OnNavigateToPaymentSmartFlow -> onSmartAccountCardClick(uiEvent.account, uiEvent.onLoadingValueChange)
            is OnNavigateToProfileScreen -> onNavigateToProfileScreen()
            is OnNavigateToDisbursement -> onNavigateToDisbursement()
            is UIEvent.OnNavigateToCryptoWallet -> onNavigateToCryptoWallet()
            is OnNavigateToGtSvNonPreApproved -> onNavigateToGtSvNonPreApproved()
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
                smartMovements = uiEvent.smartMovements,
                creditMovements = uiEvent.creditMovements
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
            is OnNavigateToScheduleAutomaticPaymentScreen -> onNavigateToAutomaticPaymentScheduleScreen(uiEvent.isEditSchedule)
            is OnQuickActionClicked -> onQuickActionClicked(
                flow = uiEvent.flow,
                onLoadingValueChange = uiEvent.onLoadingValueChange
            )
            is OnMiniCardsClicked -> onQuickActionClicked(
                flow = uiEvent.flow,
                onLoadingValueChange = {}
            )
            is OnDeleteAutomaticPayment -> onDeleteAutomaticPayment(uiEvent.onAcceptClick)
            is OnNavigateToSmartMovements -> onNavigateToSmartMovements(uiEvent.accountToken)
            is OnCartButtonClickWithoutSmartBalance -> onCartButtonClickWithoutSmartBalance(
                uiEvent.onSavingCLick
            )
            is OnNavigateToSmartPaymentAccountScreen -> onNavigateToSmartPaymentAccountScreen()
            is OnNavigateToSmartPaymentMethodScreen -> onNavigateToSmartPaymentMethodScreen()
            is OnNavigateToCreditMovementsScreen -> onNavigateToCreditMovements()
            is OnCreateMultimoneyVisa -> onCreateMultimoneyVisa(uiEvent.onLoadingValueChange)
            is OnNoVoConfig -> onConfigNovoSdk()
            is OnGetSmartContent -> getSmartContent()
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
        data class OnNavigateToSmartOriginationFlow(
            val smartStep: String = "",
            val comingFromCrypto: Boolean = false,
            val onIntent: () -> Unit? = { }
        ) : UIEvent()

        object OnNavigateToPaymentProcess : UIEvent()
        object OnNavigateToProfileScreen : UIEvent()
        object OnNavigateToHomeMultimoneyVisa : UIEvent()
        object OnNavigateToDisbursement : UIEvent()
        data class OnNavigateToPaymentSmartFlow(
            val account: Account?,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit
        ) : UIEvent()

        data class OnNavigateToSendMoneyFlow(val account: Account?) : UIEvent()
        data class OnNavigateToSmartMovements(val accountToken: String) : UIEvent()
        object OnNavigateToCreditMovementsScreen : UIEvent()
        object OnNavigateToGtSvNonPreApproved : UIEvent()
        object OnProgressCalculation : UIEvent()
        object IsPaymentExpired : UIEvent()
        data class OnNavigateToCreditScreen(val creditStep: String) : UIEvent()
        object OnChipQuotaClick : UIEvent()
        data class OnNavigateToScheduleAutomaticPaymentScreen(val isEditSchedule: Boolean) : UIEvent()
        object OnNavigateToSmartPaymentAccountScreen : UIEvent()
        object OnNavigateToSmartPaymentMethodScreen : UIEvent()
        object OnNavigateToCryptoWallet : UIEvent()
        object OnGetSmartContent : UIEvent()

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
            val smartMovements: List<SmartMovementsResult>,
            val creditMovements: List<CreditMovementsResult>
        ) : UIEvent()

        data class OnShareIbanAccount(
            val clientLabel: String,
            val accountLabel: String,
            val ibanAccount: String
        ) : UIEvent()

        data class OnQuickActionClicked(
            val flow: String,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit
        ) :
            UIEvent()

        data class OnMiniCardsClicked(val flow: String) : UIEvent()
        data class OnDeleteAutomaticPayment(val onAcceptClick: () -> Unit) : UIEvent()
        data class OnCreateMultimoneyVisa(val onLoadingValueChange: (isLoading: Boolean) -> Unit) :
            UIEvent()

        object OnNoVoConfig : UIEvent()
        data class OnCartButtonClickWithoutSmartBalance(val onSavingCLick: () -> Unit) : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowCardIssuanceError : BaseEvent()
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

        // Smart
        const val SMART_IDENTITY_INCOMPLETE = "SAMART_IDENTITY_INCOMPLETE"
        const val SMART_ONFIDO_REJECTED = "SMART_ONFIFO_REJECTED"
        const val SMART_INITIAL_CARD = "SMART_INITIAL_CARD"
        const val SMART_APPROVED_BY_ONFIDO = "SMART_APPROVED_BY_ONFIDO"
        const val SMART_ONFIDO_MAX_ATTEMPTS = "SMART_ONFIDO_MAX_ATTEMPTS"
        const val SMART_FIRMED_ONFIDO_PENDING = "SMART_FIRMED_ONFIDO_PENDING"
        const val SMART_STEP_PENDING = "SMART_STEP_PENDING"
        const val PENDING_TO_CHECK_STATUS = "Pendiente Revision"
        private const val CARD_INFORMATION_STATUS = 1
    }
}
