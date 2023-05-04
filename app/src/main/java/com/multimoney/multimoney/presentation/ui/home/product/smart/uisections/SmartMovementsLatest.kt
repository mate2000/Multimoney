package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartMovements
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementDisplayer
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
fun SmartMovementsLatest(viewModel: ProductViewModel, index: Int) {
    val accountSelected = viewModel.balanceCredit?.balanceAccountSmart?.get(index)
    val moves = viewModel.smartMovementsList.find { account ->
        account.accountToken.toString() == accountSelected?.tokenNumber
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_product_movement_title),
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.text
                )
            )
            if (moves?.result.isNullOrEmpty().not()) {
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.home_product_check_all)),
                    style = Typography.button.copy(
                        color = MultimoneyTheme.colors.textLink
                    ),
                    onClick = { viewModel.onUIEvent(OnNavigateToSmartMovements(moves?.accountToken.toString())) }
                )
            }
        }
        if(moves?.result.isNullOrEmpty()) {
            SmartMovementsEmptyState()
        } else {
            moves?.result?.forEach { move ->
                SmartMovementDisplayer(move)
            }
        }
    }
}

@Composable
@Preview
fun SmartMovementsEmptyState() {
    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MultimoneyTheme.colors.creditDetailBackground)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CustomImage(
                drawableResource = R.drawable.ic_empty_state_movements
            )
            Text(
                text = stringResource(id = R.string.smart_movements_empty_state_title),
                modifier = Modifier.padding(vertical = 10.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.smart_movements_empty_state_description),
                style = Typography.caption,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
