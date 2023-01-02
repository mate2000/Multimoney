package com.multimoney.multimoney.presentation.uielement

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartPaymentBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    saveSendTitleResource: Int,
    amount: String,
    exchangedAmount: String? = null,
    fromLabel: String,
    fromTitle: String,
    fromSubtitle: String?,
    fromIcon: Int,
    toLabel: String,
    toTitle: String,
    toSubtitle: String?,
    toIcon: Int,
    motive: String? = null,
    buttonText: String,
    buttonAction: () -> Unit
) {
    CustomModalBottomSheet(
        title = saveSendTitleResource,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = amount,
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            if (exchangedAmount.isNullOrBlank().not()) {
                ExchangeTotalLabel(
                    totalConverted = exchangedAmount ?: ""
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                text = fromLabel,
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            fromSubtitle?.let {
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp),
                    startIcon = fromIcon,
                    title = fromTitle,
                    subtitle = it,
                    endIcon = null,
                    enable = false
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_down_arrow_from_to),
                tint = Color.Unspecified,
                contentDescription = "",
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = toLabel,
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp),
                startIcon = toIcon,
                title = toTitle,
                subtitle = toSubtitle ?: "",
                endIcon = null,
                enable = false
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
            if (motive.isNullOrBlank().not()) {
                Text(
                    text = stringResource(id = R.string.motive),
                    style = Typography.body2.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = motive ?: "",
                    style = Typography.subtitle2,
                    color = MultimoneyTheme.colors.subTitleText,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                )
            }
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = buttonAction,
                text = buttonText,
                buttonType = PrimaryPrimary,
                enable = true,
                visible = true
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun BottomSheetPreview() {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = ModalBottomSheetState(Expanded),
        saveSendTitleResource = R.string.smart_payment_amount_bottom_sheet_title,
        amount = "$500",
        exchangedAmount = "₡320,980",
        fromLabel = "Desde tu cuenta",
        fromTitle = "Banco de Costa Rica",
        fromSubtitle = "CR••••4893",
        fromIcon = R.drawable.ic_visa_card_item,
        toLabel = "Hacias",
        toTitle = "Mi Cuenta Smart | $",
        toSubtitle = "Dólares",
        toIcon = R.drawable.ic_bank_account_dollar,
        buttonText = "continuar",
        motive = "Cena de ayer"
    ) {
    }
}
