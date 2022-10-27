package com.multimoney.multimoney.presentation.ui.payment.location


import android.content.Context
import androidx.compose.ui.focus.FocusManager
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.openMapsLink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor(

) : BaseViewModel(true) {

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        navigateTo(
            route = Screen.HomeScreen.route
        )
    }

    private fun onNavigateMapsClick(context: Context, latitude: String, longitude: String) {
        context.openMapsLink(latitude, longitude)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnCloseClick -> onCloseClick(event.focusManager)
            is UIEvent.OnNavigateMapsClick -> onNavigateMapsClick(
                event.context,
                event.latitude,
                event.longitude
            )
        }
    }

    sealed class UIEvent {
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnNavigateMapsClick(
            val context: Context,
            val latitude: String,
            val longitude: String
        ) : UIEvent()
    }
}