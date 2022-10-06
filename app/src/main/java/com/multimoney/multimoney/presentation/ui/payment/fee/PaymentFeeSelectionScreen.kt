package com.multimoney.multimoney.presentation.ui.payment.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentFeeSelectionScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentFeeSelectionViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onRightButtonClick = {

            },
            onLeftButtonClick = {

            })
        Column(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.payment_fee_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )

            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                startIcon = R.drawable.ic_payment_colon,
                title = stringResource(id = R.string.payment_fee_one_option) + " Colones",
                subtitle = "₡5,000",
                onClick = {}
            )

            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                startIcon = R.drawable.ic_payment_dollar,
                title = stringResource(id = R.string.payment_fee_one_option) + " Dolares",
                subtitle = "$80",
                onClick = {}
            )

            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                title = stringResource(id = R.string.payment_fee_both_options),
                subtitle = "₡5,000 + $80",
                onClick = {}
            )
        }
    }
}