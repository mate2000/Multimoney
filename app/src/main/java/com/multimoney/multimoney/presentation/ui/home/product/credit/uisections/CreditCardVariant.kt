package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Text
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
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.IsPaymentExpired
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProgressCalculation
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomRoundedLinearProgress
import com.multimoney.multimoney.presentation.util.getCardDateFormat

/**
 * Composable to handle the status non-preapproved for GT and SV
 */
@Composable
fun CardNonPreApprovedCredit(
    idBrand: Int = Brand.ElSalvador.id,
    action: () -> Unit = {}
) {
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
            text = stringResource(id = string.home_product_gt_sv_non_pre_approved_credit_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(
                id = when (idBrand) {
                    Brand.ElSalvador.id -> string.home_product_sv_non_pre_approved_credit_description
                    else -> string.home_product_gt_non_pre_approved_credit_description
                }
            ),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = string.home_product_gt_sv_non_pre_approved_credit_action),
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
            drawableResource = drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = string.home_product_gt_with_out_credit_action),
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
    action: () -> Unit = {},
    wording: Wording? = Wording("", "", "")
) {
    val notDefinedValue = stringResource(id = R.string.not_defined)
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
            text = wording?.textOne?.filter { wording.textOne != notDefinedValue } ?: "",
            modifier = Modifier.padding(top = 20.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = wording?.textTwo?.filter { wording.textTwo != notDefinedValue } ?: "",
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = drawable.ic_chevron_up
        )
        Text(
            text = wording?.cTA?.filter { wording.cTA != notDefinedValue } ?: "",
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
    action: () -> Unit = {},
    wording: Wording? = Wording("", "", "")
) {
    val notDefinedValue = stringResource(id = R.string.not_defined)
    var chipText = R.string.home_product_process_credit_label
    val title: String = wording?.textOne?.filter { wording.textOne != notDefinedValue } ?: ""
    val description: String = wording?.textTwo?.filter { wording.textTwo != notDefinedValue } ?: ""
    val actionText: String? = wording?.cTA?.filter { wording.textTwo != notDefinedValue }
    var startIcon = R.drawable.ic_time

    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }
    when (type) {
        CreditStartProcessIncomplete -> {
            chipText = string.home_product_process_credit_preapproved_label
            startIcon = drawable.ic_warning
        }
        CreditProcessOnFidoIncomplete -> {
            startIcon = drawable.ic_warning
        }
        CreditProcessOnfidoReject -> {
            startIcon = drawable.ic_warning
        }
        CreditProcessCreateAccountFailure -> {
            startIcon = drawable.ic_warning
        }
        else -> Unit
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
            text = title,
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        actionText?.let {
            if (it.isNotBlank()) {
                CustomImage(
                    modifier = Modifier
                        .padding(top = 21.dp)
                        .align(Alignment.CenterHorizontally),
                    drawableResource = drawable.ic_chevron_up
                )

                Text(
                    text = actionText,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            }
        }
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
            startIcon = drawable.ic_warning
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
            text = stringResource(id = viewModel.uiState.onGoingCreditCardTitle),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        if (viewModel.uiState.isCreditAvailable) {
            Text(
                text = viewModel.balanceCredit?.getFirstSummary()?.availableBalanceLabel.toString(),
                modifier = Modifier.padding(bottom = 10.dp),
                style = Typography.h4.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
        } else {
            Spacer(modifier = Modifier.padding(bottom = 44.dp))
        }
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
            if (viewModel.uiState.isCreditAvailable) {
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = if (viewModel.uiState.isCreditAvailable) {
                        0.dp
                    } else {
                        40.dp
                    },
                    bottom = 14.dp
                )
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
