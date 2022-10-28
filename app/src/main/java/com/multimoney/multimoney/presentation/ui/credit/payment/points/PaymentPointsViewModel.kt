package com.multimoney.multimoney.presentation.ui.credit.payment.points

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryGetPaymentPointsUseCase
import com.multimoney.domain.model.credit.PaymentPoint
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnCloseScreenClick
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnGetPaymentPoints
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnQueryValueChange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentPointsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryGetPaymentPointsUseCase: QueryGetPaymentPointsUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
    }

    private fun onItemPointClick() {
        // TODO Navigate to item details
    }

    private fun onQueryValueChange(value: String) {
        uiState = uiState.copy(
            queryValue = value
        )
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = "", // TODO define the previous screen
            popTo = Screen.PaymentPointsScreen.route
        )
    }

    private fun onCloseScreen() {
        // TODO insertar dialog
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.PaymentPointsScreen.route
        )
    }

    private fun onGetPaymentPoints() =
        executeUseCase {
            queryGetPaymentPointsUseCase.invoke(
                idBrand = idBrand ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(
                        pointsItemsList = it ?: listOf()
                    )
                }.onFailure {
                }.onLoading {
                    // onLoadingValueChange(true)
                }
            }
        }

    data class UIState(
        // Interactions
        val queryValue: String = "",
        val pointsItemsList: List<PaymentPoint?> = listOf(
            PaymentPoint(
                name = "Farmacias Económicas",
                description = "Col. San Francisco Ave. Las Ama",
                address = null,
                addressDescription = null,
                schedule = null
            ),
            PaymentPoint(
                name = "Farmacias Costosas",
                description = "Col. Bogota, Colombia",
                address = null,
                addressDescription = null,
                schedule = null
            ),
            PaymentPoint(
                name = "Farmacias Regulares",
                description = "Col. Caracas, Venezuela",
                address = null,
                addressDescription = null,
                schedule = null
            )
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCloseScreenClick -> onCloseScreen()
            is OnQueryValueChange -> onQueryValueChange(uiEvent.value)
            is OnGetPaymentPoints -> onGetPaymentPoints()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseScreenClick : UIEvent()
        data class OnQueryValueChange(val value: String) : UIEvent()
        object OnGetPaymentPoints : UIEvent()
        object OnItemPointClick : UIEvent()
    }
}
