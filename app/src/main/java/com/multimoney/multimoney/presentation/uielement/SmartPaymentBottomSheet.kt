package com.multimoney.multimoney.presentation.uielement

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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

/**
 * Parameters:
 * @param coroutineScope: Coroutine scope
 * @param modalBottomSheetState: Bottom sheet state
 * @param saveSendTitleResource: Resource of title to be shown indicating save to Smart or send from Smart
 * @param amount: String of amount (Should be formatted)
 * @param exchangedAmount: String of amount exchanged for currency conversion (Should be formatted)
 * @param fromTitle: String indicating title of origin account
 * @param fromSubtitle: String indicating subtitle of origin account
 * @param fromIcon: Resource indicating icon of origin account
 * @param toTitle: String indicating title of destination account
 * @param toSubtitle: String indicating subtitle of destination account
 * @param toSubtitle2: String indicating second line subtitle of destination account
 * @param toIcon: Resource indicating icon of destination account
 * @param motive: String of motive (null to not include field, empty string will show field empty)
 * @param buttonText: String indicating label text of button
 * @param buttonAction: Action to call on button click
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartPaymentBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    saveSendTitleResource: Int,
    amount: String,
    exchangedAmount: String? = null,
    fromTitle: String? = null,
    fromSubtitle: String? = null,
    fromIcon: Int? = null,
    titleIcon: Int? = null,
    toTitle: String? = null,
    toSubtitle: String? = null,
    toSubtitle2: String? = null,
    toIcon: Int? = null,
    toContactInfo: @Composable (() -> Unit)? = null,
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
                    totalConverted = exchangedAmount.orEmpty()
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.smart_payment_amount_bottom_sheet_from),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )

            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                imageModifier = Modifier.size(48.dp),
                startIcon = fromIcon,
                title = fromTitle.orEmpty(),
                subtitle = fromSubtitle.orEmpty(),
                endIcon = null,
                enable = false
            )

            Icon(
                painter = painterResource(R.drawable.ic_down_arrow_from_to),
                tint = Color.Unspecified,
                contentDescription = "",
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )

            if (toContactInfo == null) {
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    startIcon = toIcon,
                    title = toTitle.orEmpty(),
                    subtitle = toSubtitle.orEmpty(),
                    subtitle2 = toSubtitle2.orEmpty(),
                    titleIcon = titleIcon,
                    endIcon = null,
                    enable = false
                )
            } else {
                toContactInfo()
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
            motive?.let {
                Text(
                    text = stringResource(id = R.string.motive),
                    style = Typography.body2.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = motive,
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
    // To see this preview you may need to start interactive mode
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = ModalBottomSheetState(Expanded),
        saveSendTitleResource = R.string.smart_payment_amount_bottom_sheet_save_title,
        amount = "$500",
        exchangedAmount = "₡320,980",
        fromTitle = "Banco de Costa Rica",
        fromSubtitle = "CR••••4893",
        fromIcon = R.drawable.ic_visa_card_item,
        toTitle = "Mi Cuenta Smart | $",
        toSubtitle = "Dólares",
        toIcon = R.drawable.ic_bank_account_dollar,
        titleIcon = R.drawable.ic_star_filled,
        motive = "Cena de ayer",
        buttonText = "continuar"
    ) {}
}
