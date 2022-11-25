package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditDetailItem
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrencySymbolValue

@Composable
fun SmartAccountDetail(
    modifier: Modifier,
    uiState: UIState,
    balance: Balance?,
    onShareIbanAccount: (String, String, String) -> Unit
) {
    ExpandableSectionLayout(
        title = stringResource(id = string.smart_account_detail_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            val currency = balance?.balanceAccountSmart?.firstOrNull()?.currencyCode
            val clientLabel = stringResource(id = string.credit_detail_client)
            var accountLabel = stringResource(string.credit_detail_iban)

            if (uiState.idBrand == Brand.CostaRica.id.toString()) {

                CreditDetailItem(
                    label = stringResource(
                        string.smart_account_detail_cr_account_label,
                        stringResource(currency?.getCurrencySymbol() ?: 0)
                    ),
                    value = {
                        Row {
                            Text(
                                text = balance?.getFirstAccountSmart()?.ibanAccountNumber.orEmpty(),
                                style = Typography.body2.copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            balance?.balanceAccountSmart?.firstOrNull()?.ibanAccountNumber?.let {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    tint = MultimoneyTheme.colors.arrowColor,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clickable {
                                            if (it.isNotEmpty()) {
                                                onShareIbanAccount(
                                                    clientLabel,
                                                    accountLabel,
                                                    it
                                                )
                                            }
                                        }
                                        .padding(start = 16.dp)
                                )
                            }
                        }
                    })
            } else if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
                accountLabel = stringResource(id = string.payment_options_transfer_account)

                CreditDetailItem(
                    label = stringResource(
                        string.smart_account_detail_sv_account_label,
                        currency?.getCurrencySymbol()?.let { stringResource(it) } ?: ""
                    ),
                    value = {
                        Row {
                            Text(
                                text = balance?.getFirstAccountSmart()?.accountNumber.orEmpty(),
                                style = Typography.body2.copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            balance?.balanceAccountSmart?.firstOrNull()?.accountNumber?.let {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    tint = MultimoneyTheme.colors.arrowColor,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clickable {
                                            if (it.isNotEmpty()) {
                                                onShareIbanAccount(
                                                    clientLabel,
                                                    accountLabel,
                                                    it
                                                )
                                            }
                                        }
                                        .padding(start = 16.dp)
                                )
                            }
                        }
                    })
            }
            CreditDetailItem(
                label = stringResource(id = string.smart_account_detail_gained_interest_label),
                value = {
                    Text(
                        text = stringResource(string.smart_account_detail_gained_interest_mock),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                })
            CreditDetailItem(
                label = stringResource(id = string.smart_account_detail_total_balance_label),
                value = {
                    Text(
                        text = balance?.getFirstAccountSmart()?.currencyCode?.getCurrencySymbolValue()
                            ?.let {
                                stringResource(
                                    it,
                                    balance.getFirstAccountSmart()?.totalBalance.toString()
                                )
                            } ?: "",
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                })
        }
    }
}
