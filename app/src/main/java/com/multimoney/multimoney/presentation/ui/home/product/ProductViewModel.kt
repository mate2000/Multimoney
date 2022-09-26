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
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToVisaActivateScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnValidateUserSuccess
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Tertiary
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
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

    private fun onGetUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(
                idBrand = dataStorePreferences.getIdBrand().first(),
                pkUser = dataStorePreferences.getPkUser().first(),
                identification = dataStorePreferences.getIdentification().first(),
                email = dataStorePreferences.getUserEmail().first()
            )
            callQueryValidateUserStatus(
                uiState.pkUser.toInt(),
                uiState.identification,
                uiState.email,
                uiState.idBrand.toInt()
            )
        }
    }

    // TODO: Remove hardcoded parameters
    private fun callQueryBalanceUseCase(
        user: String = "ecruzGrapqhql",
        identification: String = "303190775",
        idBrand: Int = Brand.CostaRica.id,
        idClient: String = "192656",
        idLoanClient: Int = 223034
    ) {
        executeUseCase {
            queryBalanceUseCase.invoke(
                user = user,
                identification = identification,
                idBrand = idBrand,
                idClient = idClient,
                idLoanClient = idLoanClient
            ).collectLatest { result ->
                result.onSuccess { balance ->
                    uiState = uiState.copy(isLoading = false)
                    balance?.let {
                        uiState = uiState.copy(balanceCredit = balance)
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
        uiState = uiState.copy(
            userStatus = userStatus,
            productType = getProductBackgroundType(userStatus)
        )
        // TODO: Send parameters to balance from userStatus
        callQueryBalanceUseCase()
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
            "${Screen.CreditScreen.baseRoute}/${uiState.idBrand}/${uiState.pkUser}/${uiState.identification}/${uiState.email}/${uiState.lastStep}/${uiState.userStatus?.infoCredit?.infoPreApprove?.idUserRequest}"
        )
    }

    private fun onNavigateToVisaActivateScreen() {
        navigateTo(
            "${Screen.VisaActivateScreen.baseRoute}/${uiState.idBrand}"
        )
    }

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

    private fun getProductBackgroundType(userStatus: ValidateUserStatus) = when {
        userStatus.infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status -> Primary
        else -> Tertiary
    }

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
    }

    data class UIState(
        //Fields
        var idBrand: String = "",
        var pkUser: String = "",
        var identification: String = "",
        var email: String = "",
        var lastStep: Int = 1,
        var balanceCredit: Balance? = null,
        var userStatus: ValidateUserStatus? = null,
        var productType: ProductBackGroundType = Tertiary,
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBalanceSuccess -> uiState.balanceCredit = uiEvent.balance
            is OnValidateUserSuccess -> onValidateUserStatusSuccess(uiEvent.userStatus)
            is OnNavigateToCreditScreen -> onNavigateToCreditScreen()
            is OnNavigateToVisaActivateScreen -> onNavigateToVisaActivateScreen()
            is OnProductClick -> onProductClick(uiEvent.context, uiEvent.whatsAppLink)
            is OnGetIdBrand -> onGetUserData()
            is UIEvent.OnMaxAttemptsCardClick -> openWhatsAppLink(
                uiEvent.context,
                uiEvent.whatsAppLink
            )
            is OnLastStepChange -> uiState = uiState.copy(lastStep = uiEvent.lastStep)
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
    }
}