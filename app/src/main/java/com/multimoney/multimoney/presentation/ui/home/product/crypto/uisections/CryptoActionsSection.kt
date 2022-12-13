package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun CryptoActionsSection(
    hasSmartBalance: Boolean,
    enableCryptoActions: Boolean = false,
    noBalanceAction: () -> Unit = {},
    hasBalanceAction: () -> Unit = {}
) {

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        item {
            CryptoAction(
                enable = true,
                title = stringResource(id = R.string.crypto_footer_expanded_buy_crypto_label),
                icon = R.drawable.ic_shopping_cart_add,
                action = if (hasSmartBalance) hasBalanceAction else noBalanceAction
            )
        }
        item {
            CryptoAction(
                enable = enableCryptoActions,
                title = stringResource(id = R.string.crypto_footer_expanded_sell_crypto_label),
                icon = R.drawable.ic_tag_sell
            )
        }
        item {
            CryptoAction(
                enable = enableCryptoActions,
                title = stringResource(id = R.string.crypto_footer_expanded_get_crypto_label),
                icon = R.drawable.ic_arrow_get
            )
        }
        item {
            CryptoAction(
                enable = enableCryptoActions,
                title = stringResource(id = R.string.crypto_footer_expanded_send_crypto_label),
                icon = R.drawable.ic_arrow_send
            )
        }
    }
}

@Composable
fun CryptoAction(
    title: String,
    icon: Int,
    enable: Boolean = false,
    action: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .width(72.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(shape = CircleShape, color = MultimoneyTheme.colors.fullTransparency)
                .border(
                    width = 2.dp,
                    color = if (enable) Primary400 else GrayScale400,
                    shape = CircleShape
                )
                .clickable(enabled = enable, onClick = action)
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(16.dp)
                    .background(shape = CircleShape, color = MultimoneyTheme.colors.fullTransparency),
                painter = painterResource(id = icon),
                contentDescription = title
            )
        }
        Text(
            text = title,
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.caption,
            color = if (enable) MultimoneyTheme.colors.labelText else MultimoneyTheme.colors.quickActionLabelColor,
            textAlign = TextAlign.Center,
        )
    }
}