package com.multimoney.multimoney.presentation.ui.home.product.credit

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout
import com.multimoney.multimoney.presentation.util.sendAccount

@Composable
fun CreditDetail(balance: Balance?, modifier: Modifier, userName: String, idBrand: String) {
    ExpandableSectionLayout(
        title = stringResource(id = R.string.credit_detail_title), modifier = modifier
    ) {
        val context = LocalContext.current
        val credit = balance?.balanceCredit?.firstOrNull()
        val summary = credit?.summary?.firstOrNull()
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_max_credit),
                value = {
                    val creditLimit = credit?.creditLimit
                    Text(
                        text = creditLimit ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_balance),
                value = {
                    val currentBalanceLabel = summary?.currentBalanceLabel
                    Text(
                        text = currentBalanceLabel ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_fee),
                value = {
                    val expiredDays = summary?.expiredDays ?: 0
                    Row {
                        val monthlyQuotaLabel = summary?.monthlyQuotaLabel ?: ""
                        summary?.monthlyQuotaLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if (expiredDays > 0) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.dotIndicatorColor,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = monthlyQuotaLabel,
                            style = Typography.body2.copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_min_payment),
                value = {
                    val expiredDays = summary?.expiredDays ?: 0
                    Row {
                        val minPaymentLabel = summary?.minPaymentLabel ?: ""
                        summary?.minPaymentLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if (expiredDays > 0) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.dotIndicatorColor,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = minPaymentLabel,
                            style = Typography.body2.copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_overdue_fee),
                value = {
                    val expiredPayment = summary?.expiredPayment
                    Text(
                        text = expiredPayment?.toString() ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            if (idBrand == Brand.CostaRica.id.toString()) {
                CreditDetailItem(
                    label = stringResource(id = R.string.credit_detail_iban),
                    value = {
                        val ibanAccount = summary?.ibanAccount ?: ""
                        Row {
                            Text(
                                text = ibanAccount,
                                style = Typography.body2.copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            summary?.ibanAccount?.let {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    tint = Primary400,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clickable {
                                            if (ibanAccount.isNotEmpty()) {
                                                context.sendAccount(userName, ibanAccount)
                                            }
                                        }
                                        .padding(start = 16.dp)
                                )
                            }
                        }
                    }
                )
            }
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_max_term),
                value = {
                    val term = credit?.term
                    Text(
                        text = term ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_number),
                value = {
                    val creditNumber = credit?.creditNumber
                    Text(
                        text = creditNumber ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
        }
    }
}


@Composable
fun CreditDetailItem(
    label: String,
    value: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
            )
        )
        value()
    }
}
