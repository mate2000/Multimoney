package com.multimoney.multimoney.presentation.ui.credit.payment.location


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_ADDRESS
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_LATITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_LONGITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.LOCATION_OPENING_TIME
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_ID
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnCloseScreenClick
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnDialogPositiveButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnNavigateMapsClick
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.openMapsLink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var locationName: String = ""
    var locationAddress: String = ""
    var locationOpeningTime: String = ""
    var paymentAmount: String = ""
    var paymentId: String = ""
    var latitude: String = ""
    var longitude: String = ""

    init {
        locationName = savedStateHandle[LOCATION_NAME] ?: ""
        locationAddress = savedStateHandle[LOCATION_ADDRESS] ?: ""
        locationOpeningTime = savedStateHandle[LOCATION_OPENING_TIME] ?: ""
        paymentAmount = savedStateHandle[PAYMENT_AMOUNT] ?: "4"
        paymentId = savedStateHandle[PAYMENT_ID] ?: "5"
        latitude = savedStateHandle[LOCATION_LATITUDE] ?: "6"
        longitude = savedStateHandle[LOCATION_LONGITUDE] ?: "7"
    }

    private fun onNavigateHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentPointsScreen.route, isRestart = false)

    private fun onNavigateMapsClick(context: Context, latitude: String, longitude: String) {
        context.openMapsLink(latitude, longitude)
    }

    private fun onCloseScreen() {
        uiState = uiState.copy(
            dialogParameters = uiState.dialogParameters.copy(
                isActive = mutableStateOf(true),
                positiveAction = { onUIEvent(OnDialogPositiveButtonClick) }
            )
        )
    }

    data class UIState(
        val dialogParameters: DialogParameters = DialogParameters(
            titleResource = R.string.payment_points_dialog_title,
            descriptionResource = R.string.payment_points_dialog_description,
            isActive = mutableStateOf(false),
            positiveResource = R.string.payment_points_dialog_positive_button,
            negativeResource = R.string.payment_points_dialog_negative_button
        )
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateMapsClick -> onNavigateMapsClick(
                event.context,
                event.latitude,
                event.longitude
            )
            OnCloseScreenClick -> onCloseScreen()
            OnNavigateBack -> onNavigateBack()
            OnDialogPositiveButtonClick -> onNavigateHome()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseScreenClick : UIEvent()
        data class OnNavigateMapsClick(
            val context: Context,
            val latitude: String,
            val longitude: String
        ) : UIEvent()

        object OnDialogPositiveButtonClick : UIEvent()
    }
}