package com.multimoney.multimoney.presentation.ui.credit.payment.transfer

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.BaseEvent.OnCopyTextToClipboardEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnCopyTextToClipboard
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomLabelDescRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent.Navigate
import com.multimoney.multimoney.presentation.util.NavEvent.PopBackStack

@Composable
fun PaymentOptionsTransferScreen(
    onNavigate: (Navigate) -> Unit = {},
    onPopBackStack: (PopBackStack) -> Unit = {},
    viewModel: PaymentOptionsTransferViewModel = hiltViewModel()
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(
                onNavigate = onNavigate,
                onPopBackStack = onPopBackStack
            )
            baseEvent.collect { event ->
                when (event) {
                    is OnCopyTextToClipboardEvent -> {
                        // When setting the clip board text.
                        clipboardManager.setText(AnnotatedString(event.text))
                        // Only show a toast for Android 12 and lower.
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    PaymentOptionsTransferContent(viewModel)
    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }
}

@Composable
@Preview
fun PaymentOptionsTransferContent(viewModel: PaymentOptionsTransferViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBackHome(true)) }
        )
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(0.87f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(id = viewModel.uiState.titleResource),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Left
                )
                CustomInformativeText(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    leadingIcon = R.drawable.ic_information,
                    text = stringResource(id = R.string.payment_options_transfer_disclaimer),
                    textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.text)
                )
                if (viewModel.uiState.isAccountNumberVisible) {
                    CustomLabelDescRow(
                        modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                        labelText = stringResource(id = R.string.payment_options_transfer_account),
                        descriptionText = viewModel.uiState.transferAccount?.account ?: ""
                    )
                }
                CustomLabelDescRow(
                    modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                    labelText = stringResource(id = R.string.payment_options_transfer_bank),
                    descriptionText = viewModel.uiState.transferAccount?.bank ?: ""
                )
                CustomLabelDescRow(
                    modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                    labelText = stringResource(id = R.string.payment_options_transfer_type_transfer),
                    descriptionText = viewModel.uiState.transferAccount?.typeTransfer ?: ""
                )
                CustomLabelDescRow(
                    modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                    labelText = stringResource(id = R.string.payment_options_transfer_credit_number),
                    descriptionText = viewModel.uiState.creditNumber ?: "",
                    endIcon = R.drawable.ic_copy,
                    endIconClick = { viewModel.onUIEvent(OnCopyTextToClipboard(viewModel.uiState.creditNumber ?: "")) }
                )
                CustomLabelDescRow(
                    modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                    labelText = stringResource(id = R.string.payment_options_transfer_beneficiary_name),
                    descriptionText = viewModel.uiState.transferAccount?.beneficiaryName ?: ""
                )
            }
            CustomButton(
                onClick = { viewModel.onUIEvent(OnNavigateBackHome(false)) },
                text = stringResource(id = R.string.payment_options_transfer_go_home),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }

        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = stringResource(id = viewModel.uiState.openDialog.descriptionResource),
                negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction
            )
        }
    }
}
