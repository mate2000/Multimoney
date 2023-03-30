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
import com.multimoney.domain.model.balance.Account
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditDetailItem
import com.multimoney.multimoney.presentation.uielement.ExpandableSectionLayout
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrencySymbolValue
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun SmartAccountDetail(
    modifier: Modifier,
    uiState: UIState,
    account: Account?,
    onShareIbanAccount: (String, String, String) -> Unit
) {
    ExpandableSectionLayout(
        title = stringResource(id = string.smart_account_detail_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            val clientLabel = stringResource(id = string.credit_detail_client)
            var accountLabel = stringResource(string.credit_detail_iban)
            val currencySymbol = account?.currencyCode?.getCurrencySymbol() ?: string.empty
            val currencySymbolValue =
                account?.currencyCode?.getCurrencySymbolValue() ?: string.empty

            if (uiState.idBrand == Brand.CostaRica.id.toString()) {
                CreditDetailItem(
                    label = stringResource(
                        string.smart_account_detail_cr_account_label,
                        stringResource(currencySymbol)
                    ),
                    value = {
                        SmartDetailAccountNumberItem(
                            account?.ibanAccountNumber,
                            clientLabel,
                            accountLabel,
                            onShareIbanAccount
                        )
                    }
                )
            } else if (uiState.idBrand == Brand.ElSalvador.id.toString()) {
                accountLabel = stringResource(id = string.payment_options_transfer_account)
                CreditDetailItem(
                    label = accountLabel,
                    value = {
                        SmartDetailAccountNumberItem(
                            account?.accountNumber,
                            clientLabel,
                            accountLabel,
                            onShareIbanAccount
                        )
                    }
                )
            }
            CreditDetailItem(
                label = stringResource(id = string.smart_account_detail_gained_interest_label),
                value = {
                    Text(
                        text = stringResource(
                            string.common_percentage_format,
                            account?.interest.toString()
                        ),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            CreditDetailItem(
                label = stringResource(id = string.smart_account_detail_total_balance_label),
                value = {
                    Text(
                        text = account?.gainedInterest?.toCurrencyFormat(
                            stringResource(id = currencySymbolValue.getCurrencySymbol())
                        ) ?: "",
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

/**
 * this will represent the account number for both countries, CR and SV, it will
 * have the exact same share action, the only different thing is the accountNumber.
 * @param accountNumber the accountNumber value
 * @param clientLabel will be the same for both.
 * @param accountLabel different for CR or SV
 * @param onShareIbanAccount the share action that will show a modal with the accountNumber.
 */
@Composable
fun SmartDetailAccountNumberItem(
    accountNumber: String?,
    clientLabel: String,
    accountLabel: String,
    onShareIbanAccount: (String, String, String) -> Unit
) {
    Row {
        Text(
            text = accountNumber.orEmpty(),
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        accountNumber?.let {
            Icon(
                imageVector = Icons.Outlined.Share,
                tint = MultimoneyTheme.colors.arrowColor,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        if (it.isNotEmpty()) {
                            onShareIbanAccount(clientLabel, accountLabel, it)
                        }
                    }
                    .padding(start = 16.dp)
            )
        }
    }
}
