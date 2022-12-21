package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceIncomeViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    /**
     * call this function on any internal screen from the step three in order to return to the
     * desired screen. In this case, the economical activity options one.
     */
    fun goBackToMainOptions() {
        onUIEvent(
            UIEvent.OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            )
        )
    }

    /**
     * get the previous step based on the idBrand, since the total of screens in the
     * stepper is different across CR and SV, it should be obtained depending on the country.
     * Besides, this function will be shared across all the economical options screen.
     * @param idBrand to get the country id and handle the back step accordingly.
     */
    fun getPreviousStep(idBrand: Int): Int {
        return if (idBrand == Brand.ElSalvador.id) SmartSteps.Two.id else SmartSteps.One.id
    }

    /**
     * get the next step based on the idBrand, since the total of screens in the
     * stepper is different across CR and SV, it should be obtained depending on the country.
     * Besides, this function will be shared across all the economical options screen.
     * @param idBrand to get the country id and handle the next step accordingly.
     */
    fun getNextStep(idBrand: Int): Int {
        return if (idBrand == Brand.ElSalvador.id) SmartSteps.Four.id else SmartSteps.Three.id
    }

    data class UIState(
        // Interactions
        val selectedOption: Int = SourceIncomeOptionType.MainSourceIncomeScreenType.id
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateToSelectedSourceOfIncomeOption -> {
                uiState = uiState.copy(selectedOption = uiEvent.selectedOption)
            }
        }
    }

    sealed class UIEvent {
        data class OnNavigateToSelectedSourceOfIncomeOption(val selectedOption: Int) : UIEvent()
    }
}
