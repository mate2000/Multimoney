package com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfirmationBottomSheet(
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    onCheckedChange: (Boolean) -> Unit,
    onContinueClicked: () -> Unit,
    checked: Boolean
) {
    CustomModalBottomSheet(
        title = R.string.empty,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        ConfirmationBottomSheetContent(onCheckedChange, onContinueClicked,checked)
    }
}

@Preview
@Composable
fun ConfirmationBottomSheetContent(
    onCheckedChange: (Boolean) -> Unit = {},
    onContinueClicked: () -> Unit = {},
    checked: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.buy_crypto_warning_bs_content),
            style = Typography.body2.copy(),
            color = MultimoneyTheme.colors.text,
        )
        CustomCheckBox(
            checked = checked, //ToDo add sharedViewModel viewModel uiState parameter
            onCheckedChange = {
                onCheckedChange.invoke(it)
            },
            text = stringResource(id = R.string.buy_crypto_warning_dont_show_again),
        )
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp),
            onClick = {
                onContinueClicked.invoke()
            },
            text = stringResource(id = R.string.button_continue),
            buttonType = CustomButtonType.PrimaryPrimary
        )
    }
}