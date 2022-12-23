package com.multimoney.multimoney.presentation.ui.smart.payment.send

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SmartSendViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        // Interactions
        val amount: String = "",
        val selectedAccountTitle: String = "",
        val selectedAccountNumber: String = "",
        val receiverName: String = "",
        val receiverBank: String = "",
        val receiverAccount: String = "",
        val selectedBankIcon: Int = 0,
        val motive: String = "",
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
    )
}