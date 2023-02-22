package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencyMovementItem
import com.multimoney.multimoney.presentation.util.MAX_CRYPTO_ITEMS

@Composable
fun CryptoMovementsSection(
    onShowAllClick: () -> Unit,
    onNavigateToReleaseTransaction: (CryptoCurrencyMovement?) -> Unit,
    cryptoMovements: LazyPagingItems<CryptoCurrencyMovement>
) {

    if (cryptoMovements.itemCount != EMPTY_PAGING_DATA) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    text = stringResource(id = R.string.crypto_movements),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                TextButton(
                    onClick = onShowAllClick
                ) {
                    Text(
                        textAlign = TextAlign.End,
                        text = stringResource(id = R.string.crypto_movements_see_all),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.textLink
                    )
                }
            }
            cryptoMovements.itemSnapshotList.items.forEachIndexed { index, cryptoCurrencyMovement ->
                if (index >= MAX_CRYPTO_ITEMS) return@forEachIndexed
                CryptoCurrencyMovementItem(
                    cryptoCurrencyMovement = cryptoCurrencyMovement,
                    onReleaseTransactionClick = onNavigateToReleaseTransaction,
                )
            }
        }
    }
}

const val EMPTY_PAGING_DATA = 0