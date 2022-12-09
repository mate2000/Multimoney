package com.multimoney.multimoney.presentation.ui.home.profile.help

import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber

@HiltViewModel
class HelpScreenViewModel(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
): BaseViewModel(true) {

    private fun getContactInfo() {
        // Todo get contact information from endpoint
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
            is UIEvent.OnGetContactInfo -> getContactInfo()
            is UIEvent.OnChatWithUsClick -> Timber.d("Open Whatsapp")
            is UIEvent.OnCallToAttentionCenterClick -> Timber.d("Open phone")
            is UIEvent.OnFAQClick -> Timber.d("Open FAQ website")
            is UIEvent.OnTermsAndConditionsClick -> Timber.d("Open Terms Website")
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetContactInfo : UIEvent()
        object OnChatWithUsClick : UIEvent()
        object OnCallToAttentionCenterClick : UIEvent()
        object OnFAQClick : UIEvent()
        object OnTermsAndConditionsClick : UIEvent()
    }
}