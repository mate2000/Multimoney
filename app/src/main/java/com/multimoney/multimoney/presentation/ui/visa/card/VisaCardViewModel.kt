package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.data.util.catalog.Brand.Guatemala
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnAvailableAmountClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNfcAvailable
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaCardViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val nfcHelper: NfcHelper) :
    BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var balanceCardInformation: BalanceCardInformation? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND)?.toInt() ?: 0
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
    }

    private fun onAvailableAmountClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                descriptionResource = when (idBrand) {
                    ElSalvador.id -> string.visa_card_sv_dialog_description_available
                    CostaRica.id -> string.visa_card_cr_dialog_description_available
                    Guatemala.id -> string.visa_card_gt_dialog_description_available
                    else -> string.empty
                },
                positiveResource = string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val availableAmount: String = "$900",
        val isNfcAvailable: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnAvailableAmountClick -> onAvailableAmountClick()
            is OnNfcAvailable -> uiState = uiState.copy(isNfcAvailable = nfcHelper.isNfcSupported())
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAvailableAmountClick : UIEvent()
        object OnNfcAvailable : UIEvent()
    }
}
