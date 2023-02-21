package com.multimoney.multimoney.presentation.ui.visa.verification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.TextWithIcon
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun VisaVerifyInformationScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: VisaVerifyInformationViewModel = hiltViewModel()
) {
    // Navigation
    viewModel.apply {
        LaunchedEffect(true) {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            viewModel.onUIEvent(VisaVerifyInformationViewModel.UIEvent.OnCallMutationCreateCardVDUseCase)
        }
    }
    VisaVerifiedContent(
        onNextButtonClick = { viewModel.onUIEvent(VisaVerifyInformationViewModel.UIEvent.OnNavigateToNextScreen) },
        onBackClick = { viewModel.onUIEvent(VisaVerifyInformationViewModel.UIEvent.OnNavigateBack) },
        onCloseClick = { viewModel.onUIEvent(VisaVerifyInformationViewModel.UIEvent.OnCloseClick) },
        dialogParameter = viewModel.uiState.openDialog,
        isLoading = viewModel.uiState.isLoading
    )

    BackHandler {
        viewModel.onUIEvent(VisaVerifyInformationViewModel.UIEvent.OnNavigateBack)
    }
}

@Composable
@Preview
fun VisaVerifiedContent(
    onNextButtonClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    dialogParameter: DialogParameters = DialogParameters(),
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { onBackClick() },
                onRightButtonClick = { onCloseClick() }
            )
            Text(
                modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.visa_verified_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(36.dp))
            TextWithIcon(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                annotatedString = buildAnnotatedString {
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_1_bold))
                    }
                    append(" ")
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle()
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_1))
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextWithIcon(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                annotatedString = buildAnnotatedString {
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_2_bold))
                    }
                    append(" ")
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle()
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_2))
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextWithIcon(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                annotatedString = buildAnnotatedString {
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_3_bold))
                    }
                    append(" ")
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle()
                    ) {
                        append(stringResource(id = R.string.visa_verified_description_3))
                    }
                }
            )
        }
        CustomButton(
            enable = isLoading.not(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                .height(48.dp),
            onClick = {
                onNextButtonClick()
            },
            text = stringResource(id = R.string.button_continue),
            buttonType = CustomButtonType.PrimaryPrimary
        )
    }
    if (dialogParameter.isActive.value) {
        CustomDialog(
            title = stringResource(id = dialogParameter.titleResource),
            message = stringResource(id = dialogParameter.descriptionResource),
            positiveButtonText = stringResource(id = dialogParameter.positiveResource),
            negativeButtonText = stringResource(id = dialogParameter.negativeResource),
            openDialogCustom = dialogParameter.isActive,
            onPositiveAction = dialogParameter.positiveAction
        )
    }
}
