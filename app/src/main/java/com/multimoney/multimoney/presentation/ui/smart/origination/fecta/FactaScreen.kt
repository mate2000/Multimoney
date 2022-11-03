package com.multimoney.multimoney.presentation.ui.smart.origination.fecta

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnClickInfo
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOnlyRadioButtons

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
                    ContentOneCR(viewModel, Modifier.padding(16.dp))
                    sharedViewModel.onUIEvent(OnContinueVisible(false))
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
    ShowCustomDialog(viewModel.uiState)
}

@Composable
fun ContentSV(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.facta_screen_are_you_us_citizen),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isUSCitizen,
            optionsOne = Triple(
                stringResource(R.string.facta_screen_yes_i_am),
                true
            ) { viewModel.onUiEvent(OnIsUSCitizenChange(true, ID_BRAND_SV)) },
            optionsTwo = Triple(
                stringResource(R.string.facta_screen_no_i_am_not),
                false
            ) { viewModel.onUiEvent(OnIsUSCitizenChange(false, ID_BRAND_SV)) }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Text(
            text = stringResource(R.string.facta_screen_are_you_or_family_pep),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isPEP,
            optionsOne = Triple(
                stringResource(R.string.facta_screen_yes_they_qualify),
                true
            ) { viewModel.onUiEvent(OnIsPEPChange(true, ID_BRAND_SV)) },
            optionsTwo = Triple(
                stringResource(R.string.facta_screen_no_they_do_not_qualify),
                false
            ) { viewModel.onUiEvent(OnIsPEPChange(false, ID_BRAND_SV)) }
        )
    }
}

@Composable
fun ContentOneCR(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.facta_screen_activities_according_to_article_15) + " ")
        pushStringAnnotation(INFO_TAG, INFO_TAG)
        withStyle(style = SpanStyle(Color.Blue)) {
            append(stringResource(R.string.facta_screen_learn_more))
        }
        pop()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                        viewModel.onUiEvent(OnClickInfo)
                    }
                }
            )
        }
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isActivityOfArt15,
            optionsOne = Triple(
                stringResource(R.string.yes),
                true
            ) { viewModel.onUiEvent(OnIsActivityOfArt15Change(true, ID_BRAND_CR)) },
            optionsTwo = Triple(
                stringResource(R.string.no),
                false
            ) { viewModel.onUiEvent(OnIsActivityOfArt15Change(false, ID_BRAND_CR)) }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Text(
            text = stringResource(R.string.facta_screen_are_you_or_family_pep),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isPEP,
            optionsOne = Triple(
                stringResource(R.string.yes),
                true
            ) { viewModel.onUiEvent(OnIsPEPChange(true, ID_BRAND_CR)) },
            optionsTwo = Triple(
                stringResource(R.string.no),
                false
            ) { viewModel.onUiEvent(OnIsPEPChange(false, ID_BRAND_CR)) }
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

@Composable
fun ContentTwoCR(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.facta_screen_are_you_us_tax_payer),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isUSTaxPayer,
            optionsOne = Triple(
                stringResource(R.string.yes),
                true
            ) { viewModel.onUiEvent(OnIsUSTaxPayerChange(true, ID_BRAND_CR)) },
            optionsTwo = Triple(
                stringResource(R.string.no),
                false
            ) { viewModel.onUiEvent(OnIsUSTaxPayerChange(false, ID_BRAND_CR)) }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Text(
            text = stringResource(R.string.facta_screen_are_you_other_country_tax_payer),
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomOnlyRadioButtons(
            condition = viewModel.uiState.isTaxPayer,
            optionsOne = Triple(
                stringResource(R.string.yes),
                true
            ) { viewModel.onUiEvent(OnIsTaxPayerChange(true, ID_BRAND_CR)) },
            optionsTwo = Triple(
                stringResource(R.string.no),
                false
            ) { viewModel.onUiEvent(OnIsTaxPayerChange(false, ID_BRAND_CR)) }
        )
    }
}

@Composable
fun ShowCustomDialog(uiState: UIState) {
    if (uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(uiState.openDialog.titleResource),
            message = stringResource(uiState.openDialog.descriptionResource),
            topIcon = R.drawable.info_blue_icon,
            positiveButtonText = stringResource(uiState.openDialog.positiveResource),
            openDialogCustom = uiState.openDialog.isActive,
            onPositiveAction = uiState.openDialog.positiveAction
        )
    }
}

const val ID_BRAND_CR = 5
const val ID_BRAND_SV = 7
const val INFO_TAG = "info"
