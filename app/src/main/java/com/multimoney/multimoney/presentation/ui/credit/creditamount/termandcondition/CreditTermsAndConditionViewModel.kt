package com.multimoney.multimoney.presentation.ui.credit.creditamount.termandcondition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.TermsAndConditionsUseCase
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@HiltViewModel
class CreditTermsAndConditionViewModel @Inject constructor(
    private val termsAndConditionsUseCase: TermsAndConditionsUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    var uiState by mutableStateOf(UIState())
        private set

    fun fetchTermsAndConditions(systemInDarkTheme: Boolean) {
        executeUseCase {
            val user = dataStorePreferences.getUserEmail().first()
            val idBrand = dataStorePreferences.getIdBrand().first()

            termsAndConditionsUseCase.invoke(
                user = user,
                idBrand = idBrand.toInt(),
                systemInDarkTheme
            ).collectLatest {result ->
                result.onSuccess { htmlResponse ->
                   uiState = uiState.copy(html = htmlResponse)
                }
                result.onFailure {
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    data class UIState(
        // Fields
        val html: String = "",
        var isLoading: Boolean = false
    )

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(isLoading = false)
        openDialog = DialogParameters(
            description = error.getError() ?: "",
            isActive = mutableStateOf(true)
        )
    }
}
