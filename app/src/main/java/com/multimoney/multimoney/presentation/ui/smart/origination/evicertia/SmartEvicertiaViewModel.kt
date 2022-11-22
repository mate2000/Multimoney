package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartEvicertiaViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(isLoading = false)
        openDialog = DialogParameters(
            description = error.getError() ?: "",
            isActive = mutableStateOf(true)
        )
    }

    data class UIState(
        // Fields
        val html: String = "",
        var isLoading: Boolean = false
    )
}