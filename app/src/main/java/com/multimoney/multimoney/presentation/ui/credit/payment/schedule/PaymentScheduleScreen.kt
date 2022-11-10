package com.multimoney.multimoney.presentation.ui.credit.payment.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnEditBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun PaymentScheduleScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentScheduleViewModel = hiltViewModel()
) {
    // Navigation
    viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    PaymentScheduleContent(viewModel)
}

@Composable
@Preview
fun PaymentScheduleContent(
    viewModel: PaymentScheduleViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isRightButtonVisible = false
            )
            CustomInformativeText(
                modifier = Modifier.fillMaxWidth().padding(top = 34.dp, start = 7.dp, end = 16.dp),
                trailingIcon = drawable.ic_information_chip,
                trailingIconClick = { viewModel.onUIEvent(OnOpenDisclaimerDialog) },
                text = stringResource(id = string.payment_schedule_title),
                textStyle = Typography.h6.copy(color = MultimoneyTheme.colors.text)
            )

            viewModel.uiState.clientBankAccount?.apply {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = string.payment_schedule_label_origin),
                    style = Typography.body1.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    startIcon = idCurrency?.getCurrency()?.accountIcon
                        ?: R.drawable.ic_account_dollar,
                    title = bankDescription ?: "",
                    subtitle = getMaskedAccount(
                        accountNumber ?: "",
                        stringResource(id = string.payment_account_masked_text)
                    ),
                    endIcon = drawable.ic_edit_green,
                    onEndIconClick = {
                        viewModel.onUIEvent(OnEditBankAccount)
                    }
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = string.payment_schedule_label_date),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.labelText
                    )
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    text = if (debitDate?.isNotEmpty() == true) {
                        viewModel.uiState.clientBankAccount?.debitDate ?: ""
                    } else {
                        stringResource(id = string.payment_schedule_label_date_description, viewModel.uiState.day)
                    },
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
                text = stringResource(id = string.payment_schedule_button),
                modifier = Modifier
                    .padding(bottom = 32.dp, top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}
