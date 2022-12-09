package com.multimoney.multimoney.presentation.ui.visa.issuance

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnIssuanceClick
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaIssuanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nfcHelper: NfcHelper
) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var balanceCardInformation: BalanceCardInformation? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND) ?: 0
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
        getTextResources()
    }

    private fun getTextResources() {
        when {
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_nfc_subtitle
                )
            }
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_no_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_no_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_no_nfc_subtitle
                )
            }
        }
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val titleResource: Int = R.string.empty,
        val subtitleResource: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnIssuanceClick -> Toast.makeText(uiEvent.context, "Topkenizar tarjeta", Toast.LENGTH_SHORT).show()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnIssuanceClick(val context: Context) : UIEvent()
    }
}
