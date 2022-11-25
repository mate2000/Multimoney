package com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess.ProcessingTransactionViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess.ProcessingTransactionViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.InfoItem
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.InfoItemAccount
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.shape.DottedShape

@Composable
fun ProcessingTransactionScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: ProcessingTransactionViewModel = hiltViewModel()
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(isLeftButtonVisible = false, isCenterContentVisible = true, onRightButtonClick = {
            viewModel.onUIEvent(OnCloseClick)
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
                        width = Dimension.fillToConstraints
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
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                            trailingIcon = drawable.ic_icon_share,
                            buttonType = PrimaryTertiary
                        )
                        Text(
                            text = stringResource(string.origination_voucher_amount),
                            modifier = Modifier.padding(top = 12.dp),
                            style = Typography.body1,
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = viewModel.uiState.infoDeposit?.amount ?: "",
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
                        text = stringResource(string.payment_voucher_from_label),
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )

                    InfoItemAccount(
                        modifier = Modifier.padding(start = 27.dp, top = 24.dp),
                        icon = drawable.ic_bank,
                        tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                        title = stringResource(string.origination_voucher_destiny_account),
                        subTitle = getMaskedAccount(
                            viewModel.uiState.infoDeposit?.accountNumber ?: "",
                            stringResource(id = string.payment_account_masked_text)
                        )
                    )

                    InfoItem(
                        modifier = Modifier.padding(start = 27.dp, top = 32.dp),
                        icon = drawable.ic_receipt,
                        tintIcon = MultimoneyTheme.colors.iconTintVoucher,
                        title = stringResource(string.payment_voucher_reference_number_label),
                        subTitle = viewModel.uiState.infoDeposit?.referenceNumber ?: ""
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 27.dp, top = 34.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(id = drawable.ic_calendar_voucher),
                                tint = MultimoneyTheme.colors.iconTintVoucher,
                                contentDescription = ""
                            )
                            Text(
                                text = viewModel.uiState.infoDeposit?.data ?: "",
                                modifier = Modifier.padding(start = 15.dp),
                                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.labelText
                            )
                        }
                        Text(
                            text = viewModel.uiState.infoDeposit?.data ?: "",
                            modifier = Modifier.padding(bottom = 16.dp),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                }
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    BackHandler {
        viewModel.onUIEvent(OnCloseClick)
    }
}
