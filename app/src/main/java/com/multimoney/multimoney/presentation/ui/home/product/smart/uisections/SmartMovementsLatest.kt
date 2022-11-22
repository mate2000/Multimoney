package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate

@Composable
fun SmartMovementsLatest(viewModel: ProductViewModel) {
    val moves = viewModel.uiState.smartMovementsList ?: emptyList()
    var currencySymbol = 0

    Column(
        Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_product_movement_title),
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.text
                )
            )
            Text(
                text = stringResource(R.string.home_product_check_all),
                style = Typography.button.copy(
                    color = MultimoneyTheme.colors.textLink
                )
            )
        }
        moves.forEach {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = SpaceBetween
            ) {
                if (it.currencyDescription == API_COLONES) {
                    currencySymbol = Brand.CostaRica.id.getCurrencySymbol()
                } else if (it.currencyDescription == API_DOLARES) {
                    currencySymbol = Brand.ElSalvador.id.getCurrencySymbol()
                }

                Column(Modifier.weight(2f)) {
                    Text(
                        text = it.transactionCatalogueDescription,
                        style = Typography.subtitle2.copy(
                            color = MultimoneyTheme.colors.labelText
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = parseApiDateToCardDate(it.creationDate),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.textSubhead
                        ),
                        maxLines = 1
                    )
                }
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (it.amount > 0) {
                        Icon(painter = painterResource(R.drawable.ic_plus), contentDescription = "")
                    } else if (it.amount < 0) {
                        Icon(painter = painterResource(R.drawable.ic_alert), contentDescription = "")
                    }
                    Text(
                        text = stringResource(currencySymbol) + it.amount.toString(),
                        style = Typography.subtitle1.copy(
                            textAlign = TextAlign.End,
                            color = MultimoneyTheme.colors.labelText,
                            fontWeight = FontWeight.W600
                        ),
                        maxLines = 1
                    )
                }
            }
            Divider(color = MultimoneyTheme.colors.dividerWhite30)
        }
    }
}

const val API_DOLARES = "DOLARES"
const val API_COLONES = "COLONES"
