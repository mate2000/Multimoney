package com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnHideBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnShowBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnInitData
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnQuestionFourValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnQuestionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnQuestionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation.AdditionalInformationViewModel.UIEvent.OnQuestionTwoValueChange
import com.multimoney.multimoney.presentation.uielement.RadioButtonQuestion
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType

@Composable
fun AdditionalInformationScreen(
    sharedViewModel: CreditViewModel,
    viewModel: AdditionalInformationViewModel = hiltViewModel()
) {
    val questionTopAnswer = stringResource(id = R.string.credit_additional_information_button_yes)
    val questionBottomAnswer = stringResource(id = R.string.credit_additional_information_button_no)
    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(OnContinueEnable(true))
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        OnNextActionClick(
                            user = sharedViewModel.email,
                            nextStepAction = {
                                sharedViewModel.onUIEvent(OnHideBottomSheet)
                                sharedViewModel.onUIEvent(OnCallMutationSaveCreditFlowStep)
                            },
                            saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                        )
                    )
                    sharedViewModel.logEvents(AdjustEventType.ORIGINATION_FIRST_FILL_PEP_5009)
                },
                nextStep = CreditStep.Eight.id,
                previousStep = CreditStep.Six.id
            )
        )
        viewModel.onUIEvent(
            OnInitData(
                idBrand = sharedViewModel.idBrand.toInt(),
                questionTopAnswer = questionTopAnswer,
                questionBottomAnswer = questionBottomAnswer
            )
        )
        viewModel.onUIEvent(OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.credit_additional_information_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText
        )
        Spacer(Modifier.height(24.dp))
        when (sharedViewModel.idBrand.toInt()) {
            Brand.CostaRica.id -> {
                GetCRQuestions(viewModel, sharedViewModel)
            }
            else -> {
                GetSVQuestions(viewModel)
            }
        }
    }
}

@Composable
fun GetCRQuestions(viewModel: AdditionalInformationViewModel, sharedViewModel: CreditViewModel) {
    RadioButtonQuestion(
        questionTextResource = R.string.credit_additional_information_CR_q1,
        firstButtonTextResource = R.string.credit_additional_information_button_yes,
        secondButtonTextResource = R.string.credit_additional_information_button_no,
        shouldHaveDisclaimer = true,
        firstButtonIsSelected = viewModel.uiState.questionOneValue,
        secondButtonIsSelected = viewModel.uiState.questionOneValue.not(),
        disclaimerTextResource = R.string.credit_additional_information_CR_q1_disclaimer,
        onDisclaimerClick = { sharedViewModel.onUIEvent(OnShowBottomSheet) },
        onFirstButtonOnClick = { viewModel.onUIEvent(OnQuestionOneValueChange(true)) },
        onSecondButtonOnClick = { viewModel.onUIEvent(OnQuestionOneValueChange(false)) }
    )
    RadioButtonQuestion(
        questionTextResource = R.string.credit_additional_information_CR_q2,
        firstButtonTextResource = R.string.credit_additional_information_button_yes,
        secondButtonTextResource = R.string.credit_additional_information_button_no,
        shouldHaveDisclaimer = false,
        firstButtonIsSelected = viewModel.uiState.questionTwoValue,
        secondButtonIsSelected = viewModel.uiState.questionTwoValue.not(),
        onFirstButtonOnClick = { viewModel.onUIEvent(OnQuestionTwoValueChange(true)) },
        onSecondButtonOnClick = { viewModel.onUIEvent(OnQuestionTwoValueChange(false)) }
    )
    RadioButtonQuestion(
        questionTextResource = R.string.credit_additional_information_CR_q3,
        firstButtonTextResource = R.string.credit_additional_information_button_yes,
        secondButtonTextResource = R.string.credit_additional_information_button_no,
        shouldHaveDisclaimer = false,
        firstButtonIsSelected = viewModel.uiState.questionThreeValue,
        secondButtonIsSelected = viewModel.uiState.questionThreeValue.not(),
        onFirstButtonOnClick = { viewModel.onUIEvent(OnQuestionThreeValueChange(true)) },
        onSecondButtonOnClick = { viewModel.onUIEvent(OnQuestionThreeValueChange(false)) }
    )
    RadioButtonQuestion(
        questionTextResource = R.string.credit_additional_information_CR_q4,
        firstButtonTextResource = R.string.credit_additional_information_button_yes,
        secondButtonTextResource = R.string.credit_additional_information_button_no,
        shouldHaveDisclaimer = false,
        firstButtonIsSelected = viewModel.uiState.questionFourValue,
        secondButtonIsSelected = viewModel.uiState.questionFourValue.not(),
        onFirstButtonOnClick = { viewModel.onUIEvent(OnQuestionFourValueChange(true)) },
        onSecondButtonOnClick = { viewModel.onUIEvent(OnQuestionFourValueChange(false)) }
    )
}

@Composable
fun GetSVQuestions(viewModel: AdditionalInformationViewModel) {
    RadioButtonQuestion(
        questionTextResource = R.string.credit_additional_information_SV_q1,
        firstButtonTextResource = R.string.credit_additional_information_button_yes,
        secondButtonTextResource = R.string.credit_additional_information_button_no,
        shouldHaveDisclaimer = false,
        firstButtonIsSelected = viewModel.uiState.questionOneValue,
        secondButtonIsSelected = viewModel.uiState.questionOneValue.not(),
        onFirstButtonOnClick = { viewModel.onUIEvent(OnQuestionOneValueChange(true)) },
        onSecondButtonOnClick = { viewModel.onUIEvent(OnQuestionOneValueChange(false)) }
    )
}
