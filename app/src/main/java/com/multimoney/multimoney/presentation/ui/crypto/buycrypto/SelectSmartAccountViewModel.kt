package com.multimoney.multimoney.presentation.ui.crypto.buycrypto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.balance.Account
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        val isLoading: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val accounts: List<Account> = listOf()
    )
}