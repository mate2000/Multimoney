package com.multimoney.multimoney.presentation.ui.smart.origination.facta

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnClickBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.FactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomRadioButtonsLayout

@Composable
fun FactaScreen(
    sharedViewModel: SmartViewModel = hiltViewModel(),
    viewModel: FactaViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid(sharedViewModel.idBrandAsInt)))
        if (sharedViewModel.idBrandAsInt == ID_BRAND_CR) viewModel.onUiEvent(OnCrGoPageOne)

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                isPEP = viewModel.uiState.isPEP,
                                isUSCitizen = viewModel.uiState.isPEP,
                                isActivityOfArt15 = viewModel.uiState.isPEP,
                                isUSTaxPayer = viewModel.uiState.isPEP,
                                isTaxPayer = viewModel.uiState.isPEP
                            )
                        )
                    )
                },
                nextStep = SmartSteps.Six.id,
                previousStep = SmartSteps.Four.id
            )
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    when (sharedViewModel.idBrandAsInt) {
        ID_BRAND_CR -> {
            when (viewModel.uiState.crPage) {
                CR_PAGE_ONE -> {
                    Column {
                        ContentOneCR(viewModel, sharedViewModel, Modifier.padding(16.dp))
                        sharedViewModel.onUIEvent(OnContinueVisible(false))
                    }
                }
                CR_PAGE_TWO -> {
                    ContentTwoCR(viewModel, Modifier.padding(16.dp))
                    sharedViewModel.onUIEvent(OnContinueVisible(true))
                    BackHandler {
                        viewModel.onUiEvent(OnCrGoPageOne)
                    }
                }
            }
        }
        ID_BRAND_SV -> {
            ContentSV(viewModel, Modifier.padding(16.dp))
            sharedViewModel.onUIEvent(OnContinueVisible(true))
        }
    }
}

@Composable
fun ContentSV(
    viewModel: FactaViewModel,
    modifier: Modifier = Modifier
) {
    val optionsCitizen = stringArrayResource(R.array.facta_is_us_citizen_options).toList()
    val optionsPep = stringArrayResource(R.array.facta_is_pep_options).toList()

    Column(modifier) {
        Text(
            text = stringResource(R.string.facta_are_you_us_citizen),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp,
                color = WhiteTransparency90
            )
        )
        CustomRadioButtonsLayout(
            options = optionsCitizen,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsUSCitizenChange(it == optionsCitizen[0], ID_BRAND_SV))
            }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Text(
            text = stringResource(R.string.facta_are_you_or_family_pep),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp,
                color = WhiteTransparency90
            )
        )
        CustomRadioButtonsLayout(
            options = optionsPep,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsPEPChange(it == optionsPep[0], ID_BRAND_SV))
            }
        )
    }
}

@Composable
fun ContentOneCR(
    viewModel: FactaViewModel,
    sharedViewModel: SmartViewModel,
    modifier: Modifier = Modifier
) {
    val options = stringArrayResource(R.array.yes_no).toList()
    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.facta_activities_according_to_article_15) + " ")
        pushStringAnnotation(INFO_TAG, INFO_TAG)
        withStyle(style = SpanStyle(Color.Blue)) {
            append(stringResource(R.string.facta_learn_more))
        }
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = modifier) {
            ClickableText(
                text = annotatedText,
                style = Typography.body1.copy(
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = WhiteTransparency90
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = INFO_TAG,
                        start = offset,
                        end = offset
                    )[0].let {
                        sharedViewModel.onUIEvent(OnClickBottomSheet)
                    }
                }
            )

            CustomRadioButtonsLayout(
                options = options,
                onOptionSelected = {
                    viewModel.onUiEvent(OnIsActivityOfArt15Change(it == options[0], ID_BRAND_CR))
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Text(
                text = stringResource(R.string.facta_are_you_or_family_pep),
                style = Typography.body1.copy(
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = WhiteTransparency90
                )
            )
            CustomRadioButtonsLayout(
                options = options,
                onOptionSelected = {
                    viewModel.onUiEvent(OnIsPEPChange(it == options[0], ID_BRAND_CR))
                }
            )
        }
        CustomButton(
            onClick = { viewModel.onUiEvent(OnCrGoPageTwo) },
            text = stringResource(id = R.string.button_continue),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = PrimaryPrimary,
            enable = viewModel.uiState.isActivityOfArt15 != null && viewModel.uiState.isPEP != null
        )
    }
}

@Composable
fun ContentTwoCR(
    viewModel: FactaViewModel,
    modifier: Modifier = Modifier
) {
    val options = stringArrayResource(R.array.yes_no).toList()
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.facta_are_you_us_tax_payer),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp,
                color = WhiteTransparency90
            )
        )
        CustomRadioButtonsLayout(
            options = options,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsUSTaxPayerChange(it == options[0], ID_BRAND_CR))
            }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Text(
            text = stringResource(R.string.facta_are_you_other_country_tax_payer),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp,
                color = WhiteTransparency90
            )
        )
        CustomRadioButtonsLayout(
            options = options,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsTaxPayerChange(it == options[0], ID_BRAND_CR))
            }
        )
    }
}

const val ID_BRAND_CR = 5
const val ID_BRAND_SV = 7
const val INFO_TAG = "info"
