package com.multimoney.multimoney.presentation.ui.home

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.domain.interaction.accountsmart.QueryGetCoreBankMovementsUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.credit.MutationDeactivateCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.MutationDeactivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetPromissoryNoteDetail
import com.multimoney.domain.interaction.crypto.GetHistoricalClientBalanceUseCase
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.interaction.security.QueryMiniCardsUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.credit.CreditMovementsResult
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.MiniCardsItem
import com.multimoney.domain.model.security.QuickAction
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.catalog.ConfigurationPlatform
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.RELEASE_TOAST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.CryptoHelper
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.INDEX_ONE
import com.multimoney.multimoney.presentation.util.LAST_THREE
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.OPTION_BTN_6
import com.multimoney.multimoney.presentation.util.SignOutCommunicator
import com.multimoney.multimoney.presentation.util.boolean
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.ProductType
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import com.multimoney.multimoney.util.BiometricHelper
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagerApi::class)
class HomeViewModel @Inject constructor(
    val countDownTimer: MMCountDownTimer,
    private val dataStorePreferences: DataStorePreferences,
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase,
    private val queryGetHistoricalClientBalanceUseCase: GetHistoricalClientBalanceUseCase,
    private val queryGetConfigurationVersionUseCase: QueryGetConfigurationVersionUseCase,
    private val queryMiniCardsUseCase: QueryMiniCardsUseCase,
    private val queryGetQuickActionsUseCase: QueryGetQuickActionsUseCase,
    private val getClientAutomaticDebitUseCase: QueryGetClientAutomaticDebitUseCase,
    private val getCardAutomaticDebitUseCase: QueryGetCardAutomaticDebitUseCase,
    private val mutationDeactivateClientAutomaticDebitUseCase: MutationDeactivateClientAutomaticDebitUseCase,
    private val mutationDeactivateCardAutomaticDebitUseCase: MutationDeactivateCardAutomaticDebitUseCase,
    private val queryGetCoreBankMovements: QueryGetCoreBankMovementsUseCase,
    private val queryGetPromissoryNoteDetail: QueryGetPromissoryNoteDetail,
    private val biometricHelper: BiometricHelper,
    private val cognitoHelper: CognitoHelper,
    private val cryptoHelper: CryptoHelper
) : BaseViewModel(true) {

    // Stateless
    private var communicator: SignOutCommunicator? = null
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""
    private var isBiometricActive = false
    private var apiCallCount = 0

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onsetUserData() {
        apiCallCount = 0
        viewModelScope.launch {
            apiCallCount = 0
            uiState = uiState.copy(
                idBrand = dataStorePreferences.getIdBrand().firstOrNull() ?: "",
                pkUser = dataStorePreferences.getPkUser().firstOrNull() ?: "",
                identification = dataStorePreferences.getIdentification().firstOrNull() ?: "",
                email = dataStorePreferences.getUserEmail().firstOrNull() ?: "",
                userName = dataStorePreferences.getUserName().firstOrNull() ?: "",
            )
            callQueryValidateUserStatus(
                uiState.pkUser.toInt(),
                uiState.identification,
                uiState.email,
                uiState.idBrand.toInt()
            )
            callQueryGetConfigurationVersion(uiState.idBrand.toInt())
        }
    }



    private fun onSetHomeState(homeState: HomeState) {
        uiState = uiState.copy(
            homeState = homeState
        )
    }

    private fun onGetSmartMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        tokenNumber: Long
    ) {
        executeUseCase {
            queryGetCoreBankMovements.invoke(
                user = user,
                idBrand = idBrand,
                identificationNumber = identificationNumber,
                accountToken = tokenNumber,
                pageNumber = INDEX_ONE,
                pageSize = LAST_THREE,
                monthDate = null
            ).collectLatest { result ->
                result.onSuccess {
                    it?.let { movements ->
                        movements.accountToken = tokenNumber
                        uiState = uiState.copy(
                            smartMovementsList = uiState.smartMovementsList + movements,
                            isLoading = false
                        )
                    }
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                result.onFailure {
                    onFailure(it)
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    private fun onGetCreditMovements(
        idBrand: Int,
        idLoanClient: Int
    ) {
        executeUseCase {
            queryGetPromissoryNoteDetail.invoke(
                idBrand = idBrand,
                idLoanClient = idLoanClient,
                pageNumber = INDEX_ONE,
                pageSize = LAST_THREE,
                option = OPTION_BTN_6
            ).collectLatest { result ->
                result.onSuccess {
                    it?.let { promissoryNote ->
                        uiState = uiState.copy(
                            creditMovementsList = promissoryNote.movementsResultList ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                result.onFailure {
                    onFailure(it)
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    private fun callQueryGetQuickActions(
        idBrand: Int,
        pkUser: Int,
        identification: String,
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCriptoStatus: Int
    ) = executeUseCase {
        queryGetQuickActionsUseCase.invoke(
            idBrand = idBrand,
            pkUser = pkUser,
            identification = identification,
            infoCreditStatus = infoCreditStatus,
            infoVirtualCardStatus = infoVirtualCardStatus,
            infoBankAccountStatus = infoBankAccountStatus,
            infoCriptoStatus = infoCriptoStatus
        ).collectLatest { result ->
            result.onSuccess { quickActions ->
                quickActions?.let {
                    uiState = uiState.copy(
                        quickActions = it.quickActions
                    )
                }
                apiCallCount++
                if (apiCallCount == API_CALLS_TOTAL) {
                    uiState = uiState.copy(isLoading = false)
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

    private fun callQueryGetMiniCards(
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCryptoStatus: Int,
        idBrand: Int,
        email: String
    ) = executeUseCase {
        queryMiniCardsUseCase.invoke(
            infoCreditStatus = infoCreditStatus.boolean,
            infoVirtualCardStatus = infoVirtualCardStatus.boolean,
            infoBankAccountStatus = infoBankAccountStatus.boolean,
            infoCrypto = infoCryptoStatus.boolean,
            userEmail = email,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess { miniCards ->
                uiState = uiState.copy(
                    miniCardList = miniCards.miniCardsList.toMutableList().sortedBy { it.priority }
                )
                apiCallCount++
                if (apiCallCount == API_CALLS_TOTAL) {
                    uiState = uiState.copy(isLoading = false)
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
                balance?.let { setBalance(it) }
                apiCallCount++
                if (apiCallCount == API_CALLS_TOTAL) {
                    uiState = uiState.copy(isLoading = false)
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

    private fun callQueryGetHistoricalBalanceUseCase(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String
    ) = executeUseCase {
        queryGetHistoricalClientBalanceUseCase.invoke(
            user,
            idBrand,
            identification,
            baseAsset = baseAsset,
            startDate = getPreviousDate(FilterDateByDays.YESTERDAY.time),
            endDate = getCurrentDateYMDPattern()
        ).collectLatest { result ->
            result.onSuccess { historicBalance ->
                historicBalance?.let {
                    uiState = uiState.copy(
                        cryptoHistoricalBalance = it.historicalBalanceClient
                    )
                }
                apiCallCount++
                if (apiCallCount == API_CALLS_TOTAL) {
                    uiState = uiState.copy(isLoading = false)
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

    private fun setBalance(balance: Balance) {
        val productPageList = mutableListOf<ProductPage>()

        val defaultIndex = 0
        // Default credit
        productPageList.add(
            ProductPage(
                product = ProductType.Credit.value,
                enabled = uiState.validateUserStatus?.infoCredit?.status == CreditStatus.EXIST_IN_CORE.status,
                index = defaultIndex,
                resourceIcon = R.drawable.ic_my_credit,
                resourceText = R.string.home_my_products_label_credit
            )
        )

        removeBlankCards(productPageList)

        // If idBrand is different from Guatemala enable Smart
        if (uiState.idBrand != Brand.Guatemala.id.toString()) {
            if (balance.balanceAccountSmart.isNullOrEmpty().not()) {
                // Add the amount of account smart that user has
                balance.balanceAccountSmart?.forEachIndexed { index, account ->
                    productPageList.add(
                        ProductPage(
                            product = ProductType.Smart.value,
                            productSmartIndex = index,
                            enabled = uiState.validateUserStatus?.infoBankAccount?.status == SmartAccountStatus.EXIST_IN_CORE.status,
                            index = productPageList.lastIndex + 1,
                            resourceText = run {
                                when (account?.currencyCode) {
                                    CurrencyType.Dollar.value -> R.string.home_my_products_label_smart
                                    CurrencyType.Colon.value -> R.string.home_my_products_label_smart_colones
                                    //  adding quetzal label here if necessary
                                    else -> R.string.home_my_products_label_smart
                                }
                            },
                            resourceIcon = run {
                                when (account?.currencyCode) {
                                    CurrencyType.Dollar.value -> R.drawable.ic_dollars_strong
                                    CurrencyType.Colon.value -> R.drawable.ic_colones_strong
                                    // adding quetzal icon here if necessary
                                    else -> R.drawable.ic_dollars_strong
                                }
                            }
                        )
                    )

                    onUIEvent(
                        UIEvent.OnGetSmartMovements(
                            uiState.userName,
                            uiState.idBrand.toIntOrNull() ?: 0,
                            uiState.identification,
                            account?.tokenNumber?.toLongOrNull() ?: 0
                        )
                    )
                }
                // If user has smart activated he can enable crypto
                productPageList.add(
                    ProductPage(
                        product = ProductType.Crypto.value,
                        enabled = uiState.validateUserStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status,
                        index = productPageList.lastIndex + 1,
                        resourceIcon = R.drawable.ic_union,
                        resourceText = R.string.home_my_products_label_crypto
                    )
                )
            } else {
                // If user doesn't have smart we have to add one empty card to activate the product
                productPageList.add(
                    ProductPage(
                        product = ProductType.Smart.value,
                        enabled = uiState.validateUserStatus?.infoBankAccount?.status == SmartAccountStatus.EXIST_IN_CORE.status,
                        index = productPageList.lastIndex + 1,
                        resourceText = R.string.home_my_products_label_smart,
                        resourceIcon = R.drawable.ic_dollars_strong
                    )
                )
                productPageList.add(
                    ProductPage(
                        product = ProductType.Crypto.value,
                        enabled = uiState.validateUserStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status,
                        index = productPageList.lastIndex + 1,
                        resourceIcon = R.drawable.ic_union,
                        resourceText = R.string.home_my_products_label_crypto
                    )
                )
            }
        }

        if (uiState.validateUserStatus?.infoCredit?.status == CreditStatus.EXIST_IN_CORE.status) {
            onUIEvent(
                UIEvent.OnGetCreditMovements(
                    uiState.idBrand.toIntOrNull() ?: 0,
                    uiState.validateUserStatus?.infoCredit?.idLoanClient ?: 0
                )
            )
        }
        uiState = uiState.copy(balance = balance, productPageList = productPageList)
    }

    private fun removeBlankCards(productPageList: MutableList<ProductPage>) {
        val creditStatus = uiState.validateUserStatus?.infoCredit
        when (uiState.idBrand) {
            Brand.CostaRica.id.toString() -> {
                if (creditStatus?.status == CreditStatus.CREDIT_NOT_PRE_APPROVED.status || (creditStatus?.status == CreditStatus.CREDIT_REJECTED.status && creditStatus.wording?.display == false)) {
                    productPageList.clear()
                }
            }
            Brand.ElSalvador.id.toString() -> {
                if (creditStatus?.status == CreditStatus.NO_EXIST.status || (creditStatus?.status == CreditStatus.CREDIT_REJECTED.status && creditStatus.wording?.display == false)) {
                    productPageList.clear()
                }
            }
        }
    }

    private fun callQueryGetConfigurationVersion(
        idBrand: Int
    ) = executeUseCase {
        queryGetConfigurationVersionUseCase.invoke(
            platform = ConfigurationPlatform.Android.value,
            appVersion = BuildConfig.VERSION_NAME,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess { configurationVersion ->
                configurationVersion?.let {
                    uiState = uiState.copy(configurationVersion = configurationVersion)
                }
                viewModelScope.launch {
                    countDownTimer.startTimer(
                        configurationVersion?.configuration?.timeSession?.toLong() ?: 0
                    )
                }
                apiCallCount++
                if (apiCallCount == API_CALLS_TOTAL) {
                    uiState = uiState.copy(isLoading = false)
                }

                cryptoHelper.apply {
                    saveCryptoOrigin(configurationVersion?.configuration?.crypto?.origin ?: "")
                    saveEnableCryptoTransfer(
                        configurationVersion?.configuration?.crypto?.isTransferEnabled ?: false
                    )
                }
                dataStorePreferences.setSmartTransferLimit(
                    configurationVersion?.configuration?.accountSmart?.transferLimit ?: listOf()
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

    private fun callQueryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ) = executeUseCase {
        queryValidateUserStatusUseCase.invoke(
            pkUser,
            identification,
            email,
            idBrand
        ).collectLatest { result ->
            result.onSuccess { validateUserStatus ->
                apiCallCount++
                dataStorePreferences.setUserPhoneNumberWithCode(validateUserStatus?.infoUser?.phone.orEmpty())
                uiState = uiState.copy(validateUserStatus = validateUserStatus)
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
                callQueryGetQuickActions(
                    idBrand = idBrand,
                    pkUser = pkUser,
                    identification = identification,
                    infoCreditStatus = validateUserStatus?.infoCredit?.status ?: 0,
                    infoVirtualCardStatus = validateUserStatus?.infoVirtualCard?.status ?: 0,
                    infoCriptoStatus = validateUserStatus?.infoCrypto?.status ?: 0,
                    infoBankAccountStatus = validateUserStatus?.infoBankAccount?.status ?: 0
                )
                callQueryGetMiniCards(
                    idBrand = idBrand,
                    email = email,
                    infoCreditStatus = validateUserStatus?.infoCredit?.status ?: 0,
                    infoVirtualCardStatus = validateUserStatus?.infoVirtualCard?.status ?: 0,
                    infoCryptoStatus = validateUserStatus?.infoCrypto?.status ?: 0,
                    infoBankAccountStatus = validateUserStatus?.infoBankAccount?.status ?: 0
                )
                if (uiState.idBrand != Brand.Guatemala.id.toString() && validateUserStatus?.infoBankAccount?.status == SmartAccountStatus.EXIST_IN_CORE.status &&
                    validateUserStatus.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status
                ) {
                    callQueryGetHistoricalBalanceUseCase(
                        user = email,
                        identification = identification,
                        idBrand = idBrand,
                        baseAsset = uiState.balance?.balanceCryptoAccount?.items?.firstOrNull()?.asset
                            ?: ""
                    )
                } else {
                    apiCallCount++
                    if (apiCallCount == API_CALLS_TOTAL) {
                        uiState = uiState.copy(isLoading = false)
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

    private fun onCallGetClientAutomaticDebitUseCase() = executeUseCase {
        if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
            getClientAutomaticDebitUseCase.invoke(
                user = uiState.email,
                idBrand = uiState.idBrand.toInt(),
                idClient = uiState.validateUserStatus?.infoUser?.idClient ?: 0,
                idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    val clientBankAccount = it?.firstOrNull()
                    callMutationDeactivateClientAutomaticDebitUseCase(
                        clientBankAccount?.origin.orEmpty(),
                        clientBankAccount?.id?.toLong() ?: 0
                    )
                }.onFailure {
                    onFailure(it)
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        } else {
            getCardAutomaticDebitUseCase.invoke(
                user = uiState.email,
                identification = uiState.identification,
                idBrand = uiState.idBrand.toInt(),
                idClient = uiState.validateUserStatus?.infoUser?.idClient?.toLong() ?: 0L,
                idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient?.toLong() ?: 0L
            ).collectLatest { result ->
                result.onSuccess {
                    val cardVisaDirect = it?.firstOrNull()
                    callMutationDeactivateCardAutomaticDebitUseCase(
                        cardVisaDirect?.idCard
                    )
                }.onFailure {
                    onFailure(it)
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun callMutationDeactivateCardAutomaticDebitUseCase(idCard: Int?) =
        executeUseCase {
            mutationDeactivateCardAutomaticDebitUseCase.invoke(
                user = uiState.email,
                idBrand = uiState.idBrand.toInt(),
                idClient = uiState.validateUserStatus?.infoUser?.idClient?.toLong() ?: 0,
                idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient?.toLong() ?: 0,
                idCard = idCard?.toLong() ?: 0L
            ).collectLatest { result ->
                result.onSuccess {
                    callQueryBalanceUseCase(
                        user = uiState.email,
                        identification = uiState.identification,
                        idBrand = uiState.idBrand.toInt(),
                        idClient = uiState.validateUserStatus?.infoUser?.idClient ?: 0,
                        idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient ?: 0,
                        creditStatus = uiState.validateUserStatus?.infoCredit?.status ?: 0,
                        accountStatus = uiState.validateUserStatus?.infoBankAccount?.status ?: 0,
                        cryptoStatus = uiState.validateUserStatus?.infoCrypto?.status ?: 0,
                        cardStatus = uiState.validateUserStatus?.infoVirtualCard?.status ?: 0
                    )
                    emitBaseEvent(BaseEvent.OnDeleteAutomaticPaymentToastEvent)
                }
                result.onFailure {
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    private fun callMutationDeactivateClientAutomaticDebitUseCase(origin: String, idAccount: Long) =
        executeUseCase {
            mutationDeactivateClientAutomaticDebitUseCase.invoke(
                user = uiState.email,
                idBrand = uiState.idBrand.toInt(),
                idClient = uiState.validateUserStatus?.infoUser?.idClient?.toLong() ?: 0,
                idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient?.toLong() ?: 0,
                origin = origin,
                idAccount = idAccount
            ).collectLatest { result ->
                result.onSuccess {
                    callQueryBalanceUseCase(
                        user = uiState.email,
                        identification = uiState.identification,
                        idBrand = uiState.idBrand.toInt(),
                        idClient = uiState.validateUserStatus?.infoUser?.idClient ?: 0,
                        idLoanClient = uiState.validateUserStatus?.infoCredit?.idLoanClient ?: 0,
                        creditStatus = uiState.validateUserStatus?.infoCredit?.status ?: 0,
                        accountStatus = uiState.validateUserStatus?.infoBankAccount?.status ?: 0,
                        cryptoStatus = uiState.validateUserStatus?.infoCrypto?.status ?: 0,
                        cardStatus = uiState.validateUserStatus?.infoVirtualCard?.status ?: 0
                    )
                    emitBaseEvent(BaseEvent.OnDeleteAutomaticPaymentToastEvent)
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
        apiCallCount++
        if (apiCallCount == API_CALLS_TOTAL) {
            uiState = uiState.copy(isLoading = false)
        }
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    fun navigation(innerNavHostController: NavHostController, route: String) {
        when (route) {
            Screen.HomeBNScreen.route -> {
                innerNavigateTo(innerNavHostController, route)
            }
            Screen.QuickActionBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenQuickActionsBottomSheet)
            }
            Screen.ProductsBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenMyProductsBottomSheet)
            }
        }
    }

    private fun openQuickActionFlow(flow: String) {
        emitBaseEvent(BaseEvent.OnQuickActionClicked(flow))
    }

    private fun signOut(activity: Activity?) {
        hideTimerDialog()
        activity?.let { safeActivity ->
            communicator = safeActivity as SignOutCommunicator
            if (communicator?.isAppInForeground()?.not() == true) {
                viewModelScope.launch { dataStorePreferences.isSignOutOnBackground(true) }
            }
            if (isBiometricActive) {
                onShowBiometricPromptForDecryption(safeActivity as FragmentActivity)
            } else {
                executeLogOut()
            }
        }
    }

    private fun onSessionDuplicated() {
        hideTimerDialog()
        executeLogOut()
    }

    private fun executeLogOut() {
        viewModelScope.launch {
            dataStorePreferences.isSessionDuplicated(false)
        }
        cognitoHelper.signOut { Timber.d("SignOut Error") }
        viewModelScope.launch { dataStorePreferences.setAuthToken("") }
        popAndNavigateTo(Screen.SignInScreen.route, Screen.HomeScreen.route)
    }

    private fun onStartBiometrics() {
        viewModelScope.launch {
            isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
        }
    }

    private fun initializeBiometricPrompt(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
    }

    private fun biometricPromptError(errorCode: Int, errString: CharSequence) {
        executeLogOut()
    }

    private fun onShowBiometricPromptForDecryption(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            biometricHelper.showBiometricPrompt(
                title = biometricPromptTitle,
                description = biometricPromptDescription,
                negative = biometricPromptNegative,
                activity = fragmentActivity,
                processSuccess = {
                    countDownTimer.startTimer(
                        uiState.configurationVersion?.configuration?.timeSession?.toLong() ?: 0
                    )
                },
                processError = { errorCode, errString ->
                    biometricPromptError(errorCode, errString)
                },
                initializationVector = dataStorePreferences.getUserPasswordVector()
                    .first()
            )
        }
    }

    fun getCardIssuanceDescriptionError() = if (uiState.idBrand.toInt() == Brand.CostaRica.id) {
        R.string.card_issuance_error_description
    } else {
        R.string.card_issuance_error_description_sv
    }

    private fun showTimerDialog(time: Long, activity: Activity?) {
        activity?.let {
            communicator = it as SignOutCommunicator
            communicator?.onMaxTimeUsedDialogChangeState(
                dialogParameters = DialogParameters(
                    titleResource = R.string.empty,
                    descriptionResource = R.string.automatic_logout_dialog_description,
                    positiveResource = R.string.automatic_logout_dialog_keep_button,
                    isActive = mutableStateOf(true),
                    positiveAction = { countDownTimer.restartTimer() },
                    isCancelable = false,
                    additionalText = time.toInt().toString()
                )
            )
        }
    }

    private fun hideTimerDialog() {
        communicator?.onMaxTimeUsedDialogChangeState(
            dialogParameters = DialogParameters(
                isActive = mutableStateOf(false)
            )
        )
    }

    private fun onMyProductClick(expand: Boolean) {
        uiState = uiState.copy(isExpandedByClick = expand)
    }

    private fun onSetupSessionListener(activity: Activity?) {
        activity?.let { safeActivity ->
            communicator = safeActivity as SignOutCommunicator
            viewModelScope.launch {
                communicator?.isSessionDuplicated()?.let {
                    uiState = uiState.copy(isSessionDuplicated = it)
                }
            }
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

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isSessionDuplicated: Flow<Boolean> = flowOf(false),
        var quickActions: List<QuickAction>? = null,
        var miniCardList: List<MiniCardsItem>? = null,
        var configurationVersion: ConfigurationVersion? = null,
        var validateUserStatus: ValidateUserStatus? = null,
        var balance: Balance? = null,
        var cryptoHistoricalBalance: List<HistoricalBalanceClient> = emptyList(),
        var idBrand: String = "",
        var pkUser: String = "",
        var identification: String = "",
        var email: String = "",
        var userName: String = "",
        var homeState: HomeState = HomeState.OLD_STATE,
        var productScreenPagerState: PagerState? = null,
        var productPageList: List<ProductPage> = emptyList(),
        val smartMovementsList: List<SmartMovementsResult> = emptyList(),
        val creditMovementsList: List<CreditMovementsResult> = emptyList(),
        val showCardIssuanceError: Boolean = false,
        val toastIsVisible: Boolean = false,
        val releaseToastIsVisible: Boolean = false,
        var isExpandedByClick: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnBottomNavigationItemClick -> navigation(
                uiEvent.innerNavHostController,
                uiEvent.route
            )
            is UIEvent.OnSignOut -> signOut(uiEvent.activity)
            is UIEvent.OnShowTimerDialog -> showTimerDialog(uiEvent.time, uiEvent.activity)
            is UIEvent.OnSetUserData -> onsetUserData()
            is UIEvent.OnSetHomeState -> onSetHomeState(uiEvent.homeState)
            is UIEvent.OnOpenQuickActionFlow -> openQuickActionFlow(flow = uiEvent.flow)
            is UIEvent.OnGetSmartMovements -> onGetSmartMovements(
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.identificationNumber,
                uiEvent.tokenNumber
            )
            is UIEvent.OnGetCreditMovements -> onGetCreditMovements(
                uiEvent.idBrand,
                uiEvent.idLoanClient
            )
            is UIEvent.OnShowUnlinkToast -> {
                uiState = uiState.copy(toastIsVisible = true)
            }
            is UIEvent.OnHideUnlinkToast -> {
                uiState = uiState.copy(toastIsVisible = false)
            }
            is UIEvent.OnShowAutomaticPaymentEdit -> emitBaseEvent(BaseEvent.OnShowAutomaticPaymentEditBottomSheet)
            is UIEvent.OnHideAutomaticPaymentEdit -> emitBaseEvent(BaseEvent.OnHideAutomaticPaymentEditBottomSheet)
            is UIEvent.OnEditAutomaticPayment -> emitBaseEvent(BaseEvent.OnEditAutomaticPaymentEvent)
            is UIEvent.OnDeleteAutomaticPayment -> emitBaseEvent(BaseEvent.OnDeleteAutomaticPaymentEvent)
            is UIEvent.OnCallMutationDeactivateClientAutomaticDebit -> onCallGetClientAutomaticDebitUseCase()
            is UIEvent.OnMyProductClick -> onMyProductClick(uiEvent.expand)
            is UIEvent.OnMyProductPageChange ->
                uiState = uiState.copy(productScreenPagerState = uiEvent.page)
            is UIEvent.OnLoadingValueChanged ->
                uiState = uiState.copy(isLoading = uiEvent.isLoading)
            is UIEvent.OnShowCardIssuanceError -> uiState =
                uiState.copy(showCardIssuanceError = true)
            is UIEvent.OnCloseCardIssuanceError -> uiState =
                uiState.copy(showCardIssuanceError = false)
            is UIEvent.OnStartBiometrics -> onStartBiometrics()
            is UIEvent.OnInitializeBiometricPrompt -> initializeBiometricPrompt(
                uiEvent.biometricPromptTitle,
                uiEvent.biometricPromptDescription,
                uiEvent.biometricPromptNegative
            )
            is UIEvent.OnUpdateIsExpandedByClick ->
                uiState = uiState.copy(isExpandedByClick = uiEvent.isExpandedByClick)
            is UIEvent.OnSetupSessionListener -> onSetupSessionListener(uiEvent.activity)
            is UIEvent.OnSessionDuplicated -> onSessionDuplicated()
            is UIEvent.OnHideReleaseToast -> {
                uiState = uiState.copy(releaseToastIsVisible = false)
            }
            is UIEvent.OnShowReleaseToast -> {
                uiState = uiState.copy(releaseToastIsVisible = true)
            }
            UIEvent.OnRegisterAdjustPressPurchaseFirstTime -> registerAdjustFirstPressPurchaseEvent()
            UIEvent.OnRegisterAdjustPressReceiveFirstTime -> registerAdjustFirstPressReceiveEvent()
            UIEvent.OnRegisterAdjustPressSellFirstTime -> registerAdjustFirstPressSellEvent()
            UIEvent.OnRegisterAdjustPressSendFirstTime -> registerAdjustFirstPressSendEvent()
        }
    }

    sealed class UIEvent {
        data class OnUpdateIsExpandedByClick(val isExpandedByClick: Boolean) : UIEvent()
        data class OnOpenQuickActionFlow(val flow: String) : UIEvent()
        data class OnBottomNavigationItemClick(
            val innerNavHostController: NavHostController,
            val route: String
        ) :
            UIEvent()

        data class OnGetSmartMovements(
            val user: String,
            val idBrand: Int,
            val identificationNumber: String,
            val tokenNumber: Long
        ) : UIEvent()

        data class OnGetCreditMovements(
            val idBrand: Int,
            val idLoanClient: Int
        ) : UIEvent()

        data class OnMyProductClick(val expand: Boolean) : UIEvent()
        data class OnMyProductPageChange(val page: PagerState) : UIEvent()
        object OnSetUserData : UIEvent()
        data class OnSetHomeState(val homeState: HomeState) : UIEvent()
        data class OnSetupSessionListener(val activity: Activity?) : UIEvent()
        data class OnSignOut(val activity: Activity?) : UIEvent()
        object OnSessionDuplicated : UIEvent()
        data class OnShowTimerDialog(val time: Long, val activity: Activity?) : UIEvent()
        object OnShowUnlinkToast : UIEvent()
        object OnHideUnlinkToast : UIEvent()
        object OnShowReleaseToast : UIEvent()
        object OnHideReleaseToast : UIEvent()
        object OnShowAutomaticPaymentEdit : UIEvent()
        object OnHideAutomaticPaymentEdit : UIEvent()
        object OnEditAutomaticPayment : UIEvent()
        object OnDeleteAutomaticPayment : UIEvent()
        object OnCallMutationDeactivateClientAutomaticDebit : UIEvent()
        data class OnLoadingValueChanged(val isLoading: Boolean) : UIEvent()
        object OnShowCardIssuanceError : UIEvent()
        object OnCloseCardIssuanceError : UIEvent()
        object OnStartBiometrics : UIEvent()
        data class OnInitializeBiometricPrompt(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String
        ) : UIEvent()
        object OnRegisterAdjustPressPurchaseFirstTime : UIEvent()
        object OnRegisterAdjustPressSellFirstTime : UIEvent()
        object OnRegisterAdjustPressSendFirstTime : UIEvent()
        object OnRegisterAdjustPressReceiveFirstTime : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenQuickActionsBottomSheet : BaseEvent()
        object OnOpenMyProductsBottomSheet : BaseEvent()
        data class OnQuickActionClicked(val flow: String)
        data class OnMiniCardsClicked(val flow: String)
        object OnShowAutomaticPaymentEditBottomSheet : BaseEvent()
        object OnHideAutomaticPaymentEditBottomSheet : BaseEvent()
        object OnEditAutomaticPaymentEvent : BaseEvent()
        object OnDeleteAutomaticPaymentEvent : BaseEvent()
        object OnDeleteAutomaticPaymentToastEvent : BaseEvent()
        object OnPhoneNumberChangedToastEvent : BaseEvent()
        object OnEmailChangedToastEvent : BaseEvent()
    }

    companion object {
        const val API_CALLS_TOTAL = 6
    }
}
