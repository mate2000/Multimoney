package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartAccountDetail
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartMovementsLatest

@Composable
fun SmartFooterExpanded(
    viewModel: ProductViewModel,
    index: Int
) {
    Column(
        Modifier.padding(top = 16.dp).fillMaxWidth().wrapContentHeight()
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
}
