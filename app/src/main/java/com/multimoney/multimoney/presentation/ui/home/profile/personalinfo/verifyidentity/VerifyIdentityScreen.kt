package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.RadioButtonQuestion
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Preview
@Composable
fun VerifyIdentityScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: VerifyIdentityViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    BackHandler {
        viewModel.onUIEvent(VerifyIdentityViewModel.UIEvent.OnNavigateBack)
    }
    VerifyIdentityContent(viewModel = viewModel)
}

@Composable
fun VerifyIdentityContent(viewModel: VerifyIdentityViewModel) {
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, methodSelectionColumn, continueButton) = createRefs()

        TopNavBar(
            modifier = Modifier.constrainAs(topNavBar) {
                top.linkTo(parent.top)
            },
            onLeftButtonClick = {
                viewModel.onUIEvent(VerifyIdentityViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .constrainAs(methodSelectionColumn) {
                    top.linkTo(topNavBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_identity_verification),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = viewModel.uiState.titleResource),
                style = Typography.body2.copy(fontWeight = FontWeight.Light),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            when (viewModel.uiState.changingField) {
                FieldToChange.PHONE.value -> {
                    RadioButtonQuestion(
                        questionTextResource = R.string.empty,
                        firstButtonTextResource = R.string.profile_sms_to_previous_phone,
                        secondButtonTextResource = R.string.profile_to_email,
                        shouldHaveDisclaimer = false,
                        firstButtonIsSelected = viewModel.uiState.questionOneValue,
                        secondButtonIsSelected = viewModel.uiState.questionTwoValue,
                        onFirstButtonOnClick = {
                            viewModel.onUIEvent(
                                VerifyIdentityViewModel.UIEvent.OnQuestionOneSelected(true)
                            )
                        },
                        onSecondButtonOnClick = {
                            viewModel.onUIEvent(
                                VerifyIdentityViewModel.UIEvent.OnQuestionTwoSelected(true)
                            )
                        }
                    )
                }
                FieldToChange.EMAIL.value -> {
                    RadioButtonQuestion(
                        questionTextResource = R.string.empty,
                        firstButtonTextResource = R.string.profile_otp_to_previous_email,
                        secondButtonTextResource = R.string.profile_otp_to_sms,
                        shouldHaveDisclaimer = false,
                        firstButtonIsSelected = viewModel.uiState.questionOneValue,
                        secondButtonIsSelected = viewModel.uiState.questionTwoValue,
                        onFirstButtonOnClick = {
                            viewModel.onUIEvent(
                                VerifyIdentityViewModel.UIEvent.OnQuestionOneSelected(true)
                            )
                        },
                        onSecondButtonOnClick = {
                            viewModel.onUIEvent(
                                VerifyIdentityViewModel.UIEvent.OnQuestionTwoSelected(true)
                            )
                        }
                    )
                }
            }
        }
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .constrainAs(continueButton) {
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                },
            buttonType = CustomButtonType.PrimaryPrimary,
            text = stringResource(id = R.string.profile_verify_code),
            enable = viewModel.uiState.isButtonEnabled,
            onClick = {
                viewModel.onUIEvent(VerifyIdentityViewModel.UIEvent.OnContinueButtonClicked)
            }
        )
    }
}
