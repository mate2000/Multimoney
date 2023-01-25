package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnFooterExpandedHeightPxValueChange
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartAccountDetail
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartMovementsLatest
import com.multimoney.multimoney.presentation.util.catalog.ProductType

@Composable
fun SmartFooterExpanded(
    viewModel: ProductViewModel,
    currentPage: Int,
    onLoadingValueChange: (isLoading: Boolean) -> Unit
) {
    val decrement =
        if (viewModel.uiState.productPageList?.any { it.product == ProductType.Credit.value } == true) 1 else 0
    val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: decrement)

    Column(
        Modifier.fillMaxWidth().wrapContentHeight()
//            .onSizeChanged { size ->
//            if(viewModel.uiState.footerExpandedHeightPx != size.height.toFloat()){
//                viewModel.onUIEvent(OnFooterExpandedHeightPxValueChange(size.height.toFloat()))
//            }
//        }
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
