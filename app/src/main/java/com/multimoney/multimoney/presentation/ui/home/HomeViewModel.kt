package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryGetCoreBankMovementsUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.credit.MutationDeactivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.interaction.security.QueryMiniCardsUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.balance.Balance
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
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.HomeBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.ProductsBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.QuickActionBNScreen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnDeleteAutomaticPaymentEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnDeleteAutomaticPaymentToastEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnEditAutomaticPaymentEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnHideAutomaticPaymentEditBottomSheet
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnShowAutomaticPaymentEditBottomSheet
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnStartCountDownTimer
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnBottomNavigationItemClick
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCallMutationDeactivateClientAutomaticDebit
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnEditAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnGetSmartMovements
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnHideAutomaticPaymentEdit
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnShowAutomaticPaymentEdit
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSignOut
import com.multimoney.multimoney.presentation.util.INDEX_ONE
import com.multimoney.multimoney.presentation.util.LAST_THREE
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.boolean
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.ProductType
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    private val queryGetConfigurationVersionUseCase: QueryGetConfigurationVersionUseCase,
    private val queryMiniCardsUseCase: QueryMiniCardsUseCase,
    private val queryGetQuickActionsUseCase: QueryGetQuickActionsUseCase,
    private val getClientAutomaticDebitUseCase: QueryGetClientAutomaticDebitUseCase,
    private val mutationDeactivateClientAutomaticDebitUseCase: MutationDeactivateClientAutomaticDebitUseCase,
    private val queryGetCoreBankMovements: QueryGetCoreBankMovementsUseCase,
    private val cognitoHelper: CognitoHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onsetUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(
                idBrand = dataStorePreferences.getIdBrand().first(),
                pkUser = dataStorePreferences.getPkUser().first(),
                identification = dataStorePreferences.getIdentification().first(),
                email = dataStorePreferences.getUserEmail().first(),
                userName = dataStorePreferences.getUserName().first()
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
                    if (uiState.configurationVersion != null && uiState.balance != null) {
                        uiState = uiState.copy(isLoading = false)
                    }
                    uiState = uiState.copy(
                        quickActions = it.quickActions
                    )
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
            idBrand = idBrand,
        ).collectLatest { result ->
            result.onSuccess { miniCards ->
                if (
                    uiState.configurationVersion != null
                    && uiState.balance != null
                    && uiState.quickActions != null
                ) {
                    uiState = uiState.copy(isLoading = false)
                }
                uiState = uiState.copy(
                    miniCardList = miniCards.miniCardsList.toMutableList().sortedBy { it.priority }
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
                balance?.let { setBalance(it) }
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
                enabled = true,
                index = defaultIndex,
                resourceIcon = R.drawable.ic_my_credit,
                resourceText = R.string.home_my_products_label_credit
            )
        )
        // If idBrand is different from Guatemala enable Smart
        if (uiState.idBrand != Brand.Guatemala.id.toString()) {
            if (balance.balanceAccountSmart.isNullOrEmpty().not()) {
                // Add the amount of account smart that user has
                balance.balanceAccountSmart?.forEachIndexed { index, account ->
                    productPageList.add(
                        ProductPage(
                            product = ProductType.Smart.value,
                            productSmartIndex = index,
                            enabled = true,
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

                    onGetSmartMovements(
                        uiState.userName,
                        uiState.idBrand.toInt(),
                        uiState.identification,
                        account?.tokenNumber?.toLongOrNull() ?: 0
                    )
                }
                // If user has smart activated he can enable crypto
                productPageList.add(
                    ProductPage(
                        product = ProductType.Crypto.value,
                        enabled = true,
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
                        enabled = true,
                        index = productPageList.lastIndex + 1,
                        resourceText = R.string.home_my_products_label_smart,
                        resourceIcon = R.drawable.ic_dollars_strong
                    )
                )
                productPageList.add(
                    ProductPage(
                        product = ProductType.Crypto.value,
                        enabled = true,
                        index = productPageList.lastIndex + 1,
                        resourceIcon = R.drawable.ic_union,
                        resourceText = R.string.home_my_products_label_crypto
                    )
                )
            }
        }

        if (uiState.configurationVersion != null && uiState.quickActions != null) {
            uiState = uiState.copy(isLoading = false)
        }
        uiState = uiState.copy(balance = balance, productPageList = productPageList)
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
                if (uiState.balance != null && uiState.quickActions != null && uiState.validateUserStatus != null) {
                    uiState = uiState.copy(isLoading = false)
                }
                configurationVersion?.let {
                    uiState = uiState.copy(configurationVersion = configurationVersion)
                }
                emitBaseEvent(
                    OnStartCountDownTimer(
                        configurationVersion?.configuration?.timeSession?.toLong() ?: 0
                    )
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
                dataStorePreferences.setUserPhoneNumber(validateUserStatus?.infoUser?.phone.orEmpty())
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
                    cardStatus = 0 // TODO, the API doesn't support this yet
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
    }

    private fun callMutationDeactivateClientAutomaticDebitUseCase(origin: String, idAccount: Long) = executeUseCase {
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
                    cardStatus = 0 // TODO, the API doesn't support this yet
                )
                emitBaseEvent(OnDeleteAutomaticPaymentToastEvent)
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

    fun navigation(innerNavHostController: NavHostController, route: String) {
        when (route) {
            HomeBNScreen.route -> {
                innerNavigateTo(innerNavHostController, route)
            }
            QuickActionBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenQuickActionsBottomSheet)
            }
            ProductsBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenMyProductsBottomSheet)
            }
        }
    }

    private fun openQuickActionFlow(flow: String) {
        emitBaseEvent(BaseEvent.OnQuickActionClicked(flow))
    }

    private fun signOut() {
        cognitoHelper.signOut(signOutError = {
            Timber.d("SignOut Error")
        })
        viewModelScope.launch {
            dataStorePreferences.setAuthToken("")
        }
        countDownTimer.discardTimer()
        popAndNavigateTo(
            Screen.SignInScreen.route,
            Screen.HomeScreen.route
        )
    }

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var quickActions: List<QuickAction>? = null,
        var miniCardList: List<MiniCardsItem>? = null,
        var configurationVersion: ConfigurationVersion? = null,
        var validateUserStatus: ValidateUserStatus? = null,
        var balance: Balance? = null,
        var idBrand: String = "",
        var pkUser: String = "",
        var identification: String = "",
        var email: String = "",
        var userName: String = "",
        var forceIsExpanded: Boolean = false,
        var productScreenPagerState: PagerState? = null,
        var productPageList: List<ProductPage> = emptyList(),
        val smartMovementsList: List<SmartMovementsResult> = emptyList()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBottomNavigationItemClick -> navigation(uiEvent.innerNavHostController, uiEvent.route)
            is OnSignOut -> signOut()
            is OnSetUserData -> onsetUserData()
            is UIEvent.OnOpenQuickActionFlow -> openQuickActionFlow(flow = uiEvent.flow)
            is OnGetSmartMovements -> onGetSmartMovements(
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.identificationNumber,
                uiEvent.tokenNumber
            )
            is OnShowAutomaticPaymentEdit -> emitBaseEvent(OnShowAutomaticPaymentEditBottomSheet)
            is OnHideAutomaticPaymentEdit -> emitBaseEvent(OnHideAutomaticPaymentEditBottomSheet)
            is OnEditAutomaticPayment -> emitBaseEvent(OnEditAutomaticPaymentEvent)
            is OnDeleteAutomaticPayment -> emitBaseEvent(OnDeleteAutomaticPaymentEvent)
            is OnCallMutationDeactivateClientAutomaticDebit -> onCallGetClientAutomaticDebitUseCase()
            is UIEvent.OnMyProductClick -> uiState = uiState.copy(forceIsExpanded = uiEvent.expand)
            is UIEvent.OnMyProductPageChange -> uiState = uiState.copy(productScreenPagerState = uiEvent.page)
        }
    }

    sealed class UIEvent {
        data class OnOpenQuickActionFlow(val flow: String) : UIEvent()
        data class OnBottomNavigationItemClick(val innerNavHostController: NavHostController, val route: String) :
            UIEvent()

        data class OnGetSmartMovements(
            val user: String,
            val idBrand: Int,
            val identificationNumber: String,
            val tokenNumber: Long
        ) : UIEvent()

        data class OnMyProductClick(val expand: Boolean) : UIEvent()
        data class OnMyProductPageChange(val page: PagerState) : UIEvent()
        object OnSetUserData : UIEvent()
        object OnSignOut : UIEvent()

        object OnShowAutomaticPaymentEdit : UIEvent()
        object OnHideAutomaticPaymentEdit : UIEvent()
        object OnEditAutomaticPayment : UIEvent()
        object OnDeleteAutomaticPayment : UIEvent()
        object OnCallMutationDeactivateClientAutomaticDebit : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenQuickActionsBottomSheet : BaseEvent()
        object OnOpenMyProductsBottomSheet : BaseEvent()
        data class OnStartCountDownTimer(val millisInFuture: Long?)
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
}
