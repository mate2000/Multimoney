package com.multimoney.multimoney.presentation.ui.visa

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.visa.VisaActivateViewModel.UIEvent.OnNavigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaActivateViewModel @Inject constructor() : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.VisaActivateScreen.route
            )
        }

    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
    }
}