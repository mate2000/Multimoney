package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout

@Composable
fun CreditDetail(
    modifier: Modifier,
    uiState: UIState,
    balance: Balance?,
    getCreditBalanceLabel: (List<BalanceCredit?>?) -> String,
    getQuota: (List<BalanceCredit?>?) -> String,
    getMinPayment: (List<BalanceCredit?>?) -> String,
    onShareIbanAccount: (String, String, String) -> Unit
) {
    ExpandableSectionLayout(
        title = stringResource(id = R.string.credit_detail_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            CreditDetailItem(label = stringResource(id = string.credit_detail_max_credit), value = {
                Text(
                    text = balance?.getFirstCredit()?.creditLimitLabel ?: "",
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            })
            CreditDetailItem(label = stringResource(id = string.credit_detail_balance), value = {
                Text(
                    text = getCreditBalanceLabel(balance?.balanceCredit),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            })

            CreditDetailItem(label = stringResource(id = string.credit_detail_fee), value = {
                Row {
                    balance?.getFirstSummary()?.monthlyQuotaLabel?.let {
                        Icon(
                            imageVector = Filled.Circle,
                            tint = if ((balance.getExpiredDays()) > 0) MultimoneyTheme.colors.dotIndicatorExpired else MultimoneyTheme.colors.dotIndicatorColor,
                            contentDescription = "",
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = getQuota(balance?.balanceCredit),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            })
            CreditDetailItem(label = stringResource(id = string.credit_detail_min_payment), value = {
                Row {
                    balance?.getFirstSummary()?.minPaymentLabel?.let {
                        Icon(
                            imageVector = Filled.Circle,
                            tint = if ((balance.getExpiredDays()) > 0) {
                                MultimoneyTheme.colors.dotIndicatorExpired
                            } else {
                                MultimoneyTheme.colors.dotIndicatorColor
                            },
                            contentDescription = "",
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = getMinPayment(balance?.balanceCredit),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            })
            CreditDetailItem(label = stringResource(id = string.credit_detail_overdue_fee), value = {
                Text(
                    text = balance?.getFirstSummary()?.expiredPayment?.toString() ?: "",
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            })
            if (uiState.idBrand == Brand.CostaRica.id.toString()) {
                val clientLabel = stringResource(id = R.string.credit_detail_client)
                val accountLabel = stringResource(id = R.string.credit_detail_iban_number)
                CreditDetailItem(label = stringResource(id = string.credit_detail_iban), value = {
                    Row {
                        Text(
                            text = balance?.getFirstSummary()?.ibanAccount ?: "",
                            style = Typography.body2.copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        balance?.getFirstSummary()?.ibanAccount?.let {
                            Icon(
                                imageVector = Outlined.Share,
                                tint = MultimoneyTheme.colors.arrowColor,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    if (it.isNotEmpty()) {
                                        onShareIbanAccount(
                                            clientLabel,
                                            accountLabel,
                                            it
                                        )
                                    }
                                }.padding(start = 16.dp)
                            )
                        }
                    }
                })
            }
            CreditDetailItem(label = stringResource(id = string.credit_detail_max_term), value = {
                Text(
                    text = balance?.getFirstCredit()?.term ?: "",
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            })
            CreditDetailItem(label = stringResource(id = string.credit_detail_number), value = {
                Text(
                    text = balance?.getFirstCredit()?.creditNumber ?: "",
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            })
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
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
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
