package com.multimoney.multimoney.presentation.ui.smart.payment.transfer

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
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.BaseEvent.OnCopyTextToClipboardEvent
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnCopyTextToClipboard
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomLabelDescRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent.Navigate
import com.multimoney.multimoney.presentation.util.NavEvent.PopBackStack

@Composable
fun SavingMethodTransferScreen(
    onNavigate: (Navigate) -> Unit = {},
    onPopBackStack: (PopBackStack) -> Unit = {},
    viewModel: SavingMethodTransferViewModel = hiltViewModel()
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val sacAccountNumber = stringResource(R.string.saving_method_transfer_account_number)
    val copiedMessage = stringResource(R.string.saving_method_transfer_account_number_copied)

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
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    viewModel.apply {
        SavingMethodTransferContent(
            onBackClick = { onUIEvent(OnNavigateBack) },
            onGoToHomeClick = { onUIEvent(OnNavigateBackHome) },
            onCopyToClipboard = { onUIEvent(OnCopyTextToClipboard(sacAccountNumber)) },
        )

        BackHandler { onUIEvent(OnNavigateBack) }
    }
}

@Composable
@Preview
fun SavingMethodTransferContent(
    onBackClick: () -> Unit = { },
    onGoToHomeClick: () -> Unit = { },
    onCopyToClipboard: () -> Unit = { },
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = onBackClick,
            isRightButtonVisible = false
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
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(id = R.string.saving_method_transfer_title_sv),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Left
                )
                CustomInformativeText(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    leadingIcon = R.drawable.ic_information,
                    text = stringResource(id = R.string.saving_method_transfer_disclaimer),
                    textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.text)
                )
                CustomLabelDescRow(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .fillMaxWidth(),
                    labelText = stringResource(id = R.string.payment_options_transfer_bank),
                    descriptionText = stringResource(R.string.saving_method_transfer_bank_name)
                )
                CustomLabelDescRow(
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .fillMaxWidth(),
                    labelText = stringResource(id = R.string.saving_method_transfer_account_type_label),
                    descriptionText = stringResource(R.string.saving_method_transfer_saving_account)
                )
                CustomLabelDescRow(
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .fillMaxWidth(),
                    labelText = stringResource(id = R.string.saving_method_transfer_account),
                    descriptionText = stringResource(R.string.saving_method_transfer_account_number),
                    endIcon = R.drawable.ic_copy,
                    endIconClick = onCopyToClipboard
                )
            }
            CustomButton(
                onClick = onGoToHomeClick,
                text = stringResource(id = R.string.payment_options_transfer_go_home),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
    }
}
