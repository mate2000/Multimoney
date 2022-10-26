package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnAvailableAmountClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNfcAvailable
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.NfcHelper
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

    init {
        idBrand = savedStateHandle.get<String>(ID_BRAND)?.toInt() ?: 0
    }

    private fun onAvailableAmountClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                descriptionResource = when (idBrand) {
                    Brand.ElSalvador.id -> R.string.visa_card_sv_dialog_description_available
                    Brand.CostaRica.id -> R.string.visa_card_cr_dialog_description_available
                    Brand.Guatemala.id -> R.string.visa_card_gt_dialog_description_available
                    else -> R.string.empty
                },
                positiveResource = R.string.understood,
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
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.VisaCardScreen.route
            )
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
