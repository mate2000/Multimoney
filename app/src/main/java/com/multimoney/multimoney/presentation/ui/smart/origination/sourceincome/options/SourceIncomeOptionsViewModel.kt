package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.accountsmart.QueryGeneralEconomicActivityUseCase
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivity
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.SourceIncomeOptionsViewModel.UIEvent.OnCallQueryGetSourceOfIncome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SourceIncomeOptionsViewModel @Inject constructor(
    private val queryGeneralEconomicActivityUseCase: QueryGeneralEconomicActivityUseCase,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onCallQueryGetSourceOfIncomeUseCase() = executeUseCase {
        val user = dataStorePreferences.getUserEmail().first()
        val brandId = dataStorePreferences.getIdBrand().first()

        queryGeneralEconomicActivityUseCase(user, brandId.toInt()).collectLatest {
            it.onSuccess { result ->
                uiState = uiState.copy(
                    generalEconomicActivityList = result?.resultList?.sortedBy { item -> item?.iconCode },
                    isLoading = false
                )
            }.onFailure { error ->
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = error.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    data class UIState(
        // Interactions
        val generalEconomicActivityList: List<GeneralEconomicActivity?>? = listOf(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCallQueryGetSourceOfIncome -> onCallQueryGetSourceOfIncomeUseCase()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetSourceOfIncome : UIEvent()
    }
}
