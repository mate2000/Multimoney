package com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdditionalInformationViewModel @Inject constructor() : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless=
    var questionTopAnswer = ""
    var questionBottomAnswer = ""
    var idBrand = Brand.ElSalvador.id

    private fun onQuestionOneValueChange(value: Boolean) {
        if (value != uiState.questionOneValue) {
            uiState = uiState.copy(questionOneValue = value)
        }
    }

    private fun onQuestionTwoValueChange(value: Boolean) {
        if (value != uiState.questionTwoValue) {
            uiState = uiState.copy(questionTwoValue = value)
        }
    }

    private fun onQuestionThreeValueChange(value: Boolean) {
        if (value != uiState.questionThreeValue) {
            uiState = uiState.copy(questionThreeValue = value)
        }
    }

    private fun onQuestionFourValueChange(value: Boolean) {
        if (value != uiState.questionFourValue) {
            uiState = uiState.copy(questionFourValue = value)
        }
    }

    private fun onInitData(idBrand: Int, questionTopAnswer: String, questionBottomAnswer: String) {
        this.idBrand = idBrand
        this.questionTopAnswer = questionTopAnswer
        this.questionBottomAnswer = questionBottomAnswer
    }

    private fun onNextActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        when (idBrand) {
            Brand.CostaRica.id -> {
                saveCreditStepsHelper.saveStepSixCR(
                    user,
                    uiState.questionOneValue.toString(),
                    uiState.questionTwoValue.toString(),
                    uiState.questionThreeValue.toString(),
                    uiState.questionFourValue.toString()
                )
            }
            else -> {
                saveCreditStepsHelper.saveStepSix(
                    user,
                    uiState.questionOneValue.toString()
                )
            }
        }
        nextStepAction()
    }

    private fun onLoadStepsInfo(list: List<CreditCatalog?>?) {
        val article15 = list?.find { it?.description == SaveCreditStepsHelper.ARTICLE_15 }
        val pep = list?.find { it?.description == SaveCreditStepsHelper.POLITICALLY_EXPOSED_PERSON }
        val taxPayerUSA = list?.find { it?.description == SaveCreditStepsHelper.TAX_PAYER_USA }
        val taxPayerExternal = list?.find { it?.description == SaveCreditStepsHelper.TAX_PAYER_EXTERNAL }
        uiState = if (idBrand == Brand.CostaRica.id) {
            uiState.copy(
                questionOneValue = article15?.value.toBoolean(),
                questionTwoValue = pep?.value.toBoolean(),
                questionThreeValue = taxPayerUSA?.value.toBoolean(),
                questionFourValue = taxPayerExternal?.value.toBoolean()
            )
        } else {
            uiState.copy(
                questionOneValue = pep?.value.toBoolean()
            )
        }
    }

    data class UIState(
        val questionOneValue: Boolean = false,
        val questionTwoValue: Boolean = false,
        val questionThreeValue: Boolean = false,
        val questionFourValue: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is UIEvent.OnQuestionOneValueChange -> onQuestionOneValueChange(uiEvent.value)
            is UIEvent.OnQuestionTwoValueChange -> onQuestionTwoValueChange(uiEvent.value)
            is UIEvent.OnQuestionThreeValueChange -> onQuestionThreeValueChange(uiEvent.value)
            is UIEvent.OnQuestionFourValueChange -> onQuestionFourValueChange(uiEvent.value)
            is UIEvent.OnInitData -> onInitData(uiEvent.idBrand, uiEvent.questionTopAnswer, uiEvent.questionBottomAnswer)
            is UIEvent.OnLoadCreditSteps -> onLoadStepsInfo(uiEvent.list)
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnInitData(
            val idBrand: Int,
            val questionTopAnswer: String,
            val questionBottomAnswer: String
        ) : UIEvent()

        data class OnQuestionOneValueChange(val value: Boolean) : UIEvent()
        data class OnQuestionTwoValueChange(val value: Boolean) : UIEvent()
        data class OnQuestionThreeValueChange(val value: Boolean) : UIEvent()
        data class OnQuestionFourValueChange(val value: Boolean) : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
    }
}
