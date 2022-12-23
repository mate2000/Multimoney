package com.multimoney.multimoney.presentation.ui.credit.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCase
import com.multimoney.domain.interaction.credit.QueryGetPagedCreditMovementsUseCase
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.BaseEvent.OnStartDownloadCreditMovementsWorker
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnDownloadMovements
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnErrorLoading
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnIsLoadingChange
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.ui.credit.movements.workmanager.DownloadCreditMovementsWorker
import com.multimoney.multimoney.presentation.util.OPTION_BTN_6
import com.multimoney.multimoney.presentation.util.PAGE_SIZE
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class CreditMovementsViewModel @Inject constructor(
    private val queryGetPagedCreditMovements: QueryGetPagedCreditMovementsUseCase,
    val queryAccountStatementUseCase: QueryAccountStatementUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Bundle parameters
    val idBrand = savedStateHandle[ID_BRAND] ?: 0
    val idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
    val creditNumber = savedStateHandle[CREDIT_NUMBER] ?: ""
    val user = savedStateHandle[USER] ?: ""

    private fun onGetSmartMovements() {
        executeUseCase {
            uiState = uiState.copy(
                movementsPage = queryGetPagedCreditMovements.invoke(
                    idBrand = idBrand,
                    idLoanClient = idLoanClient,
                    pageSize = PAGE_SIZE,
                    option = OPTION_BTN_6
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

    private fun onDownloadMovements() {
        val myData: Data = workDataOf(
            CREDIT_NUMBER to creditNumber,
            USER to user,
            ID_BRAND to idBrand
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<DownloadCreditMovementsWorker>()
            .setConstraints(constraints)
            .setInputData(myData)
            .build()

        WorkManagerHelper.initDownloadPDFWorker(preferences, queryAccountStatementUseCase)

        emitBaseEvent(OnStartDownloadCreditMovementsWorker(oneTimeRequest))
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGetMovement -> onGetSmartMovements()
            is OnNavigateBackToHome -> navigateBackToHome()
            is OnErrorLoading -> onErrorLoading(event.failureDialog)
            is OnIsLoadingChange -> onIsLoadingChange(event.isLoading)
            is OnDownloadMovements -> onDownloadMovements()
        }
    }

    sealed class UIEvent {
        object OnDownloadMovements : UIEvent()
        object OnGetMovement : UIEvent()
        object OnNavigateBackToHome : UIEvent()
        data class OnErrorLoading(val failureDialog: DialogParameters) : UIEvent()
        data class OnIsLoadingChange(val isLoading: Boolean) : UIEvent()
    }

    data class UIState(
        // Fields
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val movementsPage: Flow<PagingData<CreditMovement>> = flowOf()
    )

    sealed class BaseEvent {
        data class OnStartDownloadCreditMovementsWorker(val oneTimeRequest: OneTimeWorkRequest) : BaseEvent()
    }
}

class WorkManagerHelper {
    companion object {
        lateinit var preferences: DataStorePreferences
        lateinit var queryAccountStatementUseCase: QueryAccountStatementUseCase
        fun initDownloadPDFWorker(
            preferences: DataStorePreferences,
            queryAccountStatementUseCase: QueryAccountStatementUseCase
        ) {
            this.preferences = preferences
            this.queryAccountStatementUseCase = queryAccountStatementUseCase
        }
    }
}
