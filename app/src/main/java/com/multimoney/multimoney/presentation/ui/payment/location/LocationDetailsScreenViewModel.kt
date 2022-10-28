package com.multimoney.multimoney.presentation.ui.payment.location


import android.content.Context
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_ADDRESS
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_LATITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_LONGITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_OPENING_TIME
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_MAPS_PAYMENT_ID
import com.multimoney.multimoney.presentation.util.openMapsLink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {


    // Stateless
    var locationName: String = ""
    var locationAddress: String = ""
    var locationOpeningTime: String = ""
    var paymentAmount: String = ""
    var paymentId: String = ""
    var latitude: String = ""
    var longitude: String = ""

    init {
        locationName = savedStateHandle[LOCATION_MAPS_NAME] ?: "1"
        locationAddress = savedStateHandle[LOCATION_MAPS_ADDRESS] ?: "2"
        locationOpeningTime = savedStateHandle[LOCATION_MAPS_OPENING_TIME] ?: "3"
        paymentAmount = savedStateHandle[LOCATION_MAPS_PAYMENT_AMOUNT] ?: "4"
        paymentId = savedStateHandle[LOCATION_MAPS_PAYMENT_ID] ?: "5"
        latitude = savedStateHandle[LOCATION_MAPS_LATITUDE] ?: "6"
        longitude = savedStateHandle[LOCATION_MAPS_LONGITUDE] ?: "7"
    }

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