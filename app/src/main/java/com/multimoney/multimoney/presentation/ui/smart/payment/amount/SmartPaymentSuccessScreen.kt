package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@Composable
fun SmartPaymentSuccessScreen(
    viewModel: SavingAmountViewModel = hiltViewModel(),
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

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
            TopNavBar(
                isLeftButtonVisible = false,
                isCenterContentVisible = true,
                onRightButtonClick = {
                    viewModel.onUIEvent(SavingAmountViewModel.UIEvent.OnNavigateHome)
                }
            )
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
                            text = stringResource(R.string.smart_payment_success),
                            modifier = Modifier.padding(top = 16.dp),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.text
                        )
                        CustomButton(
                            onClick = {
                                capturingViewBounds?.let { bounds ->
                                    viewModel.onUIEvent(
                                        SavingAmountViewModel.UIEvent.OnShareVoucherImage(
                                            view,
                                            bounds
                                        )
                                    )
                                }
                            },
                            text = stringResource(R.string.smart_payment_share_button),
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
                            text = stringResource(
                                R.string.smart_payment_you_saved_on_your_smart_account,
                                viewModel.uiState.currency
                            ),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = viewModel.getFormattedAmount(),
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
                        text = stringResource(R.string.smart_payment_from_label),
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )

                    SmartPaymentInfoItem(
                        modifier = Modifier.padding(start = 27.dp, top = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        icon = R.drawable.ic_visa_card_item,
                        iconModifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        title = stringResource(R.string.smart_payment_card_bank_label),
                        subtitle = stringResource(
                            R.string.visa_card_masked_number,
                            viewModel.maskedCardNumber.takeLast(4)
                        )
                    )

                    SmartPaymentInfoItem(
                        modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                        verticalAlignment = Alignment.Top,
                        icon = R.drawable.ic_receipt,
                        iconModifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        title = stringResource(R.string.smart_payment_reference_number_label),
                        subtitle = viewModel.uiState.referenceNumber
                    )

                    if (viewModel.shouldDisplayExchange) {
                        Spacer(modifier = Modifier.height(32.dp))
                        VoucherCurrencyExchangeInfo(
                            leftTitleResource = R.string.payment_amount_bottom_sheet_exchange_type,
                            rightTitleResource = R.string.payment_amount_bottom_sheet_amount_to_debit,
                            exchangeRateText = viewModel.getExchangeRateFormatted(),
                            convertedAmountText = viewModel.getConvertedAmountFormatted()
                        )
                    }
                    SmartPaymentInfoItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 21.dp, top = 32.dp, end = 32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        icon = R.drawable.ic_calendar,
                        iconModifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        title = viewModel.uiState.currentDate,
                        rightSubtitle = viewModel.uiState.currentTime
                    )

                }
            }
        }
        CustomButton(
            onClick = { viewModel.onUIEvent(SavingAmountViewModel.UIEvent.OnNavigateBack) },
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
            buttonType = CustomButtonType.PrimaryPrimary
        )

    }
}