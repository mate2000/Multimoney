package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.multimoney.domain.interaction.accountsmart.QueryGetPagedSmartMovementsUseCase
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnErrorLoading
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnIsLoadingChange
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.util.PAGE_SIZE
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class SmartMovementsViewModel @Inject constructor(
    private val queryGetPagedSmartMovements: QueryGetPagedSmartMovementsUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Bundle parameters
    val idBrand = savedStateHandle[ID_BRAND] ?: 0
    private val user = savedStateHandle[USER] ?: ""
    private val identification = savedStateHandle[IDENTIFICATION] ?: ""
    private val accountToken = savedStateHandle[ACCOUNT_TOKEN] ?: ""

    private fun onGetSmartMovements() {
        executeUseCase {
            uiState = uiState.copy(
                movementsPage = queryGetPagedSmartMovements.invoke(
                    user = user,
                    idBrand = idBrand,
                    identificationNumber = identification,
                    accountToken = accountToken.toLongOrNull() ?: 0,
                    pageSize = PAGE_SIZE,
                    monthDate = null
                ).cachedIn(viewModelScope)
            )
        }
    }

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartMovementsScreen.route
        )
    }

    private fun onErrorLoading(failureDialog: DialogParameters) {
        uiState = uiState.copy(openDialog = failureDialog)
    }

    private fun onIsLoadingChange(isLoading: Boolean) {
        uiState = uiState.copy(isLoading = isLoading)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGetMovement -> onGetSmartMovements()
            is OnNavigateBackToHome -> navigateBackToHome()
            is OnErrorLoading -> onErrorLoading(event.failureDialog)
            is OnIsLoadingChange -> onIsLoadingChange(event.isLoading)
        }
    }

    sealed class UIEvent {
        object OnGetMovement : UIEvent()
        object OnNavigateBackToHome : UIEvent()
        data class OnErrorLoading(val failureDialog: DialogParameters) : UIEvent()
        data class OnIsLoadingChange(val isLoading: Boolean) : UIEvent()
    }

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val movementsPage: Flow<PagingData<SmartMovement>> = flowOf()
    )
}
