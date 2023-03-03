package com.multimoney.multimoney.presentation.ui.crypto.sell.voucher

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.VoucherAccountInfo
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.VoucherNumberInfo
import com.multimoney.multimoney.presentation.uielement.VoucherTotalAmountInfo
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@Preview
@Composable
fun SellCryptoVoucherScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    sharedViewModel: SellCryptoSharedViewModel = hiltViewModel(),
    viewModel: SellCryptoVoucherViewModel = hiltViewModel()
) {
    SellCryptoVoucherContent(
        assetAmount = sharedViewModel.uiState.voucherAssetAmount ?: "",
        currencyName = sharedViewModel.uiState.asset ?: "",
        approximateValue = sharedViewModel.uiState.voucherApproximateValue ?: "",
        accountNumber = sharedViewModel.getAccountNumber(stringResource(id = R.string.payment_account_masked_text)),
        referenceNumber = sharedViewModel.uiState.voucherReferenceNumber ?: "",
        currentDate = sharedViewModel.uiState.voucherSellDate ?: "",
        currentTime = sharedViewModel.uiState.voucherSellTime ?: "",
        exchangeRate = sharedViewModel.uiState.voucherExchangeRate ?: "",
        totalCreditedAmountExchange = sharedViewModel.uiState.voucherTotalCreditedAmountExchange,
        totalCreditedAmount = sharedViewModel.uiState.voucherTotalCreditedAmount ?: "",
        idCurrency = sharedViewModel.uiState.idCurrency,
        viewModel = viewModel
    )
}

@Composable
fun SellCryptoVoucherContent(
    assetAmount: String,
    currencyName: String,
    approximateValue: String,
    accountNumber: String,
    referenceNumber: String,
    currentDate: String,
    currentTime: String,
    exchangeRate: String,
    totalCreditedAmountExchange: String,
    totalCreditedAmount: String,
    idCurrency: Int? = CurrencyType.Dollar.id,
    viewModel: SellCryptoVoucherViewModel
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                        width = Dimension.fillToConstraints
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
                        Text(
                            text = stringResource(R.string.payment_voucher_transaction_success),
                            modifier = Modifier.padding(top = 16.dp),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.text
                        )
                        CustomButton(
                            onClick = {
                                capturingViewBounds?.let { bounds ->
                                    viewModel.onUIEvent(
                                        SellCryptoVoucherViewModel.UIEvent.OnSharedVoucherImage(
                                            view,
                                            bounds
                                        )
                                    )
                                }
                            },
                            text = stringResource(R.string.payment_voucher_shared_button),
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
                            text = stringResource(R.string.sell_crypto_voucher_you_have_sold),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = assetAmount,
                            style = Typography.h4.copy(fontWeight = FontWeight.W600),
                            color = MultimoneyTheme.colors.text,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(
                                id = R.string.buy_crypto_voucher_value_in_currency_template,
                                (if (idCurrency == CurrencyType.Dollar.id)
                                    CurrencyType.Dollar.stringName
                                else CurrencyType.Colon.stringName).lowercase()
                            ),
                            style = Typography.body2.copy(fontWeight = FontWeight.W600),
                            color = MultimoneyTheme.colors.text,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = approximateValue,
                            style = Typography.body2.copy(fontWeight = FontWeight.W100),
                            color = MultimoneyTheme.colors.text,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(
                                MultimoneyTheme.colors.dividerWhite16,
                                shape = DottedShape(step = 10.dp)
                            )
                    )
                    Text(
                        text = stringResource(R.string.sell_crypto_voucher_to_account),
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )

                    VoucherAccountInfo(
                        modifier = Modifier.padding(start = 27.dp, top = 24.dp),
                        icon = R.drawable.ic_multimoney_voucher_gray,
                        title = stringResource(R.string.payment_voucher_origin_account_label),
                        subTitle = accountNumber
                    )

                    VoucherNumberInfo(
                        modifier = Modifier.padding(start = 24.dp, top = 32.dp),
                        icon = R.drawable.ic_receipt,
                        title = stringResource(R.string.payment_voucher_reference_number_label),
                        subTitle = referenceNumber
                    )

                    if (idCurrency == CurrencyType.Colon.id) {
                        Spacer(modifier = Modifier.height(32.dp))
                        VoucherCurrencyExchangeInfo(
                            leftTitleResource = R.string.sell_crypto_voucher_amount_credited,
                            rightTitleResource = R.string.buy_crypto_voucher_exchange_rate,
                            exchangeRateText = totalCreditedAmountExchange,
                            convertedAmountText = exchangeRate
                        )
                    } else {
                        VoucherTotalAmountInfo(
                            modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                            icon = R.drawable.ic_money_voucher,
                            title = stringResource(R.string.sell_crypto_voucher_amount_credited),
                            subTitle = totalCreditedAmount
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
                                text = currentDate,
                                modifier = Modifier.padding(start = 15.dp),
                                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.labelText
                            )
                        }
                        Text(
                            text = currentTime,
                            modifier = Modifier.padding(bottom = 16.dp),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                }
            }
        }
    }
}
