package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryGetCoreBankMovementsUseCase
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartMovementsViewModel @Inject constructor(
    private val queryGetCoreBankMovements: QueryGetCoreBankMovementsUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onGetSmartMovements(
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        executeUseCase {
            queryGetCoreBankMovements.invoke(
                user = "401920903",
                idBrand = 5,
                identificationNumber = "107910975",
                accountToken = 287380645,
                pageNumber = pageNumber,
                pageSize = pageSize,
                monthDate = null
            ).collectLatest { result ->
                result.onSuccess { movements ->
                    uiState = uiState.copy(
                        smartMovementsList = uiState.smartMovementsList.plus(movements?.result ?: emptyList()),
                        moreRecordsAvailable = ((movements?.totalRecords ?: 0) > uiState.currentPage * PAGE_SIZE),
                        currentPage = uiState.currentPage + 1
                    )
                    delay(100)
                    uiState = uiState.copy(isLoading = false)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                result.onFailure { error ->
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = error.getError() ?: "",
                            isActive = mutableStateOf(true)
                        ),
                        isLoading = false
                    )
                }
            }
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
            is OnGetMovement -> onGetSmartMovements(event.pageNumber, event.pageSize)
            is OnNavigateBackToHome -> navigateBackToHome()
        }
    }

    sealed class UIEvent {
        data class OnGetMovement(val pageNumber: Int, val pageSize: Int) : UIEvent()
        object OnNavigateBackToHome : UIEvent()
    }

    data class UIState(
        // Fields
        var idBrand: String = "0",
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val smartMovementsList: List<SmartMovement> = emptyList(),
        val currentPage: Int = 1,
        val moreRecordsAvailable: Boolean = true
    )
}
