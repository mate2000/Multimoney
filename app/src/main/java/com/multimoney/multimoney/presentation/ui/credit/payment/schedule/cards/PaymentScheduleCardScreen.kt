package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnEditCardVisaDirect
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnGetClientCardVisaDirect
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentScheduleCardScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentScheduleCardViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            onUIEvent(OnGetClientCardVisaDirect)
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }

    PaymentScheduleContent(viewModel)
}

@Composable
@Preview
fun PaymentScheduleContent(
    viewModel: PaymentScheduleCardViewModel = hiltViewModel()
) {
    if (viewModel.uiState.isAlertResultVisible) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = if (isAlertResultSuccess) {
                    string.empty
                } else {
                    alertResultDescriptionResource
                },
                descriptionString = if (isAlertResultSuccess) {
                    stringResource(id = alertResultDescriptionResource, day)
                } else {
                    alertResultDescription
                },
                buttonTextResource = alertResultButtonResource,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnAlertCloseClick) },
                onButtonClick = { viewModel.onUIEvent(OnAlertButtonClick) }
            )
        }
    } else {
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TopNavBar(
                    onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                    onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
                )
                CustomInformativeText(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 7.dp, end = 16.dp),
                    trailingIcon = drawable.ic_information_chip,
                    trailingIconClick = { viewModel.onUIEvent(OnOpenDisclaimerDialog) },
                    text = stringResource(id = string.payment_schedule_card_title),
                    textStyle = Typography.h6.copy(color = MultimoneyTheme.colors.text)
                )

                viewModel.uiState.cardVisaDirect?.apply {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                        text = stringResource(id = string.payment_schedule_card_label_origin),
                        style = Typography.body1.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        imageModifier = Modifier.size(48.dp),
                        startIcon = drawable.ic_visa_card_item,
                        title = detail ?: "",
                        subtitle = stringResource(
                            id = string.visa_card_masked_number,
                            cardMaskedNumber?.takeLast(4) ?: 0
                        ),
                        endIcon = drawable.ic_edit_green,
                        onEndIconClick = {
                            viewModel.onUIEvent(OnEditCardVisaDirect)
                        }
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                        text = stringResource(id = string.payment_schedule_card_label_date),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.quickActionLabelColor
                        )
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        text = stringResource(
                            id = string.payment_schedule_card_label_date_description,
                            viewModel.uiState.day
                        ),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            Column {
                CustomButton(
                    onClick = { viewModel.onUIEvent(OnProgramClick) },
                    text = stringResource(id = string.payment_schedule_card_button),
                    modifier = Modifier
                        .padding(bottom = 32.dp, top = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    buttonType = PrimaryPrimary
                )
            }
        }
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}
