package com.multimoney.multimoney.presentation.ui.credit.movements

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditMovementsScreen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementDisplayer

@Composable
fun CreditMovementsLatest(viewModel: ProductViewModel, index: Int = 0) {
    val accountSelected = viewModel.balanceCredit?.balanceAccountSmart?.get(index)
    val moves = SmartMovementsResult(0, listOf(), 0)/*viewModel.smartMovementsList.find { account ->
        account.accountToken.toString() == accountSelected?.tokenNumber
    }*/

    Column(
        Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_product_movement_title),
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.text
                )
            )
            ClickableText(
                text = AnnotatedString(stringResource(R.string.home_product_check_all)),
                style = Typography.button.copy(
                    color = MultimoneyTheme.colors.textLink
                ),
                onClick = { viewModel.onUIEvent(OnNavigateToCreditMovementsScreen) }
            )
        }
        /*moves.result.forEach { move ->
            SmartMovementDisplayer(move)
        }*/
    }

    /*moves.result.isNotEmpty().let { result ->
        if (result) {

        } else {
            // todo: Show no movements ui
        }
    }*/
}
