package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartAccountDetail
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartCtaButtons
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartMovementsLatest

@Composable
fun SmartFooterExpanded(
    viewModel: ProductViewModel,
    currentPage: Int,
    onLoadingValueChange: (isLoading: Boolean) -> Unit
) {
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (content, buttons) = createRefs()
        val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: 1)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .constrainAs(content) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(buttons.top)
                    height = Dimension.fillToConstraints
                }
        ) {
            viewModel.balanceCredit?.balanceAccountSmart?.let {
                if (it.isNotEmpty()) {
                    SmartAccountDetail(
                        modifier = Modifier
                            .background(MultimoneyTheme.colors.creditDetailBackground)
                            .wrapContentSize(),
                        uiState = viewModel.uiState,
                        account = it[index],
                        onShareIbanAccount = { clientLabel: String, accountLabel: String, ibanAccount: String ->
                            viewModel.onUIEvent(
                                ProductViewModel.UIEvent.OnShareIbanAccount(
                                    clientLabel,
                                    accountLabel,
                                    ibanAccount
                                )
                            )
                        }
                    )
                    SmartMovementsLatest(viewModel, index)
                }
            }
        }
        SmartCtaButtons(
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(buttons) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            onClickPay = {
                viewModel.onUIEvent(
                    ProductViewModel.UIEvent.OnNavigateToPaymentSmartFlow(
                        viewModel.balanceCredit?.balanceAccountSmart?.get(index),
                        onLoadingValueChange
                    )
                )
            },
            onClickSendMoney = {
                viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSendMoneyFlow(
                    viewModel.balanceCredit?.balanceAccountSmart?.get(index)
                ))
            },
            canSendMoney = viewModel.canSendMoney(
                viewModel.uiState.productPageList?.get(currentPage)?.productSmartIndex
            )
        )
    }
}
