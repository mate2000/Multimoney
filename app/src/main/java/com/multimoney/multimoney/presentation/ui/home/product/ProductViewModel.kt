package com.multimoney.multimoney.presentation.ui.home.product

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnBalanceSuccess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetIdBrand
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnLastStepChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToTestScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToVisaActivateScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    private fun onGetUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(idBrand = dataStorePreferences.getIdBrand().first())
            pkUser = dataStorePreferences.getPkUser().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()

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
                cardStatus = cardStatus,
            ).collectLatest { result ->
                result.onSuccess { balance ->
                    uiState = uiState.copy(isLoading = false)
                    balance?.let {
                        balanceCredit = it
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
                    uiState = uiState.copy(isLoading = false)
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
            cardStatus = uiState.userStatus?.infoVirtualCard?.status ?: 0,
        )
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(isLoading = false)
        openDialog = DialogParameters(
            description = error.getError() ?: "",
            isActive = mutableStateOf(true)
        )
    }

    private fun onNavigateToCreditScreen() {
        navigateTo(
            "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/${pkUser}/${identification}/${email}/${lastStep}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}"
        )
    }

    private fun onNavigatoToTestScren() {
        navigateTo(
            Screen.PaymentFeeScreen.route
        )
    }

    private fun onNavigateToVisaActivateScreen() =
        navigateTo("${Screen.VisaIssuanceScreen.baseRoute}/${uiState.idBrand}")

    private fun onProductClick(context: Context, whatsAppLink: String) {
        when {
            uiState.userStatus?.infoCredit?.infoPreApprove?.statusFirm != CreditOnFidoOrFirmStatus.APPROVED.status -> onNavigateToCreditScreen()
            uiState.userStatus?.infoCredit?.status == CreditStatus.APPROVED_CREDIT.status -> navigateTo(
                Screen.CreditScreen.route
            )
            uiState.userStatus?.infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status -> navigateTo(
                Screen.CreditScreen.route
            )
            uiState.userStatus?.infoCredit?.status == CreditStatus.CREDIT_NOT_PRE_APPROVED.status || uiState.userStatus?.infoCredit?.status == CreditStatus.CREDIT_REJECTED.status -> {
                openWhatsAppLink(context, whatsAppLink)
            }
            else -> navigateTo(Screen.CreditScreen.route)
        }
    }

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
    }

    fun evaluateCardCondition(action: String, validateUserStatus: ValidateUserStatus): Boolean {
        validateUserStatus.apply {
            return when (action) {
                CREDIT_INITIAL_CARD -> {
                    infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status
                            && infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.PENDING.status
                            && (infoCredit?.infoPreApprove?.currentStep.isNullOrEmpty()
                            || validateUserStatus.infoCredit?.infoPreApprove?.currentStep == CREDIT_STEP_PRE_APPROVED)
                }
                CREDIT_MAX_ATTEMPTS -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.OVER_COUNTER.status
                }
                CREDIT_IDENTITY_INCOMPLETE -> {
                    (infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status) && (CreditStep.Search.getIdByName(
                        infoCredit?.infoPreApprove?.currentStep
                    ) == CreditStep.Six.id)
                }
                CREDIT_INFO_INCOMPLETE -> {
                    (infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status) && (CreditStep.Search.getIdByName(
                        infoCredit?.infoPreApprove?.currentStep
                    ) < CreditStep.Six.id)
                }
                CREDIT_REJECTED -> {
                    infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.REJECTED.status
                }
                else -> false
            }
        }
    }

    data class UIState(
        //Fields
        var idBrand: String = "",
        var userStatus: ValidateUserStatus? = null,
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBalanceSuccess -> balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> onValidateUserStatusSuccess(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen()
            is OnNavigateToTestScreen -> onNavigatoToTestScren()
            is OnNavigateToVisaActivateScreen -> onNavigateToVisaActivateScreen()
            is OnProductClick -> onProductClick(uiEvent.context, uiEvent.whatsAppLink)
            is OnGetIdBrand -> onGetUserData()
            is UIEvent.OnMaxAttemptsCardClick -> openWhatsAppLink(
                uiEvent.context,
                uiEvent.whatsAppLink
            )
            is OnLastStepChange -> lastStep = uiEvent.lastStep
        }
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
        object OnNavigateToTestScreen : UIEvent()
        object OnNavigateToVisaActivateScreen : UIEvent()
        data class OnProductClick(
            val whatsAppLink: String,
            val context: Context
        ) : UIEvent()

        object OnGetIdBrand : UIEvent()
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

    companion object {
        const val CREDIT_STEP_PRE_APPROVED = "CREDIT_STEP_PREAPROBADO"
        const val CREDIT_INITIAL_CARD = "CREDIT_INITIAL_CARD"
        const val CREDIT_MAX_ATTEMPTS = "CREDIT_MAX_ATTEMPTS"
        const val CREDIT_IDENTITY_INCOMPLETE = "CREDIT_IDENTITY_INCOMPLETE"
        const val CREDIT_INFO_INCOMPLETE = "CREDIT_INFO_INCOMPLETE"
        const val CREDIT_REJECTED = "CREDIT_REJECTED"
    }
}
