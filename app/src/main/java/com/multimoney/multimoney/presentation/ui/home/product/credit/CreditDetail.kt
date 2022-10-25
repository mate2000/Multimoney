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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout

@Composable
fun CreditDetail(modifier: Modifier, viewModel: ProductViewModel) {
    ExpandableSectionLayout(
        title = stringResource(id = R.string.credit_detail_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            CreditDetailItem(
                label = stringResource(id = R.string.credit_detail_max_credit),
                value = {
                    Text(
                        text = viewModel.balanceCredit?.getFirstCredit()?.creditLimitLabel ?: "",
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
                        text = viewModel.getCreditBalanceLabel(viewModel.balanceCredit?.balanceCredit),
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
                        viewModel.balanceCredit?.getFirstSummary()?.monthlyQuotaLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if ((viewModel.balanceCredit?.getExpiredDays() ?: 0) > 0
                                ) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.dotIndicatorColor,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = viewModel.getQuota(viewModel.balanceCredit?.balanceCredit),
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
                        viewModel.balanceCredit?.getFirstSummary()?.minPaymentLabel?.let {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                tint = if ((viewModel.balanceCredit?.getExpiredDays() ?: 0) > 0) {
                                    MultimoneyTheme.colors.dotIndicatorExpired
                                } else {
                                    MultimoneyTheme.colors.dotIndicatorColor
                                },
                                contentDescription = "",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = viewModel.getMinPayment(viewModel.balanceCredit?.balanceCredit),
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
                        text = viewModel.balanceCredit?.getFirstSummary()?.expiredPayment?.toString()
                            ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            if (viewModel.uiState.idBrand == Brand.CostaRica.id.toString()) {
                val clientLabel = stringResource(id = R.string.credit_detail_client)
                val accountLabel = stringResource(id = R.string.credit_detail_iban_number)
                CreditDetailItem(
                    label = stringResource(id = R.string.credit_detail_iban),
                    value = {
                        Row {
                            Text(
                                text = viewModel.balanceCredit?.getFirstSummary()?.ibanAccount
                                    ?: "",
                                style = Typography.body2.copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            viewModel.balanceCredit?.getFirstSummary()?.ibanAccount?.let {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    tint = MultimoneyTheme.colors.arrowColor,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clickable {
                                            if (it.isNotEmpty()) {
                                                viewModel.onUIEvent(
                                                    ProductViewModel.UIEvent.OnShareIbanAccount(
                                                        clientLabel,
                                                        accountLabel,
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
                        text = viewModel.balanceCredit?.getFirstCredit()?.term ?: "",
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
                        text = viewModel.balanceCredit?.getFirstCredit()?.creditNumber ?: "",
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
    value: @Composable () -> Unit
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
                color = MultimoneyTheme.colors.text
            )
        )
        value()
    }
}
