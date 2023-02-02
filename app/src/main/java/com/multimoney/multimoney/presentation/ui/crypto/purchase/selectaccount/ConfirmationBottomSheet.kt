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
import androidx.compose.ui.text.font.FontWeight
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
) {
    CustomModalBottomSheet(
        title = R.string.empty,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        ConfirmationBottomSheetContent()
    }
}

@Preview
@Composable
fun ConfirmationBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.buy_crypto_warning_bs_content),
            style = Typography.body2.copy(fontWeight = FontWeight.W600),
            color = MultimoneyTheme.colors.text,
        )
        CustomCheckBox(
            checked = false, //ToDo add sharedViewModel viewModel uiState parameter
            onCheckedChange = {
                //ToDo implement logic for saving state of the checkboc
            },
            text = stringResource(id = R.string.buy_crypto_warning_dont_show_again),
        )
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp),
            onClick = {
                      // todo save preference using setVolatileDialogVisible
                      //  and save the value depending on the checkbox value
                //ToDo on navigate to next step
            },
            text = stringResource(id = R.string.button_continue),
            buttonType = CustomButtonType.PrimaryTertiary
        )
    }
}