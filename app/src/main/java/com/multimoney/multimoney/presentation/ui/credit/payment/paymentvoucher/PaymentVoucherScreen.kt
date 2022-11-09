package com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Dimension.Companion
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnScheduleAutomaticPayment
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@OptIn(ExperimentalTextApi::class)
@Composable
@Preview
fun PaymentVoucherScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentVoucherViewModel = hiltViewModel()
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(isLeftButtonVisible = false, isCenterContentVisible = true, onRightButtonClick = {
            capturingViewBounds?.let { bounds ->
                viewModel.onUIEvent(
                    OnSharedVoucherImage(
                        view,
                        bounds
                    )
                )
            }
        })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                val (backgroundId, contentId, shareButtonId) = createRefs()
                CustomImage(
                    modifier = Modifier.constrainAs(backgroundId) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Companion.fillToConstraints
                    },
                    drawableResource = R.drawable.bg_confirmation_card,
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier
                        .constrainAs(contentId) {
                            top.linkTo(parent.top)
                            bottom.linkTo(shareButtonId.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .onGloballyPositioned {
                            capturingViewBounds = it.boundsInRoot()
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomImage(
                            modifier = Modifier.padding(top = 48.dp),
                            drawableResource = R.drawable.ic_success_symbol
                        )
                        Text(
                            text = stringResource(string.payment_voucher_transaction_success),
                            modifier = Modifier.padding(top = 16.dp),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.text
                        )
                        CustomButton(
                            onClick = {
                                capturingViewBounds?.let { bounds ->
                                    viewModel.onUIEvent(
                                        OnSharedVoucherImage(
                                            view,
                                            bounds
                                        )
                                    )
                                }
                            },
                            text = stringResource(string.payment_voucher_shared_button),
                            modifier = Modifier
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    top = 12.dp
                                )
                                .fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp
                            ),
                            trailingIcon = R.drawable.ic_icon_share,
                            buttonType = CustomButtonType.PrimaryTertiary
                        )
                        Text(
                            text = stringResource(string.payment_voucher_you_have_paid),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            text = "₡60,920",
                            style = Typography.h4.copy(
                                fontWeight = FontWeight.SemiBold,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            text = "₡5,000 + $80",
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                            color = MultimoneyTheme.colors.text
                        )
                    }
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MultimoneyTheme.colors.dividerWhite16, shape = DottedShape(step = 10.dp))
                    )
                    Text(
                        text = stringResource(string.payment_voucher_from_your_account_label),
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (viewModel.shouldDisplayExchangeRate()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        CurrencyExchangeRow(viewModel)
                    }

                    InfoItem(
                        modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                        icon = drawable.ic_receipt,
                        tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                        title = stringResource(string.payment_voucher_reference_number_label),
                        subTitle = viewModel.referenceNumber ?: ""
                    )

                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .padding(start = 27.dp, top = 32.dp)
                    ) {
                        InfoItem(
                            modifier = Modifier.padding(end = 15.dp),
                            icon = drawable.ic_money_voucher,
                            tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                            title = stringResource(string.payment_voucher_exchange_rate_label),
                            subTitle = "₡699.00"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MultimoneyTheme.colors.bottomNavigationDividerColor)
                        )
                        InfoItem(
                            modifier = Modifier.padding(start = 12.dp),
                            title = stringResource(string.payment_voucher_amount_to_pay_label),
                            subTitle = "₡699.00"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 27.dp, top = 34.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar_voucher),
                                tint = MultimoneyTheme.colors.iconTintVoucher,
                                contentDescription = ""
                            )
                            Text(
                                text = viewModel.currentDate,
                                modifier = Modifier.padding(start = 15.dp),
                                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.labelText
                            )

                        }
                        Text(
                            text = viewModel.currentTime,
                            modifier = Modifier.padding(bottom = 16.dp),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                }

            }
            if (viewModel.uiState.showScheduleAutomaticPaymentProcess) {
                CustomButton(
                    onClick = { viewModel.onUIEvent(OnScheduleAutomaticPayment) },
                    text = stringResource(string.payment_voucher_schedule_payment),
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 32.dp,
                            top = 16.dp
                        )
                        .fillMaxWidth()
                        .height(48.dp),
                    buttonType = PrimaryPrimary
                )
            }
        }
    }
}


@Composable
fun CurrencyExchangeRow(viewModel: PaymentVoucherViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_exchange_type),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = viewModel.uiState.exchangeRateLabel.toString(),
                style = Typography.body2,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(40.dp))
        Divider(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_amount_to_debit),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = viewModel.uiState.exchangeConvertedAmount.toString(),
                style = Typography.body2,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun InfoItem(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    tintIcon: Color = Color.Transparent,
    title: String,
    subTitle: String
) {
    Row(modifier = modifier) {
        icon?.let {
            Icon(painter = painterResource(id = it), contentDescription = "", tint = tintIcon)
        }
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = MultimoneyTheme.colors.labelText
            )
        }
    }
}
