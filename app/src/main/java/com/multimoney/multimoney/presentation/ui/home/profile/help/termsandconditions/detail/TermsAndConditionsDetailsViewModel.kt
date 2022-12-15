package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.HTML
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TITLE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            html = savedStateHandle[HTML],
            title = savedStateHandle[TITLE]
        )
    }

    data class UIState(
        // Fields
        val html: String? = null,
        val title: String? = null,
        val termsAndConditionsSigned: TermsAndConditionsSigned? = null,
        val termsAndConditionsTitleResource: Int? = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(
                Screen.ProfileTermsAndConditionsScreen.route,
                false
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
    }
}
