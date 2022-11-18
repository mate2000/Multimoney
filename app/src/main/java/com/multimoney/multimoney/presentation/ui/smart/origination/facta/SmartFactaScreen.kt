package com.multimoney.multimoney.presentation.ui.smart.origination.facta

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnClickBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomRadioButtonsLayout

@Composable
fun SmartFactaScreen(
    sharedViewModel: SmartViewModel = hiltViewModel(),
    viewModel: SmartFactaViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid(sharedViewModel.idBrandAsInt)))
        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                isPEP = viewModel.uiState.isPEP,
                                isUSCitizen = viewModel.uiState.isUSCitizen,
                                isActivityOfArt15 = viewModel.uiState.isActivityOfArt15,
                                isUSTaxPayer = viewModel.uiState.isUSTaxPayer,
                                isTaxPayer = viewModel.uiState.isTaxPayer,
                                currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep)
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
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.smart_facta_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )

        when (sharedViewModel.idBrandAsInt) {
            Brand.CostaRica.id -> {
                when (viewModel.uiState.crPage) {
                    CR_PAGE_ONE -> {
                        Column {
                            ContentOneCR(viewModel, sharedViewModel)
                            sharedViewModel.onUIEvent(OnContinueVisible(false))
                        }
                    }
                    CR_PAGE_TWO -> {
                        ContentTwoCR(viewModel)
                        sharedViewModel.onUIEvent(OnContinueVisible(true))
                    }
                }
            }
            Brand.ElSalvador.id -> {
                ContentSV(viewModel)
                sharedViewModel.onUIEvent(OnContinueVisible(true))
            }
        }
    }
}

@Composable
fun ContentSV(
    viewModel: SmartFactaViewModel,
    modifier: Modifier = Modifier
) {
    val optionsCitizen = stringArrayResource(R.array.smart_facta_is_us_citizen_options).toList()
    val optionsPep = stringArrayResource(R.array.smart_facta_is_pep_options).toList()

    Column(modifier) {
        Text(
            text = stringResource(R.string.smart_facta_are_you_us_citizen),
            style = Typography.body1,
            color = WhiteTransparency90,
            modifier = Modifier.padding(top = 24.dp)
        )
        CustomRadioButtonsLayout(
            options = optionsCitizen,
            onOptionSelected = {
                viewModel.onUiEvent(
                    OnIsUSCitizenChange(
                        it == optionsCitizen.first(),
                        Brand.ElSalvador.id
                    )
                )
            }
        )

        Text(
            text = stringResource(R.string.smart_facta_are_you_or_family_pep),
            style = Typography.body1,
            color = WhiteTransparency90,
            modifier = Modifier.padding(top = 32.dp)
        )
        CustomRadioButtonsLayout(
            options = optionsPep,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsPEPChange(it == optionsPep.first(), Brand.ElSalvador.id))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentOneCR(
    viewModel: SmartFactaViewModel,
    sharedViewModel: SmartViewModel,
    modifier: Modifier = Modifier
) {
    val optionsYesNo = stringArrayResource(R.array.options_yes_no).toList()
    val optionsPep = stringArrayResource(R.array.smart_facta_is_pep_options).toList()

    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.smart_facta_activities_according_to_article_15) + " ")
        pushStringAnnotation(INFO_TAG, INFO_TAG)
        withStyle(style = SpanStyle(MultimoneyTheme.colors.textInformation)) {
            append(stringResource(R.string.smart_facta_learn_more))
        }
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = modifier) {
            ClickableText(
                text = annotatedText,
                style = Typography.body1.copy(color = WhiteTransparency90),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = INFO_TAG,
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        sharedViewModel.onUIEvent(OnClickBottomSheet)
                        sharedViewModel.uiState.bottomSheet = {
                            SmartFactaBottomSheet(
                                coroutineScope = rememberCoroutineScope(),
                                modalBottomSheetState = sharedViewModel.uiState.bottomSheetState,
                                viewModel = sharedViewModel
                            )
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            )
            CustomRadioButtonsLayout(
                options = optionsYesNo,
                onOptionSelected = {
                    viewModel.onUiEvent(
                        OnIsActivityOfArt15Change(
                            it == optionsYesNo.first(),
                            Brand.CostaRica.id
                        )
                    )
                }
            )

            Text(
                text = stringResource(R.string.smart_facta_are_you_or_family_pep),
                style = Typography.body1,
                color = WhiteTransparency90,
                modifier = Modifier.padding(top = 32.dp)
            )
            CustomRadioButtonsLayout(
                options = optionsPep,
                onOptionSelected = {
                    viewModel.onUiEvent(OnIsPEPChange(it == optionsPep.first(), Brand.CostaRica.id))
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
    viewModel: SmartFactaViewModel,
    modifier: Modifier = Modifier
) {
    val options = stringArrayResource(R.array.options_yes_no).toList()
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.smart_facta_are_you_us_tax_payer),
            style = Typography.body1,
            color = WhiteTransparency90,
            modifier = Modifier.padding(top = 24.dp)
        )
        CustomRadioButtonsLayout(
            options = options,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsUSTaxPayerChange(it == options.first(), Brand.CostaRica.id))
            }
        )

        Text(
            text = stringResource(R.string.smart_facta_are_you_other_country_tax_payer),
            style = Typography.body1,
            color = WhiteTransparency90,
            modifier = Modifier.padding(top = 32.dp)
        )
        CustomRadioButtonsLayout(
            options = options,
            onOptionSelected = {
                viewModel.onUiEvent(OnIsTaxPayerChange(it == options.first(), Brand.CostaRica.id))
            }
        )
    }
    BackHandler {
        viewModel.onUiEvent(OnCrGoPageOne)
    }
}
const val INFO_TAG = "info"
