package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsLatest
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditCtaButtons
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditDetail
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditVisa
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.ScheduleAutomaticPayment

/**
 * Composable function to show the option to active accountsmart product
 */
@Composable
fun CreditFooterExpanded(viewModel: ProductViewModel, sharedViewModel: HomeViewModel) {
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
            ScheduleAutomaticPayment(viewModel, sharedViewModel)
            Spacer(modifier = Modifier.height(24.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp),
                color = MultimoneyTheme.colors.dividerWhite16
            )
            CreditVisa(
                uiState = viewModel.uiState,
                balance = viewModel.balanceCredit,
                onNavigateToVisaActivateScreen = {
                    viewModel.onUIEvent(
                        OnNavigateToHomeMultimoneyVisa
                    )
                },
                onCreateMultimoneyVisa = {
                    viewModel.onUIEvent(
                        OnCreateMultimoneyVisa(onLoadingValueChange = {
                            sharedViewModel.onUIEvent(HomeViewModel.UIEvent.OnLoadingValueChanged(it))
                        })
                    )
                },
                isExpanded = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            CreditDetail(
                modifier = Modifier
                    .background(MultimoneyTheme.colors.creditDetailBackground)
                    .wrapContentSize(),
                uiState = viewModel.uiState,
                balance = viewModel.balanceCredit,
                getCreditBalanceLabel = {
                    viewModel.getCreditBalanceLabel(it)
                },
                getQuota = {
                    viewModel.getQuota(it)
                },
                getMinPayment = { viewModel.getMinPayment(it) },
                onShareIbanAccount = { clientLabel: String, accountLabel: String, ibanAccount: String ->
                    viewModel.onUIEvent(
                        OnShareIbanAccount(
                            clientLabel,
                            accountLabel,
                            ibanAccount
                        )
                    )
                }
            )
            CreditMovementsLatest(viewModel)
        }
        // Check if the user can disburse or has payments available
        if (viewModel.uiState.canExpandCredit || viewModel.uiState.paymentAvailable) {
            CreditCtaButtons(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(buttons) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                onClickPay = { viewModel.onUIEvent(OnNavigateToPaymentProcess) },
                onClickDisbursement = { viewModel.onUIEvent(OnNavigateToDisbursement) },
                canDisburse = viewModel.uiState.canExpandCredit,
                paymentAvailable = viewModel.uiState.paymentAvailable
            )
        } else {
            // Create an empty Box just to keep motion layout constraints
            Box(
                modifier = Modifier
                    .constrainAs(buttons) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}
