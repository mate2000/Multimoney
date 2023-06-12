package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoamount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomLabeledInfoText
import com.multimoney.multimoney.presentation.util.roundToEightDecimalPlaces
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendCryptoAmountBottomSheetScreen(
    viewModel: CryptoSendAmountViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    Column(modifier = Modifier
        .wrapContentSize()
        .background(color = MultimoneyTheme.colors.creditDetailBackground)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.crypto_send_amount_bottom_sheet_title),
                style = Typography.subtitle1.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.Bold
                )
            )
            Image(
                modifier = Modifier.clickable {
                    coroutineScope.launch { modalBottomSheetState.hide() }
                },
                painter = painterResource(id = R.drawable.ic_close_bottom_sheet),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${viewModel.uiState.sendCryptoAmount.roundToEightDecimalPlaces()} ${viewModel.asset}",
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
                    text = viewModel.uiState.sendDollarAmount,
                    style = Typography.body2.copy(fontWeight = FontWeight.W600),
                    color = Color.LightGray,
                    textAlign = TextAlign.Start
                )
            }
            CustomLabeledInfoText(
                icon = R.drawable.ic_network_public,
                iconColor = Color.White,
                label = stringResource(id = R.string.crypto_send_amount_bottom_sheet_to_address),
                text = viewModel.destinationAddress,
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
                text = "${viewModel.uiState.transferCommission?.transferFee?.totalFee?.roundToEightDecimalPlaces()} ${viewModel.asset}",
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
                onClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnSendCryptoCurrency)
                },
                text = stringResource(id = R.string.crypto_send_amount_bottom_sheet_button_send),
                buttonType = CustomButtonType.PrimaryPrimary
            )
        }
    }
}

