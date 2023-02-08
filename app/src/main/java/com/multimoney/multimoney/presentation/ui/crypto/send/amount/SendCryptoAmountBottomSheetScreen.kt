package com.multimoney.multimoney.presentation.ui.crypto.send.amount

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomLabeledInfoText
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendCryptoAmountBottomSheetScreen(
//    viewModel: ,
//    coroutineScope: CoroutineScope,
//    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = R.string.crypto_send_amount_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded), //TODO get from param
        coroutineScope = rememberCoroutineScope(), //TODO get from param
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "0.000027 BTC", //TODO get from viewModel
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            ){
                Text(
                    text = stringResource(id = R.string.crypto_send_amount_bottom_sheet_approximate_value_dollars),
                    style = Typography.body2.copy(fontWeight = FontWeight.Light),
                    color = Color.LightGray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$1.18", //TODO get from viewModel
                    style = Typography.body2.copy(fontWeight = FontWeight.W600),
                    color = Color.LightGray,
                    textAlign = TextAlign.Start
                )
            }
            CustomLabeledInfoText(
                icon = R.drawable.ic_network_public,
                iconColor = Color.White,
                label = stringResource(id = R.string.crypto_send_amount_bottom_sheet_to_address),
                text = "139cmexU1KpNMrgqBT7REqjf6mXQAvX5rQ", //TODO get from viewModel
                labelStyle = Typography.body2.copy(fontWeight = FontWeight.W600),
                textStyle = Typography.body2.copy(fontWeight = FontWeight.Light),
                labelColor = MultimoneyTheme.colors.text,
                textColor = Color.LightGray,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            CustomLabeledInfoText(
                icon = R.drawable.ic_receipt,
                iconColor = Color.White,
                label = stringResource(id = R.string.crypto_send_amount_bottom_sheet_approximate_transaction_fee),
                text = "0.0000059 BTC", //TODO get from viewModel
                labelStyle = Typography.body2.copy(fontWeight = FontWeight.W600),
                textStyle = Typography.body2.copy(fontWeight = FontWeight.Light),
                labelColor = MultimoneyTheme.colors.text,
                textColor = Color.LightGray,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { }, //TODO perform send action
                text = stringResource(id = R.string.crypto_send_amount_bottom_sheet_button_send),
                buttonType = CustomButtonType.PrimaryPrimary
            )
        }
    }
}

