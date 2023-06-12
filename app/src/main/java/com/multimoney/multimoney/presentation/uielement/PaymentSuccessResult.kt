package com.multimoney.multimoney.presentation.uielement

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.util.shape.DottedShape

/**
 * Parameters:
 * @param onShareClick: Action to perform when share button is pressed.
 *         Passes view and bounds to handle screen-capture.
 * @param savePayText: String of title indicating save or pay action
 * @param amount: String of amount to be shown (Should be sent formatted)
 * @param exchangedAmount: String of amount exchanged (Should be sent formatted)
 * @param fromToText: String of label indicating if transaction is to or from account Smart
 * @param buttonText: String of button text
 * @param onButtonClick: Action to perform on button click
 * @param infoContent: Composable items to be shown indicating the info of the transaction
 */

@Composable
fun PaymentSuccessResult(
    onShareClick: (view: View, bounds: Rect) -> Unit,
    savePayText: String,
    amount: String,
    exchangedAmount: String? = null,
    fromToText: String,
    buttonText: String = "",
    showButton: Boolean = true,
    onButtonClick: () -> Unit = {},
    infoContent: @Composable () -> Unit = {}
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                .onGloballyPositioned {
                    capturingViewBounds = it.boundsInRoot()
                },
            contentAlignment = Alignment.TopCenter
        ) {
            CustomImage(
                modifier = Modifier.matchParentSize(),
                drawableResource = drawable.bg_confirmation_card,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = if (exchangedAmount.isNullOrBlank().not()) 16.dp else 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(string.smart_payment_success),
                        modifier = Modifier.padding(top = 24.dp),
                        style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.text
                    )
                    CustomButton(
                        onClick = {
                            capturingViewBounds?.let { bounds ->
                                onShareClick(view, bounds)
                            }
                        },
                        text = stringResource(string.smart_payment_share_button),
                        modifier = Modifier
                            .padding(
                                start = 24.dp,
                                end = 24.dp
                            )
                            .fillMaxWidth(),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp
                        ),
                        trailingIcon = drawable.ic_icon_share,
                        buttonType = PrimaryTertiary
                    )
                    Text(
                        text = savePayText,
                        style = Typography.body1.copy(letterSpacing = -(0.32.sp)),
                        color = MultimoneyTheme.colors.text
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = amount,
                        style = Typography.h4.copy(
                            fontWeight = FontWeight.W600
                        ),
                        color = MultimoneyTheme.colors.text,
                        textAlign = TextAlign.Center
                    )
                    if (exchangedAmount.isNullOrBlank().not()) {
                        ExchangeTotalLabel(totalConverted = exchangedAmount.orEmpty())
                    }
                }
                Box(
                    Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            MultimoneyTheme.colors.dividerWhite16,
                            shape = DottedShape(step = 12.dp)
                        )
                )
                Text(
                    text = fromToText,
                    modifier = Modifier.padding(start = 21.dp, top = 16.dp),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                infoContent()
                Spacer(Modifier.height(16.dp))
            }
        }
        if (showButton) {
            CustomButton(
                onClick = onButtonClick,
                text = buttonText,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PaymentSuccessResultPreview() {
    PaymentSuccessResult(
        onShareClick = { _, _ -> },
        savePayText = "Monto ahorrado a tu cuenta smart | $",
        amount = "$500",
        exchangedAmount = "",
        fromToText = "Desde",
        showButton = false,
        buttonText = "Hacer otro ahorro",
        onButtonClick = {}
    ) {
        SmartPaymentInfoItem(
            verticalAlignment = Alignment.CenterVertically,
            icon = drawable.ic_visa_card_item,
            title = "Tarjeta",
            subtitle = "Visa••••1234"
        )

        SmartPaymentInfoItem(
            verticalAlignment = Alignment.Top,
            icon = drawable.ic_receipt,
            title = stringResource(string.smart_payment_reference_number_label),
            subtitle = "5365841"
        )

        SmartPaymentInfoItem(
            icon = drawable.ic_money_gray,
            title = stringResource(string.payment_amount_bottom_sheet_exchange_type),
            subtitle = "1000",
            rightTitle = stringResource(string.payment_amount_bottom_sheet_amount_to_debit),
            rightSubtitle = "500",
            showVerticalDivision = true
        )

        SmartPaymentInfoItem(
            verticalAlignment = Alignment.CenterVertically,
            icon = drawable.ic_calendar,
            title = "12/12/2022",
            rightSubtitle = "12:05"
        )
    }
}
