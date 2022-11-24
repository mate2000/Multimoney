package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.QuickAction
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.catalog.ConfigurationPlatform
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.HomeBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.ProductsBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.QuickActionBNScreen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnStartCountDownTimer
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnBottomNavigationItemClick
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSignOut
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val countDownTimer: MMCountDownTimer,
    private val dataStorePreferences: DataStorePreferences,
    private val queryBalanceUseCase: QueryBalanceUseCase,
    private val queryValidateUserStatusUseCase: QueryValidateUserStatusUseCase,
    private val queryGetConfigurationVersionUseCase: QueryGetConfigurationVersionUseCase,
    private val querytGetQuickActionsUseCase: QueryGetQuickActionsUseCase
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

    private fun callQueryGetQuickActions(
        idBrand : Int,
        pkUser : Int,
        identification : String,
        infoCreditStatus : Int,
        infoVirtualCardStatus : Int,
        infoBankAccountStatus : Int,
        infoCriptoStatus : Int
    ) = executeUseCase {
        querytGetQuickActionsUseCase.invoke(
            idBrand =  idBrand,
            pkUser = pkUser,
            identification = identification,
            infoCreditStatus = infoCreditStatus,
            infoVirtualCardStatus = infoVirtualCardStatus,
            infoBankAccountStatus = infoBankAccountStatus,
            infoCriptoStatus = infoCriptoStatus
        ).collectLatest { result ->
            result.onSuccess { balance ->
                balance?.let {
                    if (uiState.isLoading)
                        uiState = uiState.copy(
                            isLoading = false,
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
                    if (uiState.isLoading)
                        uiState = uiState.copy(isLoading = false)
                    uiState = uiState.copy(balance = balance)

                    //this.balance = balance
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
//                    if (uiState.isLoading)
//                        uiState = uiState.copy(isLoading = false)
                    uiState = uiState.copy(configurationVersion = configurationVersion)
                    //this.configurationVersion = configurationVersion
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
                //this.validateUserStatus = validateUserStatus
                callQueryBalanceUseCase(
                    user = email,
                    identification = identification,
                    idBrand = idBrand,
                    idClient = validateUserStatus?.infoUser?.idClient ?: 0,
                    idLoanClient = validateUserStatus?.infoCredit?.idLoanClient ?: 0,
                    creditStatus = validateUserStatus?.infoCredit?.status ?: 0,
                    accountStatus = validateUserStatus?.infoBankAccount?.status ?: 0,
                    cryptoStatus = validateUserStatus?.infoCrypto?.status ?: 0,
                    // cardStatus = uiState.userStatus?.infoVirtualCard?.status ?: 0
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

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var quickActions : List<QuickAction>? = listOf(),
        var configurationVersion: ConfigurationVersion? = null,
        var validateUserStatus: ValidateUserStatus? = null,
        var balance: Balance? = null,
        var idBrand: String = "",
        var pkUser: String = "",
        var identification: String = "",
        var email: String = "",
        var userName: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBottomNavigationItemClick -> navigation(uiEvent.innerNavHostController, uiEvent.route)
            is OnSignOut -> popAndNavigateTo(Screen.SignInScreen.route, Screen.HomeScreen.route)
            is OnSetUserData -> onsetUserData()
        }
    }

    sealed class UIEvent {
        data class OnBottomNavigationItemClick(val innerNavHostController: NavHostController, val route: String) :
            UIEvent()

        object OnSetUserData : UIEvent()
        object OnSignOut : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenQuickActionsBottomSheet : BaseEvent()
        object OnOpenMyProductsBottomSheet : BaseEvent()
        data class OnStartCountDownTimer(val millisInFuture: Long?)
    }
}
