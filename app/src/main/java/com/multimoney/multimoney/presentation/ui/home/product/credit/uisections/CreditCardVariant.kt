package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.Wording
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency16
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomRoundedLinearProgress
import com.multimoney.multimoney.presentation.util.getCardDateFormat
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrencySymbolValue

/**
 * Composable to handle the status without credit GT
 */
@Composable
@Preview
fun CardGTWithoutCredit(action: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                action()
            }
    ) {
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_description),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_action),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}

// Credit In Process
/**
 * Composable function handle to status of the credit pre approved
 */
@Composable
@Preview
fun CreditPreApproved(
    amount: String? = "0.0",
    idBrand: Int = Brand.ElSalvador.id,
    action: () -> Unit = {}
) {
    var description = ""

    description = if (idBrand == Brand.Guatemala.id) {
        stringResource(
            id = R.string.home_product_gt_credit_approved_card_description,
            amount ?: "0.0"
        )
    } else {
        stringResource(
            id = R.string.home_product_credit_approved_card_description,
            amount ?: "0.0"
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .clickable {
                action()
            }
    ) {
        Text(
            text = stringResource(id = R.string.home_product_credit_approved_card_title),
            modifier = Modifier.padding(top = 20.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.home_product_credit_approved_card_action),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
    }
}

@Composable
@Preview
fun CardWithCreditInProcess(
    type: CreditProcessStarted = CreditProcessOnfidoReject,
    idBrand: Int = Brand.ElSalvador.id,
    action: () -> Unit = {}
) {
    val chipText = R.string.home_product_process_credit_label
    val title: Int
    val description: Int
    val actionText: Int
    var startIcon = R.drawable.ic_time

    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }
    when (type) {
        CreditStartProcessIncomplete -> {
            title = R.string.home_product_credit_not_completed_title
            description = R.string.home_product_credit_not_completed_description
            actionText = R.string.home_product_credit_not_completed_action
            startIcon = R.drawable.ic_warning
        }
        CreditProcessOnFidoIncomplete -> {
            title = R.string.home_on_fido_pending_title
            description = R.string.home_on_fido_pending_description
            actionText = R.string.home_on_fido_pending_action_text
            startIcon = R.drawable.ic_warning
        }
        CreditProcessFirmIncomplete -> {
            title = R.string.home_firm_incomplete_title
            description = R.string.home_firm_incomplete_description
            actionText = R.string.home_firm_incomplete_action
        }
        CreditProcessOnfidoReject -> {
            title = R.string.onfido_rejected_first_time_title
            description = if (idBrand == Brand.Guatemala.id) {
                R.string.onfido_rejected_first_time_subtitle_gt
            } else {
                R.string.onfido_rejected_first_time_subtitle
            }
            actionText = R.string.onfido_rejected_action
            startIcon = R.drawable.ic_warning
        }
        CreditProcessOnfidoMaxAttempts -> {
            title = R.string.onfido_rejected_second_time_title
            description = R.string.onfido_rejected_second_time_subtitle
            actionText = R.string.onfido_rejected_action_second_time
        }
        CreditProcessFirmReject -> {
            title = R.string.sign_document_reject_title
            description = if (idBrand == Brand.Guatemala.id) {
                R.string.sign_document_reject_description_gt
            } else {
                R.string.sign_document_reject_description
            }
            actionText = R.string.home_firm_incomplete_action
        }
        CreditProcessFirmMaxAttempts -> {
            title = R.string.sign_credit_max_attempts_title
            description = if (idBrand == Brand.Guatemala.id) {
                R.string.sign_credit_max_attempts_message_gt
            } else {
                R.string.sign_credit_max_attempts_message
            }
            actionText = R.string.sign_credit_max_attempts_contact
        }
        CreditProcessCreateAccountFailure -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action
            startIcon = R.drawable.ic_warning
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                action.invoke()
            }
    ) {
        CustomInformativeChip(
            text = stringResource(id = chipText),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = backgroundShip,
            startIcon = startIcon,
            startIconTint = MultimoneyTheme.colors.iconColor
        )
        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = description),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 21.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = actionText),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable function show the status of evicertia firmed and onfido pending
 */
@Composable
@Preview
fun CardCreditFirmedAndOnfidoPending() {
    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_product_process_credit_label),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = backgroundShip,
            startIcon = R.drawable.ic_warning
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_description),
            modifier = Modifier.padding(top = 8.dp, bottom = 73.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun OngoingCredit(
    viewModel: ProductViewModel
) {
    viewModel.onUIEvent(OnProgressCalculation)
    viewModel.onUIEvent(IsPaymentExpired)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_product_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = viewModel.balanceCredit?.getFirstSummary()?.availableBalanceLabel.toString(),
            modifier = Modifier.padding(bottom = 10.dp),
            style = Typography.h4.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomRoundedLinearProgress(
            progress = viewModel.productProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = stringResource(
                    id = R.string.home_product_remaining,
                    viewModel.balanceCredit?.getFirstSummary()?.currentBalanceLabel.toString()
                ),
                modifier = Modifier.padding(top = 4.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
            Text(
                text = stringResource(
                    id = R.string.home_product_amount,
                    viewModel.balanceCredit?.getFirstCredit()?.creditLimitLabel.toString()
                ),
                modifier = Modifier.padding(top = 4.dp, start = 3.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.textSubhead
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.weight(0.5F)) {
                Text(
                    text = stringResource(id = R.string.home_product_fee),
                    modifier = Modifier.padding(top = 4.dp),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    text = viewModel.balanceCredit?.getFirstSummary()?.monthlyQuotaLabel.toString(),
                    modifier = Modifier.padding(top = 4.dp),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
            }
            Column(modifier = Modifier.weight(0.5F)) {
                Text(
                    text = stringResource(id = viewModel.isExpiredTitle),
                    modifier = Modifier.padding(top = 4.dp),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Chip(
                    enabled = false,
                    colors = ChipDefaults.chipColors(
                        disabledBackgroundColor = MultimoneyTheme.colors.productChipBackground,
                        disabledContentColor = MultimoneyTheme.colors.text
                    ),
                    modifier = Modifier
                        .height(28.dp)
                        .padding(top = 2.dp),
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if ((
                                        viewModel.balanceCredit?.getFirstSummary()?.daysExpired
                                            ?: 0
                                        ) > 0
                                    ) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.tipActionColor
                                )
                        )
                    },
                    onClick = {
                        // Empty on purpose
                    },
                    content = {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = getCardDateFormat(viewModel.balanceCredit?.getFirstSummary()?.paymentDateLabel),
                                style = Typography.body1.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

sealed class CreditProcessStarted {
    object CreditStartProcessIncomplete : CreditProcessStarted()
    object CreditProcessOnFidoIncomplete : CreditProcessStarted()
    object CreditProcessFirmIncomplete : CreditProcessStarted()
    object CreditProcessFirmReject : CreditProcessStarted()
    object CreditProcessFirmMaxAttempts : CreditProcessStarted()
    object CreditProcessOnfidoReject : CreditProcessStarted()
    object CreditProcessOnfidoMaxAttempts : CreditProcessStarted()
    object CreditProcessCreateAccountFailure : CreditProcessStarted()
}
