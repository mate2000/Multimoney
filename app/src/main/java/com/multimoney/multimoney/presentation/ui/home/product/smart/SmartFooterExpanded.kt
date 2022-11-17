package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditCtaButtons
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartCtaButtons

@Composable
fun SmartFooterExpanded(viewModel: ProductViewModel) {
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (content, buttons) = createRefs()

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

        }

        SmartCtaButtons(
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(buttons) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            onClickPay = { viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToPaymentProcess) },
            onClickDisbursement = { viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToCreditScreen) },
            canDisburse = viewModel.uiState.canExpandCredit
        )
    }

    Text(
        text = "Smart Footer Expanded",
        style = Typography.h6.copy(letterSpacing = 0.38.sp),
        color = MultimoneyTheme.colors.labelText
    )
}
