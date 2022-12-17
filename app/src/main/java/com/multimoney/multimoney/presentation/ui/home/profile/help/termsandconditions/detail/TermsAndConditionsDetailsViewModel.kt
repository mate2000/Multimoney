package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.detail

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.DATE_SIGNED
import com.multimoney.multimoney.presentation.navigation.HTML
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TITLE
import com.multimoney.multimoney.presentation.navigation.VERSION
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            html =  Base64.decode(savedStateHandle[HTML] ?: "", Base64.DEFAULT).toString(Charsets.UTF_8),
            title = savedStateHandle[TITLE],
            version = savedStateHandle[VERSION],
            dateSigned = savedStateHandle[DATE_SIGNED]
        )
    }

    data class UIState(
        // Fields
        val html: String? = null,
        val title: String? = null,
        val version : String? = null,
        val dateSigned : String? = null,
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
