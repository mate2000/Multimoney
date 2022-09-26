package com.multimoney.multimoney.presentation.ui.visa

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.visa.VisaActivateViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.visa.VisaActivateViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaActivateViewModel @Inject constructor(private val nfcHelper: NfcHelper) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    private fun getTextResources(idBrand: Int) {
        when {
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_sv_nfc_title,
                    subtitleResource = R.string.visa_activate_sv_nfc_subtitle
                )
            }
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_sv_no_nfc_title,
                    subtitleResource = R.string.visa_activate_sv_no_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_cr_nfc_title,
                    subtitleResource = R.string.visa_activate_cr_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_cr_no_nfc_title,
                    subtitleResource = R.string.visa_activate_cr_no_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_gt_nfc_title,
                    subtitleResource = R.string.visa_activate_gt_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_activate_gt_no_nfc_title,
                    subtitleResource = R.string.visa_activate_gt_no_nfc_subtitle
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
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.VisaActivateScreen.route
            )
            is OnGetTextResources -> getTextResources(uiEvent.idBrand.toInt())
        }

    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        class OnGetTextResources(val idBrand: String) : UIEvent()
    }
}