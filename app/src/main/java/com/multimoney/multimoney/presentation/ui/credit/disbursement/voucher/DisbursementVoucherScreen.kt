package com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher

import android.view.View
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.constraintlayout.compose.Dimension.Companion
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher.DisbursementVoucherViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher.DisbursementVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VoucherAccountInfo
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.VoucherNumberInfo
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@Composable
fun DisbursementVoucherScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: DisbursementVoucherViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    DisbursementVoucherContent(
        onCloseClick = {
            viewModel.onUIEvent(OnCloseClick)
        },
        onShareVoucherImage = { view, capturingBounds ->
            viewModel.onUIEvent(
                OnSharedVoucherImage(
                    view,
                    capturingBounds,
                )
            )
        },
        viewModel.disbursementLabel ?: "",
        viewModel.clientBankAccount?.bankDescription ?: "",
        viewModel.clientBankAccount?.accountNumber ?: "",
        viewModel.reference ?: "",
        viewModel.shouldDisplayExchangeRate ?: false,
        viewModel.exchangeRateLabel.toString(),
        viewModel.amountInCurrencyLabel ?: "",
        viewModel.currentDate,
        viewModel.currentTime
    )

    BackHandler {
        viewModel.onUIEvent(OnCloseClick)
    }
}

@Preview
@Composable
fun DisbursementVoucherContent(
    onCloseClick: () -> Unit = {},
    onShareVoucherImage: (view: View, capturingBounds: Rect) -> Unit = { _, _ -> },
    disbursementLabel: String = "",
    bankDescription: String = "",
    accountNumber: String = "",
    reference: String = "",
    shouldDisplayExchangeRate: Boolean = false,
    exchangeRateLabel: String = "",
    amountInCurrencyLabel: String = "",
    currentDate: String = "",
    currentTime: String = ""
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            TopNavBar(isLeftButtonVisible = false, isCenterContentVisible = true, onRightButtonClick = {
                onCloseClick()
            })
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
                    drawableResource = drawable.bg_confirmation_card,
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
                        },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(string.disbursement_voucher_transaction_success),
                            modifier = Modifier.padding(top = 16.dp),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.text
                        )
                        CustomButton(
                            onClick = {
                                capturingViewBounds?.let { bounds ->
                                    onShareVoucherImage(view, bounds)
                                }
                            },
                            text = stringResource(string.disbursement_voucher_shared_button),
                            modifier = Modifier
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    top = 12.dp,
                                )
                                .fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                            ),
                            trailingIcon = drawable.ic_icon_share,
                            buttonType = CustomButtonType.PrimaryTertiary,
                        )
                        Text(
                            text = stringResource(string.disbursement_voucher_you_have_received),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = disbursementLabel,
                            style = Typography.h4.copy(fontWeight = FontWeight.W600),
                            color = MultimoneyTheme.colors.text,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MultimoneyTheme.colors.dividerWhite16, shape = DottedShape(step = 10.dp))
                    )
                    Text(
                        text = stringResource(string.disbursement_voucher_from_label),
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )

                    VoucherAccountInfo(
                        modifier = Modifier.padding(start = 27.dp, top = 24.dp),
                        icon = drawable.ic_bank,
                        title = bankDescription,
                        subTitle = getMaskedAccount(
                            accountNumber,
                            stringResource(id = string.disbursement_account_masked_text)
                        ),
                    )

                    VoucherNumberInfo(
                        modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                        icon = drawable.ic_receipt,
                        title = stringResource(string.disbursement_voucher_reference_number_label),
                        subTitle = reference
                    )

                    if (shouldDisplayExchangeRate) {
                        Spacer(modifier = Modifier.height(32.dp))
                        VoucherCurrencyExchangeInfo(
                            leftTitleResource = string.disbursement_voucher_exchange_type,
                            rightTitleResource = string.disbursement_voucher_amount_to_disburse,
                            exchangeRateText = exchangeRateLabel,
                            convertedAmountText = amountInCurrencyLabel
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 27.dp, top = 34.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(id = drawable.ic_calendar_voucher),
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
