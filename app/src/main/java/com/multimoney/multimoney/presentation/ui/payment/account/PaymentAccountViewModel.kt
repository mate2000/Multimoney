package com.multimoney.multimoney.presentation.ui.payment.account

import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentAccountViewModel @Inject constructor() : BaseViewModel(true) {
    // uiState
//    var uiState by mutableStateOf(UIState())
//        private set

//    private fun getTextResources(currency: String) {
//        when(currency) {
//            currency -> {
//                uiState = uiState.copy(
//                    titleResource = R.string.visa_activate_gt_no_nfc_title,
//                    subtitleResource = R.string.visa_activate_gt_no_nfc_subtitle
//                )
//            }
//        }
//    }
//
//    data class UIState(
//        // Interactions
//        val isTextVisible: Boolean = false,
//        val titleResource: Int = R.string.empty,
//        val subtitleResource: Int = R.string.empty
//    )
//
//    fun onUIEvent(uiEvent: UIEvent) {
//        when (uiEvent) {
//            is OnNavigateBack -> popAndNavigateTo(
//                route = Screen.HomeScreen.route,
//                popTo = Screen.VisaActivateScreen.route
//            )
//            is OnGetTextResources -> getTextResources(uiEvent.idBrand.toInt())
//        }
//
//    }
//
//    sealed class UIEvent {
//        object OnNavigateBack : UIEvent()
//        class OnGetTextResources(val idBrand: String) : UIEvent()
//    }
}