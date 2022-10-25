package com.multimoney.multimoney.presentation.ui.payment.points

// import com.multimoney.domain.interaction.credit.MutationProcessPaymentListUseCase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent.OnTextValueChange
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent.OnCloseScreenClick
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent.OnSaveArguments
import javax.inject.Inject

class PaymentPointsViewModel @Inject constructor(
    // private val mutationProcessPaymentUseCase: MutationProcessPaymentListUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int? = null

    private fun onSaveArguments(
        idBrand: Int?
    ) {
        this.idBrand = idBrand
    }

    private fun onItemPointClick() {
        // TODO Navigate to item details
    }

    private fun onAmountValueChange(value: String) {
        uiState = uiState.copy()
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

    private fun onProcessPayment(paymentDescription: String) =
        executeUseCase {
            /*mutationProcessPaymentUseCase.invoke(

            ).collectLatest { result ->
                result.onSuccess {

                }.onMessage {

                }.onFailure {
                    *//*onFailureWithDialog(
                        false,
                        DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )*//*
                }.onLoading {
                    // onLoadingValueChange(true)
                }
            }*/
        }

    data class UIState(
        // Interactions
        val queryText: String = "",
        val pointsItemsList: List<String> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCloseScreenClick -> onCloseScreen()
            is OnTextValueChange -> onAmountValueChange(uiEvent.value)
            is OnSaveArguments -> onSaveArguments(
                idBrand = uiEvent.idBrand
            )
        }
    }

    sealed class UIEvent {
        class OnSaveArguments(
            val idBrand: Int?
        ) : UIEvent()

        object OnNavigateBack : UIEvent()
        object OnCloseScreenClick : UIEvent()
        data class OnTextValueChange(val value: String) : UIEvent()
        object OnItemPointClick : UIEvent()
    }
}
