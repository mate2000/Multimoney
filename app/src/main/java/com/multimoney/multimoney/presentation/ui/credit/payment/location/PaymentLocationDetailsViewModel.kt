package com.multimoney.multimoney.presentation.ui.credit.payment.location

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_ADDRESS
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_LATITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_LONGITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_SCHEDULE
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnCloseScreenClick
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnDialogPositiveButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnNavigateMapsClick
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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
    var pointName: String = ""
    var pointAddress: String = ""
    var pointSchedule: String = ""
    var pointLatitude: String = ""
    var pointLongitude: String = ""
    var paymentAmount: String = ""
    var creditNumber: String = ""
    var idBrand: Int = 0

    init {
        pointName = savedStateHandle[POINT_NAME] ?: ""
        pointAddress = savedStateHandle[POINT_ADDRESS] ?: ""
        pointSchedule = savedStateHandle[POINT_SCHEDULE] ?: ""
        pointLatitude = savedStateHandle[POINT_LATITUDE] ?: ""
        pointLongitude = savedStateHandle[POINT_LONGITUDE] ?: ""
        paymentAmount = savedStateHandle[PAYMENT_AMOUNT] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
    }

    private fun onStart() {
        uiState = uiState.copy(
            informativeText = when (idBrand) {
                Brand.ElSalvador.id -> R.string.payment_location_maps_info_sv
                else -> R.string.payment_location_maps_info_gt
            }
        )
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
        val informativeText: Int = R.string.empty,
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
            is OnStart -> onStart()
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
        object OnStart : UIEvent()
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
