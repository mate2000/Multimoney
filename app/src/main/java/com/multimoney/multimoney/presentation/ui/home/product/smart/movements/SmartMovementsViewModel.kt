package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.multimoney.domain.interaction.accountsmart.QueryGetPagedSmartMovementsUseCase
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class SmartMovementsViewModel @Inject constructor(
    private val queryGetPagedSmartMovements: QueryGetPagedSmartMovementsUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onGetSmartMovements() {
        executeUseCase {
            uiState = uiState.copy(
                movementsPage = queryGetPagedSmartMovements.invoke(
                    user = "401920903",
                    idBrand = 5,
                    identificationNumber = "107910975",
                    accountToken = 1,
                    monthDate = null
                )
            )
        }
    }

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGetMovement -> onGetSmartMovements()
            is OnNavigateBackToHome -> navigateBackToHome()
        }
    }

    sealed class UIEvent {
        object OnGetMovement : UIEvent()
        object OnNavigateBackToHome : UIEvent()
    }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val currentPage: Int = 1,
        val moreRecordsAvailable: Boolean = true,
        val movementsPage: Flow<PagingData<SmartMovement>> = flowOf()
    )
}
