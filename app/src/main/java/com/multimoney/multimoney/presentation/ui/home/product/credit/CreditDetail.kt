package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency90
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout
import com.multimoney.multimoney.presentation.util.sendAccount

@Composable
fun CreditDetail(balance: Balance?, modifier: Modifier) {
    ExpandableSectionLayout(
        title = "prueba", modifier = modifier.then(Modifier.background(BlackTransparency90))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_max_credit),
                value = balance?.balanceCredit?.firstOrNull()?.creditLimit
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_balance),
                value = balance?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.currentBalanceLabel
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_fee),
                value = balance?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.monthlyQuotaLabel,
                hasDotIndicator = true
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_min_payment),
                value = balance?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.minPaymentLabel,
                hasDotIndicator = true
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_overdue_fee),
                value = balance?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.expiredPayment.toString()
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_iban),
                value = balance?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.ibanAccount,
                hasShare = true
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_max_term),
                value = balance?.balanceCredit?.firstOrNull()?.term,
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_number),
                value = balance?.balanceCredit?.firstOrNull()?.creditNumber
            )
        }
    }
}


@Composable
fun CreditDetailItem(
    label: String,
    value: String?,
    hasDotIndicator: Boolean = false,
    hasShare: Boolean = false,
    icon: ImageVector = Icons.Outlined.Share
) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MultimoneyTheme.colors.text,
            fontWeight = FontWeight.Normal
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (hasDotIndicator) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    tint = Primary400,
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = value ?: "",
                fontSize = 14.sp,
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
            if (hasShare) {
                Icon(
                    imageVector = icon,
                    tint = Primary400,
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            context.sendAccount("Paul Romero", "005596845443")
                        }
                        .padding(start = 16.dp)
                )
            }
        }
    }
}
