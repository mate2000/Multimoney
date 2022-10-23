package com.multimoney.multimoney.presentation.ui.smart.origin.sourceofincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryGeneralEconomicActivityUseCase
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivity
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SourceOfIncomeViewModel @Inject constructor(
    private val queryGeneralEconomicActivityUseCase: QueryGeneralEconomicActivityUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onCallQueryGetSourceOfIncomeUseCase() {
        executeUseCase {
            // TODO, obtain this params dynamically
            queryGeneralEconomicActivityUseCase("rob.mm02@yopmail.com", 7).collectLatest {
                it.onSuccess { result ->
                    uiState = uiState.copy(
                        generalEconomicActivityList = result?.resultList,
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
    }

    data class UIState(
        // Interactions
        val generalEconomicActivityList: List<GeneralEconomicActivity?>? = listOf(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCallQueryGetSourceOfIncome -> onCallQueryGetSourceOfIncomeUseCase()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetSourceOfIncome : UIEvent()
    }
}
