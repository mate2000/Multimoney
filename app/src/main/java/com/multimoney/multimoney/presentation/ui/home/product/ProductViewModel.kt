package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.PENDING
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.data.util.catalog.CreditWorkflow
import com.multimoney.data.util.catalog.MyProductStatus
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus.NOT_SIGNED
import com.multimoney.data.util.catalog.SmartWorkflow
import com.multimoney.data.util.catalog.SmartWorkflow.SMART_CONTRACT_PROCESS
import com.multimoney.data.util.catalog.SmartWorkflow.SMART_FIRMED_ONFIDO_PENDING
import com.multimoney.data.util.catalog.SmartWorkflow.SMART_IDENTITY_INCOMPLETE_OR_ONFIDO_MAX_ATTEMPTS
import com.multimoney.data.util.catalog.SmartWorkflow.SMART_ONFIDO_PROCESS
import com.multimoney.domain.interaction.accountsmart.MutationAccountStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCase
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.interaction.mmvisa.QueryCardIssuanceNVUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountSmall
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditMovementsResult
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CROSSELING
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_GET_EVICERTIA_LINK
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
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
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateCollapsedPage
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateExpandedPage
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsBackPressed
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnVisaCardExpiredDialog
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.util.CryptoHelper
import com.multimoney.multimoney.presentation.util.FilterDate
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.PAGE_SIZE
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.ProfileCardListOrigin
import com.multimoney.multimoney.presentation.util.catalog.QuickActionFlow
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.getPreviousDate
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.NovoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val helper: ShareHelper,
    private val nfcHelper: NfcHelper,
    private val novoHelper: NovoHelper,
    private val cardIssuanceNVUseCase: QueryCardIssuanceNVUseCase,
    private val balanceCardInformationUseCase: QueryBalanceCardInformationUseCase,
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
    private val queryGetCryptoCurrencyMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val mutationAccountStatusUseCase: MutationAccountStatusUseCase,
    private val cryptoHelper: CryptoHelper
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
    var smartAccount: SmartAccountID? = null

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
        uiState = uiState.copy(
            idBrand = idBrand,
            productPageList = productPageList,
            expandedProductPageList = productPageList.filter { it.enabled }
        )
        this.idClient = validateUserStatus?.infoUser?.idClient ?: 0
        setBalance(balanceCredit)
        setValidateUserStatus(validateUserStatus)
        uiState = uiState.copy(idBrand = idBrand)
        this.smartMovementsList = smartMovements
        this.creditMovements = creditMovements
        viewModelScope.launch {
            uiState = uiState.copy(
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first(),
                isCryptoTransferEnabled = cryptoHelper.isCryptoTransferEnabled()
            )
        }
    }

    private fun setBalance(balance: Balance?) {
        balance?.let {
            balanceCredit = it
            val isCreditAvailable =
                (balanceCredit?.getFirstSummary()?.availableBalance ?: 0.0) > 0.0
            uiState = uiState.copy(
                canExpandCredit = it.getFirstSummary()?.canExpandState ?: false && it.getFirstSummary()?.isProductActive ?: false,
                paymentAvailable = checkPaymentAvailability(it.balanceCredit?.firstOrNull()?.summary),
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

    private fun onVisaCardExpiredDialog(
        idBrand: String,
        balance: Balance?
    ) {
        if (idBrand != Brand.CostaRica.id.toString()) {
            if (balance?.isExpiredAutomaticDebitCard() == true) {
                viewModelScope.launch {
                    if (dataStorePreferences.isVisaCardExpiredEnabled().first()) {
                        uiState = uiState.copy(
                            openDialog = DialogParameters(
                                titleResource = R.string.product_visa_card_expired_dialog_title,
                                descriptionResource = R.string.product_visa_card_expired_dialog_description,
                                positiveResource = R.string.product_visa_card_expired_dialog_update_label,
                                negativeResource = R.string.product_visa_card_expired_dialog_hide_label,
                                positiveAction = { navigateToMyCards() },
                                negativeAction = {
                                    viewModelScope.launch {
                                        dataStorePreferences.isVisaCardExpiredDialogEnabled(false)
                                    }
                                },
                                isActive = mutableStateOf(true)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setValidateUserStatus(userStatus: ValidateUserStatus?) {
        lastStep =
            CreditStep.Search.getIdByName(userStatus?.infoCredit?.infoPreApprove?.currentStep)
        uiState = uiState.copy(userStatus = userStatus)
    }

    fun getProductScreenTitle(): Int =
        when {
            uiState.userStatus?.infoCredit?.status == MyProductStatus.ACTIVE.status ||
                uiState.userStatus?.infoCrypto?.status == MyProductStatus.ACTIVE.status ||
                uiState.userStatus?.infoBankAccount?.status == MyProductStatus.ACTIVE.status -> R.string.home_product_header_title
            else -> R.string.home_product_available_products_title
        }

    private fun navigateToMyCards() {
        navigateTo(
            route = "${Screen.ProfileCardListScreen.baseRoute}/$email/${uiState.idBrand}/$identification/${ProfileCardListOrigin.Product.value}"
        )
    }

    private fun onNavigateToCreditScreen(
        workflow: String
    ) {
        when (workflow) {
            CreditWorkflow.CREDIT_CONTRACT_PROCESS.workflow -> {
                navigateTo(
                    Screen.SignDocumentProcessScreen.baseRoute
                        .plus(
                            getNavParam(
                                SIGN_DOCUMENT_STEP_ARG,
                                SignDocumentStep.GENERATE_DOCUMENT_STEP.value
                            )
                        )
                        .plus(
                            getNavParam(
                                SIGN_DOCUMENT_ID_PRINT,
                                uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint ?: 0
                            )
                        )
                        .plus(getNavParam(ID_BRAND, uiState.idBrand.toInt()))
                        .plus(getNavParam(PK_USER, pkUser))
                        .plus(getNavParam(IDENTIFICATION, identification))
                        .plus(getNavParam(EMAIL, email))
                        .plus(
                            getNavParam(
                                ID_USER_REQUEST,
                                uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0
                            )
                        )
                        .plus(getNavParam(FIRST_NAME, uiState.userStatus?.infoUser?.firstName))
                        .plus(getNavParam(LAST_NAME, uiState.userStatus?.infoUser?.lastName))
                        .plus(
                            getNavParam(
                                CROSSELING,
                                uiState.userStatus?.infoCredit?.infoPreApprove?.crosseling ?: false
                            )
                        )
                        .plus(getNavParam(SHOULD_GET_EVICERTIA_LINK, true))
                        .plus(
                            getNavParam(
                                EVICERTIA_STATUS,
                                uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm
                            )
                        )
                )
            }
            else -> {
                if (workflow == CreditWorkflow.CREDIT_ONFIDO_PROCESS.workflow) {
                    lastStep = CreditStep.Eight.id
                }
                navigateTo(
                    "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                        "${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}/${uiState.userStatus?.infoUser?.firstName}/" +
                        "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/" +
                        "${uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint}/" +
                        "${uiState.userStatus?.infoCredit?.infoPreApprove?.crosseling ?: false}"
                )
            }
        }
    }

    private fun onNavigateToSmartFlow(
        smartStep: String,
        comingFromCrypto: Boolean = false,
        onIntent: () -> Unit? = { }
    ) {
        when (smartStep) {
            SMART_IDENTITY_INCOMPLETE_OR_ONFIDO_MAX_ATTEMPTS.workflow -> onIntent()
            PENDING.status -> onCallMutationAccountStatusUseCase(comingFromCrypto)
            else -> {
                val firmStatus =
                    if (uiState.userStatus?.infoBankAccount?.statusFirm.isNullOrBlank().not()) {
                        uiState.userStatus?.infoBankAccount?.statusFirm
                    } else {
                        NOT_SIGNED.status
                    }
                navigateTo(
                    "${Screen.SmartScreen.baseRoute}/$userName/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                        "${uiState.userStatus?.infoUser?.firstName}/${uiState.userStatus?.infoUser?.lastName}/$comingFromCrypto/" +
                        "${uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestGlobal}/" +
                        "${uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestSysde}/$firmStatus/${uiState.userStatus?.infoBankAccount?.wording?.workflow}"
                )
            }
        }
    }

    // This function opens the saving flow from the quick actions
    private fun onNavigateToSmartSave() {
        if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
            val account = balanceCredit?.balanceAccountSmart?.firstOrNull()
            val smartIds = encodeData(
                SmartAccountID(
                    tokenAccount = account?.tokenNumber,
                    accountNumber = account?.accountNumber,
                    currencyID = account?.idCurrencyAccount
                )
            )
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/$smartIds/${encodeData(uiState.userStatus?.infoUser)}")
        } else if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            val infoCredit = uiState.userStatus?.infoCredit
            val smartIds = encodeData(
                balanceCredit?.balanceAccountSmart?.map {
                    SmartAccountID(
                        tokenAccount = it?.tokenNumber,
                        currencyID = it?.idCurrencyAccount,
                        accountNumber = it?.accountNumber ?: "",
                        ibanAccountNumber = it?.ibanAccountNumber
                    )
                }
            )
            navigateTo("${Screen.SmartPaymentOptionsScreenCR.baseRoute}/$smartIds/$email/${uiState.idBrand}/$identification/${infoCredit?.idClient}/${infoCredit?.idLoanClient}")
        }
    }

    private fun onNavigateToPaymentScreen() {
        val creditSummary = balanceCredit?.balanceCredit?.first()?.summary
        val infoCredit = uiState.userStatus?.infoCredit
        logEvents(AdjustEventType.HOME_CTA_FIRST_START_PAYMENT_5034)

        val route = if (
            (creditSummary?.size ?: 0) > 1 &&
            validateQuotas(creditSummary) &&
            uiState.idBrand.toInt() == Brand.CostaRica.id
        ) {
            "${Screen.PaymentFeeScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(creditSummary)
            }/$identification/$userName/${balanceCredit?.getFirstSummary()?.paymentDate}"
        } else if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            "${Screen.PaymentAccountScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
            encodeData(listOf(creditSummary?.firstOrNull { (it.currentBalance ?: ZERO) > ZERO }))
            }/$identification/$userName/${balanceCredit?.getFirstSummary()?.paymentDate}/${Screen.HomeScreen.route}"
        } else {
            "${Screen.PaymentOptionsScreen.baseRoute}/${balanceCredit?.getFirstCredit()?.creditNumber}/${
            encodeData(configurationVersion?.configuration?.credit?.paymentMethod?.filter { it?.active == true })
            }/${encodeData(configurationVersion?.configuration?.credit?.transferAccount)}/" +
                "${balanceCredit?.getFirstSummary()?.minPayment}/${balanceCredit?.getFirstSummary()?.minPaymentLabel}/" +
                "${balanceCredit?.getFirstSummary()?.currentBalance}/${balanceCredit?.getFirstSummary()?.currentBalanceLabel}/" +
                "$identification/$idClient/${infoCredit?.idLoanClient}/${balanceCredit?.getFirstSummary()?.idCurrency}/" +
                "${balanceCredit?.getFirstSummary()?.paymentDate}/${encodeData(uiState.userStatus?.infoUser)}"
        }
        navigateTo(route)
    }

    private fun onNavigateToAutomaticPaymentScheduleScreen(isEditSchedule: Boolean) {
        val infoCredit = uiState.userStatus?.infoCredit
        logEvents(AdjustEventType.HOME_CTA_ENABLED_FIRST_AUTOMATIC_PAYMENT_5032)
        if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            navigateTo(
                route = "${Screen.PaymentScheduleScreen.baseRoute}/$email/${uiState.idBrand}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
                encodeData(ClientBankAccount())
                }/${balanceCredit?.getFirstSummary()?.paymentDate}/${false}/${Screen.HomeScreen.route}/$isEditSchedule"
            )
        } else {
            navigateTo(
                route = "${Screen.PaymentScheduleCardScreen.baseRoute}/${infoCredit?.idClient}/${infoCredit?.idLoanClient}/${
                encodeData(
                    CardVisaDirect()
                )
                }/${balanceCredit?.getFirstSummary()?.paymentDate}/${false}/${Screen.HomeScreen.route}/$isEditSchedule/$identification/${
                encodeData(
                    uiState.userStatus?.infoUser
                )
                }"
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

    private fun checkPaymentAvailability(summaryList: List<Summary>?): Boolean =
        summaryList?.firstOrNull { it.currentBalance != ZERO } != null

    private fun validateQuotas(summaryList: List<Summary>?): Boolean =
        summaryList?.firstOrNull { it.currentBalance == ZERO } == null

    private fun onNavigateToVisaActivateScreen() =
        navigateTo(
            "${Screen.VisaIssuanceScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/${uiState.userStatus?.infoUser?.phone}/${
            encodeData(balanceCredit?.balanceCardInformation)
            }/${balanceCredit?.getFirstSummary()?.availableBalanceLabel}/$idClient/${uiState.userStatus?.infoCredit?.idLoanClient ?: 0}"
        )

    private fun onNavigateToHomeMultimoneyVisa() {
        val infoCredit = uiState.userStatus?.infoCredit
        logEvents(AdjustEventType.HOME_CTA_FIRST_ACTIVATE_MM_VISA_5036)
        navigateTo(
            "${Screen.VisaCardScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/${uiState.userStatus?.infoUser?.phone}/${
            encodeData(balanceCredit?.balanceCardInformation)
            }/${balanceCredit?.getFirstSummary()?.availableBalanceLabel}/$idClient/${uiState.userStatus?.infoCredit?.idLoanClient ?: 0}"
        )
    }

    private fun onNavigateToProfileScreen() {
        navigateTo("${Screen.ProfileScreen.baseRoute}/$idClient/${uiState.idBrand}/${uiState.userStatus?.infoUser?.firstName}/$email/${uiState.userStatus?.infoUser?.phone}/$identification/$pkUser/$userName")
    }

    // Todo check if the navigation to this screen is suitable for the purchase crypto flow
    private fun onNavigateToSmartPaymentAccountScreen() {
        val smartIds = encodeData(
            balanceCredit?.balanceAccountSmart?.map {
                SmartAccountID(
                    tokenAccount = it?.tokenNumber,
                    currencyID = it?.idCurrencyAccount,
                    accountNumber = it?.accountNumber ?: "",
                    ibanAccountNumber = it?.ibanAccountNumber
                )
            }
        )

        navigateTo("${Screen.SmartPaymentOptionsScreenCR.baseRoute}/$smartIds/$userName/${uiState.idBrand}/$identification/$idClient/${uiState.userStatus?.infoCredit?.idLoanClient}")
    }

    private fun onNavigateToSmartPaymentMethodScreen() {
        val account = balanceCredit?.balanceAccountSmart?.firstOrNull()

        smartAccount = SmartAccountID(
            tokenAccount = account?.tokenNumber,
            currencyID = account?.idCurrencyAccount,
            accountNumber = account?.accountNumber,
            customerId = account?.customerId,
            ibanAccountNumber = account?.ibanAccountNumber,
            totalBalance = account?.totalBalance
        )
        navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/${encodeData(smartAccount)}/${encodeData(uiState.userStatus?.infoUser)}")
    }

    private fun onNavigateToSmartMovements(accountToken: String) =
        navigateTo("${Screen.SmartMovementsScreen.baseRoute}/$userName/${uiState.idBrand}/$identification/$accountToken")

    private fun onNavigateToCryptoWallet() {
        val userStatus = uiState.userStatus
        val globalBalance = balanceCredit?.balanceCryptoAccount?.globalBalance ?: 0.0
        val statusCrypto = userStatus?.infoCrypto?.status
        val statusSmart = userStatus?.infoBankAccount?.status
        val statusCredit = userStatus?.infoCredit?.status
        val idLoanClient = userStatus?.infoCredit?.idLoanClient
        val cardStatus = userStatus?.infoVirtualCard?.status
        navigateTo(
            "${Screen.CryptoWalletScreen.baseRoute}/$email/${uiState.idBrand}/$identification/" +
                "$globalBalance/$idClient/$idLoanClient/$statusCredit/$statusSmart/$statusCrypto/$cardStatus"
        )
    }

    private fun onNavigateToCryptoMarket() {
        navigateTo(
            "${Screen.CryptoMarketScreen.baseRoute}/$userName/${uiState.idBrand}"
        )
    }

    private fun onNavigateToCryptoMovements() {
        navigateTo("${Screen.CryptoHomeAllMovementsScreen.baseRoute}/${uiState.idBrand}/$identification/$email")
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
            (balanceCredit?.getFirstSummary()?.currentBalance?.toFloat() ?: DEFAULT_PROGRESS) /
                (balanceCredit?.getFirstCredit()?.creditLimit?.toFloat() ?: DEFAULT_PROGRESS)
        }
    }

    private fun isExpired() {
        isExpiredTitle = if (
            (balanceCredit?.getFirstSummary()?.daysExpired ?: 0) > 0
        ) R.string.home_product_expired else R.string.home_product_expiration
    }

    private fun getIfIsPep() =
        uiState.userStatus?.infoCredit?.infoPreApprove?.status == PENDING_TO_CHECK_STATUS

    private fun shareIbanAccount(clientLabel: String, accountLabel: String, ibanAccount: String) {
        helper.shareTextPlain("$clientLabel: ${userName.uppercase()}\n$accountLabel: $ibanAccount")
    }

    private fun onNavigateToDisbursement() {
        logEvents(AdjustEventType.DISBURSEMENT_FIRST_INIT_PROCESS_5021)
        navigateTo(
            route = "${Screen.DisbursementAmountScreen.baseRoute}/${uiState.idBrand}/$email/${uiState.userStatus?.infoCredit?.idClient}/${
            encodeData(
                balanceCredit?.getFirstCredit()?.summary
            )
            }/$pkUser/${balanceCredit?.getFirstCredit()?.creditNumber}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/$identification"
        )
    }

    private fun onNavigateToGtSvNonPreApproved() =
        navigateTo(
            "${Screen.NonPreApprovedScreen.baseRoute}/${uiState.idBrand}/$pkUser/$identification/$email/$lastStep/" +
                "${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest ?: 0}/${uiState.userStatus?.infoUser?.firstName}/" +
                "${uiState.userStatus?.infoUser?.lastName}/${uiState.userStatus?.infoUser?.statusOnfido}/" +
                "${uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idPrint ?: 0}/${uiState.userStatus?.infoCredit?.infoPreApprove?.crosseling ?: false}"
        )

    fun getCreditBalanceLabel(balanceCredit: List<BalanceCredit?>?): String {
        var amount = balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.currentBalanceLabel ?: ""
        balanceCredit?.forEach { balance ->
            if (balance?.summary?.any { it.currentBalance != ZERO } == true) {
                amount = balance.summary?.filter { it.currentBalance != ZERO }
                    ?.joinToString(separator = SEPARATOR) { summary ->
                        summary.currentBalanceLabel ?: ""
                    } ?: ""
            }
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
            QuickActionFlow.SEND_MONEY.flow -> onNavigateToSendMoneyScreenQuickAction()
            QuickActionFlow.BUY_CRYPTO.flow -> onNavigateToPurchaseCryptoFlow()
            QuickActionFlow.SELL_CRYPTO.flow -> onNavigateToSellCryptoFlow()
            QuickActionFlow.SEND_CRYPTO.flow -> onNavigateToSendCryptoFlow()
            QuickActionFlow.RECEIVE_CRYPTO.flow -> onNavigateToGiveCryptoFlow()
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

    private fun callSinpeAccountsListUseCase(
        account: Account?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit
    ) =
        executeUseCase {
            queryListSinpeAccountUseCaseImpl.invoke(
                user = email,
                identification = identification,
                idBrand = uiState.idBrand.toIntOrNull() ?: 0,
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
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSmartSavingBtnEventRegistered().first()) {
                registerAdjustEvent(adjustEventType = AdjustEventType.HOME_CTA_FIRST_SAVING_6016)
                dataStorePreferences.isAdjustSmartSavingBtnEventRegistered(false)
            }
        }
        if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
            val smartIds = encodeData(
                SmartAccountID(
                    tokenAccount = account?.tokenNumber,
                    accountNumber = account?.accountNumber,
                    currencyID = account?.idCurrencyAccount
                )
            )
            navigateTo("${Screen.SmartPaymentMethodScreenSV.baseRoute}/$smartIds/${encodeData(uiState.userStatus?.infoUser)}")
        } else if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            callSinpeAccountsListUseCase(account, onLoadingValueChange)
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
            route = "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$email/${uiState.idBrand}/" +
                "$identification/${Screen.HomeScreen.route}/$idClient/${infoCredit?.idLoanClient}/" +
                "${encodeData(clientBankAccounts)}/${encodeData(smartIds)}"
        )
    }

    private fun navigateToAddIbanAccount() {
        val infoCredit = uiState.userStatus?.infoCredit
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$email/${uiState.idBrand.toIntOrNull() ?: 0}/$identification/${Screen.HomeScreen.route}/$idClient/${infoCredit?.idLoanClient}"
        )
    }

    private fun onNavigateToSendMoneyScreenQuickAction() {
        if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
            val account = balanceCredit?.balanceAccountSmart?.firstOrNull()
            onNavigateToSendMoneyScreen(account)
        } else if (uiState.idBrand == Brand.CostaRica.id.toString()) {
            val infoCredit = uiState.userStatus?.infoCredit
            val smartIds = encodeData(
                balanceCredit?.balanceAccountSmart?.map {
                    SmartAccountID(
                        tokenAccount = it?.tokenNumber,
                        currencyID = it?.idCurrencyAccount,
                        accountNumber = it?.accountNumber ?: "",
                        ibanAccountNumber = it?.ibanAccountNumber,
                        totalBalance = it?.totalBalance,
                        customerId = it?.customerId
                    )
                }
            )
            navigateTo("${Screen.SmartSelectAccountScreen.baseRoute}/$smartIds/$email/${uiState.idBrand}/$identification/${infoCredit?.idClient}")
        }
    }

    private fun onNavigateToSendMoneyScreen(account: Account?) {
        smartAccount = SmartAccountID(
            tokenAccount = account?.tokenNumber,
            currencyID = account?.idCurrencyAccount,
            accountNumber = account?.accountNumber,
            totalBalance = account?.totalBalance,
            ibanAccountNumber = account?.ibanAccountNumber,
            customerId = account?.customerId
        )
        val secondAccount =
            balanceCredit?.balanceAccountSmart?.find { accounts -> accounts?.tokenNumber != account?.tokenNumber }
        val secondAccountSend = encodeData(
            SmartAccountID(
                tokenAccount = secondAccount?.tokenNumber,
                currencyID = secondAccount?.idCurrencyAccount,
                accountNumber = secondAccount?.accountNumber,
                totalBalance = secondAccount?.totalBalance,
                ibanAccountNumber = secondAccount?.ibanAccountNumber,
                customerId = account?.customerId
            )
        )

        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSmartSendingBtnEventRegistered().first()) {
                registerAdjustEvent(adjustEventType = AdjustEventType.HOME_CTA_FIRST_SENDING_6017)
                dataStorePreferences.isAdjustSmartSendingBtnEventRegistered(false)
            }
        }

        navigateTo(
            "${Screen.SmartSelectSendingTypeScreen.baseRoute}/$userName/${uiState.idBrand}/$identification" +
                "/${encodeData(smartAccount)}/$secondAccountSend/$idClient/${Screen.HomeScreen.route}"
        )
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

    private fun onCallQueryGetCryptoMovements() = executeUseCase {
        uiState = uiState.copy(
            cryptoCurrencyMovements = queryGetCryptoCurrencyMovementsUseCase.invoke(
                user = userName,
                idBrand = uiState.idBrand.toIntOrNull() ?: 0,
                identification = identification,
                market = EMPTY_STRING, // get all markets movements with empty string
                order_time_begin = getPreviousDate(FilterDate.LAST_365_DAYS),
                order_time_end = getCurrentDateYMDPattern(),
                pagination_limit = PAGE_SIZE
            ).cachedIn(viewModelScope)
        )
    }

    private fun onCallMutationAccountStatusUseCase(comingFromCrypto: Boolean) = executeUseCase {
        mutationAccountStatusUseCase.invoke(
            user = userName,
            idBrand = uiState.idBrand.toInt(),
            identificationNumber = identification,
            newState = DEFAULT_NEW_STATE,
            typeState = DEFAULT_TYPE_STATE,
            idAccountSysde = uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestSysde ?: 0,
            idAccountRequest = uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestGlobal
                ?: 0L
        ).collectLatest { result ->
            result.onSuccess {
                popAndNavigateTo(
                    "${Screen.SmartSignScreen.baseRoute}/${SIGN_DOCUMENTS_STEP.value}/${it?.urlFirmDocument}/${uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestSysde}/${uiState.idBrand.toInt()}/$pkUser/$identification/$email/${uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestSysde}/$firstName/${uiState.userStatus?.infoUser?.lastName}/${true}/${uiState.userStatus?.infoBankAccount?.infoRequest?.idRequestGlobal}/$userName/$comingFromCrypto",
                    Screen.HomeScreen.route
                )
            }.onFailure {
            }
        }
    }

    private fun onCallQueryBalanceCardInformation(onLoadingValueChange: (isLoading: Boolean) -> Unit) {
        executeUseCase {
            balanceCardInformationUseCase.invoke(
                email,
                identification,
                uiState.idBrand.toIntOrNull() ?: 0,
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
            novoHelper.configureNovoSdk()
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

    private fun onNavigateToPurchaseCryptoFlow() {
        navigateTo("${Screen.PurchaseCryptoFlow.baseRoute}/${Screen.HomeScreen.route}")
    }

    private fun onNavigateToSellCryptoFlow() {
        navigateTo(
            "${Screen.CryptoSellFlow.baseRoute}/${Screen.HomeScreen.route}"
        )
    }

    private fun onNavigateToSendCryptoFlow() {
        navigateTo(Screen.CryptoSendFlow.baseRoute)
    }

    private fun onNavigateToGiveCryptoFlow() {
        navigateTo("${Screen.CryptoReceiveFlowScreen.baseRoute}/$userName/${uiState.idBrand}")
    }

    private fun getSmartContent() {
        val workflow = uiState.userStatus?.infoBankAccount?.wording?.workflow
        uiState = uiState.copy(
            smartContent = when (workflow) {
                SmartWorkflow.SMART_INITIAL_CARD.workflow, SmartWorkflow.SMART_STEP_PENDING.workflow, SMART_IDENTITY_INCOMPLETE_OR_ONFIDO_MAX_ATTEMPTS.workflow,
                SmartWorkflow.SMART_CONTRACT_PROCESS.workflow, SmartWorkflow.SMART_FIRMED_ONFIDO_PENDING.workflow, SmartWorkflow.SMART_FIRMED_ONFIDO_REJECTED.workflow,
                SmartWorkflow.SMART_APPROVED_BY_ONFIDO.workflow, SMART_ONFIDO_PROCESS.workflow -> {
                    Pair(true, workflow)
                }
                "" -> {
                    Pair(true, SMART_CARD_NO_ACTION)
                }
                else -> {
                    Pair(false, "")
                }
            }
        )
    }

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }

    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first()
            )
        }
    }

    private fun onShowDisclaimer() {
        emitBaseEvent(BaseEvent.OnShowDisclaimer)
    }

    private fun List<Account?>.toSmartAccountsNavType(): List<SmartAccountSmall> {
        return this.map { account ->
            SmartAccountSmall(
                totalBalance = account?.totalBalance,
                currencyCode = account?.currencyCode,
                idCurrencyAccount = account?.idCurrencyAccount,
                accountToken = account?.tokenNumber ?: "",
                accountNumber = account?.accountNumber ?: "",
                ibanAccountNumber = account?.ibanAccountNumber ?: ""
            )
        }
    }

    private fun navigateToMaintenanceAlert() {
        navigateTo(Screen.MaintenanceAlertScreen.route)
    }

    private fun onNavigateToReleaseTransaction(cryptoItem: CryptoCurrencyMovement?) {
        navigateTo("${Screen.ReleaseTransactionScreen.baseRoute}/${cryptoItem?.market}/${cryptoItem?.id}/${Screen.HomeScreen.route}")
    }

    private fun registerAdjustCryptoHomeFirstTimeEvent() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoHomeFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoHomeFirstTime(true)
            registerAdjustEvent(
                adjustEventType = AdjustEventType.HOME_CRYPTO_FIST_TIME_ENTER_TO_HOME
            )
        }
    }

    private fun registerAdjustFirstPressPurchaseEvent() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoPressPurchaseFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoPressPurchaseFirstTime(true)
            registerAdjustEvent(
                adjustEventType = AdjustEventType.PURCHASE_CRYPTO_FIRST_TIME_PRESS_BUY_BUTTON
            )
        }
    }

    private fun registerAdjustFirstPressSellEvent() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoPressSellFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoPressSellFirstTime(true)
            registerAdjustEvent(
                adjustEventType = AdjustEventType.SELL_CRYPTO_FIRST_TIME_PRESS_SELL_BUTTON
            )
        }
    }

    private fun registerAdjustFirstPressSendEvent() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoPressSendFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoPressSendFirstTime(true)
            registerAdjustEvent(
                adjustEventType = AdjustEventType.SEND_CRYPTO_FIRST_TIME_PRESS_SEND_BUTTON
            )
        }
    }

    private fun registerAdjustFirstPressReceiveEvent() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoPressReceiveFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoPressReceiveFirstTime(true)
            registerAdjustEvent(
                adjustEventType = AdjustEventType.RECEIVE_CRYPTO_FIRST_TIME_PRESS_RECEIVE_BUTTON
            )
        }
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val infoCredit = uiState.userStatus?.infoCredit
        val baseAdjustEvent = BaseEventDataDto(
            user = email,
            idBrand = uiState.idBrand.toInt(),
            idClient = idClient,
            idLoanClient = infoCredit?.idLoanClient,
            identification = identification
        )
        return when (adjustEventType) {
            AdjustEventType.HOME_CTA_FIRST_START_PAYMENT_5034 -> {
                getStartPaymentEvent(baseAdjustEvent)
            }
            AdjustEventType.HOME_CTA_ENABLED_FIRST_AUTOMATIC_PAYMENT_5032 -> {
                getScheduledPaymentEvent(baseAdjustEvent)
            }
            AdjustEventType.HOME_CTA_FIRST_ACTIVATE_MM_VISA_5036 -> {
                getActivateMMVisaEvent(baseAdjustEvent)
            }
            AdjustEventType.DISBURSEMENT_FIRST_INIT_PROCESS_5021 -> {
                getStartDisbursementEvent(baseAdjustEvent)
            }
            else -> suspend {}
        }
    }

    private fun getStartDisbursementEvent(baseAdjustEvent: BaseEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstDisbursementEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.DISBURSEMENT_FIRST_INIT_PROCESS_5021,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFirstDisbursementEventRegister(false)
            }
        }

    private fun getStartPaymentEvent(baseAdjustEvent: BaseEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstPaymentEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.HOME_CTA_FIRST_START_PAYMENT_5034,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFirstPaymentEventRegister(false)
            }
        }

    private fun getScheduledPaymentEvent(baseAdjustEvent: BaseEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstSchedulePaymentEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.HOME_CTA_ENABLED_FIRST_AUTOMATIC_PAYMENT_5032,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFirstSchedulePaymentEventRegister(false)
            }
        }

    private fun getActivateMMVisaEvent(baseAdjustEvent: BaseEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstActivateMMVisaEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.HOME_CTA_FIRST_ACTIVATE_MM_VISA_5036,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFirstActivateMMVisaEventRegister(false)
            }
        }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var expandedPage: Int = 0,
        var collapsedPage: Int = 0,
        var userStatus: ValidateUserStatus? = null,
        var productPageList: List<ProductPage>? = null,
        var expandedProductPageList: List<ProductPage>? = null,
        val openDialog: DialogParameters = DialogParameters(),
        val isExpanded: Boolean = false,
        val onGoingCreditCardTitle: Int = R.string.home_product_title,
        val isCreditAvailable: Boolean = false,
        val canExpandCredit: Boolean = false,
        val scheduleChipIconResource: Int? = null,
        val phoneNumber: String? = null,
        val smartContent: Pair<Boolean?, String> = Pair(null, ""),
        val cryptoCurrencyMovements: Flow<PagingData<CryptoCurrencyMovement>> = flowOf(),
        var isBackPressed: Boolean = false,
        val paymentAvailable: Boolean = false,
        val shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
        val isCryptoTransferEnabled: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnUpdateExpandedPage -> uiState = uiState.copy(
                expandedPage = uiEvent.expandedPage
            )
            is OnUpdateCollapsedPage -> uiState = uiState.copy(
                collapsedPage = uiEvent.collapsedPage
            )
            is OnUpdateIsExpanded -> uiState = uiState.copy(
                isExpanded = uiEvent.isExpanded
            )
            is OnBalanceSuccess -> balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> setValidateUserStatus(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen(uiEvent.workflow)
            is OnNavigateToSmartOriginationFlow -> onNavigateToSmartFlow(
                smartStep = uiEvent.smartStep,
                comingFromCrypto = uiEvent.comingFromCrypto,
                onIntent = uiEvent.onIntent
            )
            is OnNavigateToPaymentProcess -> onNavigateToPaymentScreen()
            is OnNavigateToSendMoneyFlow -> onNavigateToSendMoneyScreen(uiEvent.account)
            is OnNavigateToHomeMultimoneyVisa -> onNavigateToHomeMultimoneyVisa()
            is OnNavigateToPaymentSmartFlow -> onSmartAccountCardClick(
                uiEvent.account,
                uiEvent.onLoadingValueChange
            )
            is OnNavigateToProfileScreen -> onNavigateToProfileScreen()
            is OnNavigateToDisbursement -> onNavigateToDisbursement()
            is UIEvent.OnNavigateToCryptoWallet -> onNavigateToCryptoWallet()
            is UIEvent.OnNavigateToCryptoMarket -> onNavigateToCryptoMarket()
            is UIEvent.OnNavigateToCryptoMovements -> onNavigateToCryptoMovements()
            is UIEvent.OnGetCryptoMovements -> onCallQueryGetCryptoMovements()
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
            is OnNavigateToScheduleAutomaticPaymentScreen -> onNavigateToAutomaticPaymentScheduleScreen(
                uiEvent.isEditSchedule
            )
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
            is UIEvent.OnNavigateToPurchaseCryptoFlow -> onNavigateToPurchaseCryptoFlow()
            is UIEvent.OnNavigateToSellCryptoFlow -> onNavigateToSellCryptoFlow()
            is UIEvent.OnNavigateToSendCryptoFlow -> onNavigateToSendCryptoFlow()
            is UIEvent.OnNavigateToGiveCryptoFlow -> onNavigateToGiveCryptoFlow()
            is OnVisaCardExpiredDialog -> onVisaCardExpiredDialog(
                idBrand = uiEvent.idBrand,
                balance = uiEvent.balance
            )
            is OnUpdateIsBackPressed ->
                uiState = uiState.copy(isBackPressed = uiEvent.isBackPressed)
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(uiEvent.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(uiEvent.checked)
            BaseEvent.OnShowDisclaimer -> onShowDisclaimer()
            UIEvent.OnNavigateToMaintenanceAlert -> navigateToMaintenanceAlert()
            is UIEvent.OnNavigateToReleaseTransaction -> onNavigateToReleaseTransaction(uiEvent.cryptoItem)
            UIEvent.OnRegisterAdjustCryptoHomeFistTime -> registerAdjustCryptoHomeFirstTimeEvent()
            UIEvent.OnRegisterAdjustPressPurchaseFirstTime -> registerAdjustFirstPressPurchaseEvent()
            UIEvent.OnRegisterAdjustPressReceiveFirstTime -> registerAdjustFirstPressReceiveEvent()
            UIEvent.OnRegisterAdjustPressSellFirstTime -> registerAdjustFirstPressSellEvent()
            UIEvent.OnRegisterAdjustPressSendFirstTime -> registerAdjustFirstPressSendEvent()
            UIEvent.OnRegisterAdjustPaxosInMaintenance -> registerAdjustEvent(
                applyAdjust = false,
                adjustEventType = AdjustEventType.HOME_CRYPTO_PAXOS_IN_MAINTENANCE
            )
        }
    }

    sealed class UIEvent {
        data class OnUpdateExpandedPage(val expandedPage: Int) : UIEvent()
        data class OnUpdateCollapsedPage(val collapsedPage: Int) : UIEvent()
        data class OnUpdateIsBackPressed(val isBackPressed: Boolean) : UIEvent()
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

        object OnGetCryptoMovements : UIEvent()
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
        data class OnNavigateToCreditScreen(val workflow: String) : UIEvent()
        object OnChipQuotaClick : UIEvent()
        data class OnNavigateToScheduleAutomaticPaymentScreen(val isEditSchedule: Boolean) :
            UIEvent()

        object OnNavigateToSmartPaymentAccountScreen : UIEvent()
        object OnNavigateToSmartPaymentMethodScreen : UIEvent()
        object OnNavigateToCryptoWallet : UIEvent()
        object OnNavigateToCryptoMarket : UIEvent()
        object OnNavigateToCryptoMovements : UIEvent()
        object OnNavigateToPurchaseCryptoFlow : UIEvent()
        object OnNavigateToSellCryptoFlow : UIEvent()
        object OnNavigateToSendCryptoFlow : UIEvent()
        object OnNavigateToGiveCryptoFlow : UIEvent()
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
        data class OnVisaCardExpiredDialog(
            val idBrand: String,
            val balance: Balance?
        ) : UIEvent()

        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()
        object OnNavigateToMaintenanceAlert : UIEvent()
        data class OnNavigateToReleaseTransaction(val cryptoItem: CryptoCurrencyMovement?) :
            UIEvent()

        object OnRegisterAdjustCryptoHomeFistTime : UIEvent()
        object OnRegisterAdjustPressPurchaseFirstTime : UIEvent()
        object OnRegisterAdjustPressSellFirstTime : UIEvent()
        object OnRegisterAdjustPressSendFirstTime : UIEvent()
        object OnRegisterAdjustPressReceiveFirstTime : UIEvent()
        object OnRegisterAdjustPaxosInMaintenance : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowCardIssuanceError : BaseEvent()
        object OnShowTbdToastEvent : BaseEvent()
        object OnShowDisclaimer : UIEvent()
    }

    companion object {
        const val EMPTY_STRING = ""
        const val ERROR_CREDIT = "Error"
        const val DEFAULT_PRODUCT_PAGES = 1
        const val INITIAL_PRODUCT_PAGE = 0
        const val DEFAULT_PROGRESS = 1F
        const val ZERO = 0.0
        const val SEPARATOR = " + "

        // Smart
        const val PENDING_TO_CHECK_STATUS = "Pendiente Revision"
        const val DEFAULT_NEW_STATE = "PG"
        const val DEFAULT_TYPE_STATE = "S"
        private const val CARD_INFORMATION_STATUS = 1
        const val SMART_CARD_NO_ACTION = "smart_card_no_action"
    }
}
