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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnCloseScreenClick
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnDialogPositiveButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnGetPaymentPoints
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnQueryValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
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

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentOptionsScreen.route, isRestart = false)

    private fun onNavigateHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onCloseScreen() {
        uiState = uiState.copy(
            dialogParameters = uiState.dialogParameters.copy(
                isActive = mutableStateOf(true),
                positiveAction = { onUIEvent(OnDialogPositiveButtonClick) }
            )
        )
    }

    private fun onGetPaymentPoints() =
        executeUseCase {
            queryGetPaymentPointsUseCase.invoke(
                idBrand = idBrand ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(
                        pointsItemsList = it ?: listOf(),
                        isLoading = false
                    )
                }.onFailure {
                    uiState = uiState.copy(
                        pointsItemsList = listOf(),
                        isLoading = false
                    )
                }.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }

    private fun onLoadingValueChange(isLoading: Boolean) {
        uiState = uiState.copy(isLoading = isLoading)
    }

    data class UIState(
        // Interactions
        val queryValue: String = "",
        val pointsItemsList: List<PaymentPoint?> = listOf(),
        val dialogParameters: DialogParameters = DialogParameters(
            titleResource = R.string.payment_points_dialog_title,
            descriptionResource = R.string.payment_points_dialog_description,
            isActive = mutableStateOf(false),
            positiveResource = R.string.payment_points_dialog_positive_button,
            negativeResource = R.string.payment_points_dialog_negative_button
        ),
        val isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnDialogPositiveButtonClick -> onNavigateHome()
            is OnCloseScreenClick -> onCloseScreen()
            is OnQueryValueChange -> onQueryValueChange(uiEvent.value)
            is OnGetPaymentPoints -> onGetPaymentPoints()
            is OnLoadingValueChange -> onLoadingValueChange(uiEvent.isLoading)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseScreenClick : UIEvent()
        data class OnQueryValueChange(val value: String) : UIEvent()
        object OnGetPaymentPoints : UIEvent()
        object OnItemPointClick : UIEvent()
        object OnDialogPositiveButtonClick : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
    }
}
