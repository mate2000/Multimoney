package com.multimoney.multimoney.presentation.ui.credit.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.unit.sp
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditMovementsScreen
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
fun CreditMovementsLatest(viewModel: ProductViewModel) {
    val movesResult = mutableListOf<CreditMovement>()
    viewModel.creditMovements.let { movementResultList ->
        movementResultList.forEach { creditMovementsResult ->
            creditMovementsResult.result?.let {
                if (movesResult.size > MAX_HOME_CREDIT_MOVEMENTS_RECORDS) {
                    return@forEach
                } else {
                    movesResult.addAll(it)
                }
            }
        }
    }

    movesResult.take(MAX_HOME_CREDIT_MOVEMENTS_RECORDS)

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
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (movesResult.isNotEmpty()) {
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.home_product_check_all)),
                    style = Typography.button.copy(
                        color = MultimoneyTheme.colors.textLink,
                        fontSize = 14.sp
                    ),
                    onClick = { viewModel.onUIEvent(OnNavigateToCreditMovementsScreen) }
                )
            }
        }
        if (movesResult.isEmpty()) {
            CreditMovementsEmptyState()
        } else {
            movesResult.forEach {
                CreditMovementItem(it)
            }
        }
    }
}

@Composable
@Preview
fun CreditMovementsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.creditDetailBackground),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        CustomImage(
            drawableResource = R.drawable.ic_empty_state_movements
        )
        Text(
            text = stringResource(id = R.string.credit_movements_empty_state_title),
            modifier = Modifier.padding(vertical = 10.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = stringResource(id = R.string.credit_movements_empty_state_description),
            style = Typography.caption,
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

const val MAX_HOME_CREDIT_MOVEMENTS_RECORDS = 3
