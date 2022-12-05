package com.multimoney.multimoney.presentation.ui.smart.payment.paymentdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@Composable
fun PaymentSuccessScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentSuccessViewModel = hiltViewModel()
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    var offset: Rect

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .onGloballyPositioned { capturingViewBounds = it.boundsInRoot() }
    ) {
        TopNavBar(isLeftButtonVisible = false, isCenterContentVisible = true, onRightButtonClick = {
            // todo viewModel.onUIEvent(OnCloseClick)
        })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomImage(
                    modifier = Modifier.fillMaxSize(),
                    drawableResource = R.drawable.bg_confirmation_card,
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = stringResource(R.string.smart_payment_success),
                            modifier = Modifier.padding(top = 16.dp),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.text
                        )
                        CustomButton(
                            onClick = {
                                // todo
//                                capturingViewBounds?.let { bounds ->
//                                    viewModel.onUIEvent(
//                                        OnSharedVoucherImage(
//                                            view,
//                                            bounds
//                                        )
//                                    )
//                                }
                            },
                            text = stringResource(R.string.smart_payment_shared_button),
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
                            buttonType = PrimaryTertiary
                        )
                        Text(
                            text = stringResource(R.string.smart_payment_you_saved_on_you_smart_account),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )
// todo
//                        if (viewModel.isMultiCurrency == true) {
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = viewModel.currentAmountValueString ?: "",
//                                style = Typography.h4.copy(fontWeight = FontWeight.W600),
//                                color = MultimoneyTheme.colors.text,
//                                textAlign = TextAlign.Center
//                            )
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = viewModel.paymentLabel ?: "x",
//                                style = Typography.body2.copy(fontWeight = FontWeight.W600),
//                                color = MultimoneyTheme.colors.text,
//                                textAlign = TextAlign.Center
//                            )
//                        } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "$500",
                            style = Typography.h4.copy(fontWeight = FontWeight.W600),
                            color = MultimoneyTheme.colors.text,
                            textAlign = TextAlign.Center
                        )
//                        }
                    }
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MultimoneyTheme.colors.dividerWhite16, shape = DottedShape(step = 10.dp))
                    )
                    Text(
                        text = stringResource(R.string.smart_payment_from_label),
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )

                    InfoItemAccount(
                        modifier = Modifier.padding(start = 27.dp, top = 24.dp),
                        icon = R.drawable.ic_bank,
                        tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                        title = stringResource(R.string.smart_payment_origin_account_label),
                        subTitle = "[Inicial pais]••••0000"
//                            viewModel.clientBankAccount?.accountNumber ?: "",
//                            stringResource(id = string.payment_account_masked_text)
                    )

                    InfoItem(
                        modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                        icon = R.drawable.ic_receipt,
                        tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                        title = stringResource(R.string.smart_payment_reference_number_label),
                        subTitle = "7453892" // viewModel.referenceNumber ?: ""
                    )

//                    if (viewModel.shouldDisplayExchangeRate == true) {
//                        Spacer(modifier = Modifier.height(32.dp))
//                        CurrencyExchangeRow(
//                            viewModel
//                        )
//                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 27.dp, top = 34.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar_voucher),
                                tint = MultimoneyTheme.colors.iconTintVoucher,
                                contentDescription = ""
                            )
                            Text(
                                text = "12|06|2022", // viewModel.currentDate,
                                modifier = Modifier.padding(start = 15.dp),
                                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.labelText
                            )
                        }
                        Text(
                            text = "8:12 am", // viewModel.currentTime,
                            modifier = Modifier.padding(bottom = 16.dp),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                }
            }

            CustomButton(
                onClick = { },
                text = stringResource(R.string.smart_payment_make_another_payment),
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

@Composable
fun CurrencyExchangeRow(viewModel: PaymentVoucherViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 27.dp)
    ) {
        Icon(

            painter = painterResource(R.drawable.ic_money_gray),
            tint = MultimoneyTheme.colors.iconTintVoucher,
            contentDescription = "",
            modifier = Modifier.height(24.dp).width(24.dp)
        )
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_exchange_type),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = viewModel.exchangeRateLabel.toString(),
                style = Typography.body2,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Divider(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
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

@Composable
fun InfoItemAccount(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    tintIcon: Color = Color.Transparent,
    title: String,
    subTitle: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "",
                tint = tintIcon,
                modifier = Modifier.height(24.dp).width(24.dp).alpha(0.4f)
            )
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
