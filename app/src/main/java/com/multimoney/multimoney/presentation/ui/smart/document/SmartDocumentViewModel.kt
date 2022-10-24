package com.multimoney.multimoney.presentation.ui.smart.document

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCase
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCivilStateChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnGenderChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnIssueDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartDocumentViewModel @Inject constructor(
    private val queryCivilStatusUseCase: QueryCivilStatusUseCase,
    private val queryProfessionUseCase: QueryProfessionUseCase,
    private val queryNationalitiesUseCase: QueryNationalitiesUseCase,
    private val queryAddressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun callQueryNationalitiesUseCase() = executeUseCase {
        queryNationalitiesUseCase.invoke(
            user = "401920903",
            idBrand = 5
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun callQueryAddressLevelTwoUseCase() = executeUseCase {
        queryAddressLevelTwoUseCase.invoke(
            user = "401920903",
            idBrand = 5,
            idAddressLevelOne = "1"
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun callQueryCivilStatusUseCase() = executeUseCase {
        queryCivilStatusUseCase.invoke(
            user = "401920903",
            idBrand = 5
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun callQueryProfesionUseCase() = executeUseCase {
        queryProfessionUseCase.invoke(
            user = "40192",
            idBrand = 5
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    data class UIState(
        // Fields
        val issueDate: String = "",
        val expirationDate: String = "",
        val issuePlace: String = "",
        val gender: String = "",
        val civilState: String = "",
        val profession: String = "",
        val openDialog: DialogParameters = DialogParameters(),
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGenderChange -> uiState = uiState.copy(gender = event.gender)
            is OnCivilStateChange -> uiState = uiState.copy(civilState = event.civilState)
            is OnProfessionChange -> uiState = uiState.copy(profession = event.profession)
            is OnExpirationDateValueChange -> TODO()
            is OnFailureWithDialog -> TODO()
            is OnIssueDateValueChange -> TODO()
            is UIEvent.OnLoadingValueChange -> TODO()
            is OnNextActionClick -> TODO()
            is OnStart -> TODO()
            OnValidateForm -> TODO()
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val userCompletedDialogDescription: String,
            val linkWhatsapp: String,
            val blockedMessage: String,
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnIssueDateValueChange(val date: String) : UIEvent()
        data class OnExpirationDateValueChange(val date: String) : UIEvent()
        data class OnGenderChange(val gender: String) : UIEvent()
        data class OnCivilStateChange(val civilState: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val BIRTH_DATE_MIN_YEAR = 1972
        const val BIRTH_DATE_MIN_MONTH = 0
        const val BIRTH_DATE_MIN_DAY = 1
    }
}