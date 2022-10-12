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
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout

@Composable
fun CreditDetail(balance: Balance?, modifier: Modifier, viewModel: ProductViewModel) {
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
                    Text(
                        text = credit?.creditLimitLabel ?: "",
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
                    Text(
                        text = viewModel.getCreditBalanceLabel(balance?.balanceCredit),
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
                    Row {
                        summary?.monthlyQuotaLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if ((summary.expiredDays
                                        ?: 0) > 0
                                ) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.dotIndicatorColor,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = viewModel.getQuota(balance?.balanceCredit),
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
                    Row {
                        summary?.minPaymentLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if ((summary.expiredDays ?: 0) > 0)
                                    MultimoneyTheme.colors.dotIndicatorExpired
                                else
                                    MultimoneyTheme.colors.dotIndicatorColor,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = viewModel.getMinPayment(balance?.balanceCredit),
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
                    Text(
                        text = summary?.expiredPayment?.toString() ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            if (viewModel.uiState.idBrand == Brand.CostaRica.id.toString()) {
                CreditDetailItem(
                    label = stringResource(id = R.string.credit_detail_iban),
                    value = {
                        Row {
                            Text(
                                text = summary?.ibanAccount ?: "",
                                style = Typography.body2.copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            summary?.ibanAccount?.let {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    tint = MultimoneyTheme.colors.arrowColor,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clickable {
                                            if (it.isNotEmpty()) {
                                                viewModel.onUIEvent(
                                                    ProductViewModel.UIEvent.OnShareIbanAccount(
                                                        context,
                                                        it
                                                    )
                                                )
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
                    Text(
                        text = credit?.term ?: "",
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
                    Text(
                        text =credit?.creditNumber ?: "",
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
