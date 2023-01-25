package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.crypto.CryptoOperationSide
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class PurchaseCryptoSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    private var overridePreviousAction: (() -> Unit)? = null
    private var nextStep: Int = PurchaseCryptoSteps.One.id
    private var previousStep: Int = PurchaseCryptoSteps.One.id
    var nextAction: () -> Unit = {}

    //bundle parameters
    // la necesaria
    val comingFromDetails: Boolean = false
    val status = 1
    val idBrand = savedStateHandle[ID_BRAND] ?: DEFAULT_ID_BRAND_ERROR
//    val identification = savedStateHandle[IDENTIFICATION] ?: ""
    val user = savedStateHandle[USER] ?: ""
//    val pkUser = savedStateHandle[PK_USER] ?: ""
//    var asset = savedStateHandle[CRYPTO_ASSET] ?: ""
    val description = ""
    //val market = asset.plus(CurrencyType.Dollar.disbursementValue)
    val side = CryptoOperationSide.BUY.value
    // la de smart
    //val cryptoNetwork: String = ""
    val accountToken: String = ""
    val abvCurrency: String = if (idBrand == Brand.CostaRica.id) {
        CurrencyType.Colon.disbursementValue
    } else {
        CurrencyType.Dollar.disbursementValue
    }

    private fun previousStep() {
        if (overridePreviousAction != null) {
            overridePreviousAction?.invoke()
        } else {
            if (previousStep > PurchaseCryptoSteps.One.id || uiState.currentStep == PurchaseCryptoSteps.Two.id) {
                uiState = uiState.copy(
                    currentStep = previousStep
                )
            } else {
                navigateBackToHome()
            }
        }
    }

    private fun getTotalSteps(): Int {
        val counter = if (idBrand == Brand.CostaRica.id) {
            PURCHASE_CRYPTO_TOTAL_STEPS_CR
        } else if(idBrand == Brand.ElSalvador.id) {
            PURCHASE_CRYPTO_TOTAL_STEPS_SV
        } else if(idBrand == Brand.CostaRica.id && comingFromDetails) {
            PURCHASE_CRYPTO_TOTAL_STEPS_CR_DETAILS
        } else {
            PURCHASE_CRYPTO_TOTAL_STEPS_SV_DETAILS
        }
        return counter
    }

    private fun overridePreviousAction(overridePreviousAction: (() -> Unit)?) {
        this.overridePreviousAction = overridePreviousAction
    }

    private fun nextStep() {
        if (nextStep <= getTotalSteps()) {
            uiState = uiState.copy(
                currentStep = nextStep
            )
        }
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.UNEXPANDED
        )

    private fun onSetNavigation(
        nextAction: () -> Unit,
        overridePreviousAction: (() -> Unit)?,
        nextStep: Int,
        previousStep: Int
    ) {
        this.nextAction = nextAction
        this.overridePreviousAction = overridePreviousAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onShowBottomSheet() {
        uiState = if (uiState.bottomSheetState.isVisible) {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
            )
        } else {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        }
    }

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.smart_close_origination_dialog_title,
                positiveResource = R.string.common_leave,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val currentStep: Int = 1,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {}
    )

    fun onUIEvent(event: UIEvent) {
        when(event) {
            is UIEvent.OnSetNavigation -> onSetNavigation(
                nextAction = event.nextAction,
                overridePreviousAction = event.overridePreviousAction,
                nextStep = event.nextStep,
                previousStep = event.previousStep
            )
            UIEvent.OnNextStep -> nextStep()
            UIEvent.OnPreviousStep -> previousStep()
            UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OverridePreviousAction -> overridePreviousAction(event.action)
            UIEvent.OnCloseClick -> onCloseClick()
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val overridePreviousAction: (() -> Unit)? = null,
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OverridePreviousAction(val action: (() -> Unit)?) : UIEvent()
    }

    companion object {
        const val DEFAULT_ID_BRAND_ERROR = -1
        const val PURCHASE_CRYPTO_TOTAL_STEPS_CR = 4
        const val PURCHASE_CRYPTO_TOTAL_STEPS_SV = 3
        const val PURCHASE_CRYPTO_TOTAL_STEPS_CR_DETAILS = 3
        const val PURCHASE_CRYPTO_TOTAL_STEPS_SV_DETAILS = 2
    }
}