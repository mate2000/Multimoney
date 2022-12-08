package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryBlack2
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    userStatus: ValidateUserStatus?,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit
) {

    if (userStatus?.infoBankAccount?.status == SmartAccountStatus.EXIST_IN_CORE.status) {
        CryptoFooterExpandedContent(
            balance,
            userStatus.infoCrypto?.profileEnable,
            actionMarket,
            actionWallet,
            noBalanceAction,
            hasBalanceAction
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    profileEnable: Boolean?,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit
) {

    val smartBalanceAvailable = verifyIfHasSmartBalance(balance?.balanceAccountSmart)
    val hasSmartBalance by remember { mutableStateOf(smartBalanceAvailable) }

    Column {
        ButtonsSection(
            walletEnable = profileEnable ?: false,
            actionMarket = actionMarket,
            actionWallet = actionWallet
        )
        profileEnable?.let {
            if (!it) {
                NoticeSection()
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), color = GrayScale500)
        CryptoActionsSection(
            hasSmartBalance = hasSmartBalance,
            enableCryptoActions = profileEnable ?: false,
            noBalanceAction = noBalanceAction,
            hasBalanceAction = hasBalanceAction
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = GrayScale500)
    }
}

@Composable
fun ButtonsSection(
    actionWallet: () -> Unit,
    actionMarket: () -> Unit,
    walletEnable: Boolean = false
) {

    val buttonColor = ButtonDefaults.buttonColors(
        backgroundColor = GrayScale700,
        disabledBackgroundColor = GrayScale700,
        disabledContentColor = GrayScale700
    )
    val textColor = MultimoneyTheme.colors.labelText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .padding(8.dp)
                .width(164.dp)
                .wrapContentHeight(),
            onClick = actionWallet,
            colors = buttonColor,
            shape = RoundedCornerShape(50),
            enabled = walletEnable,
        ) {
            Text(
                text = stringResource(R.string.crypto_footer_expanded_btn_wallet),
                style = Typography.caption,
                color = textColor
            )
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .width(164.dp)
                .wrapContentHeight(),
            onClick = actionMarket,
            colors = buttonColor,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = stringResource(R.string.crypto_footer_expanded_btn_market),
                style = Typography.caption,
                color = textColor
            )
        }
    }
}

@Composable
fun NoticeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = ComplementaryBlack2
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.ic_crypto_empty_state_notice),
                //tint = GrayScale300,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.crypto_footer_expanded_notice_title),
                modifier = Modifier.padding(bottom = 4.dp),
                style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = stringResource(R.string.crypto_footer_expanded_notice_description),
                modifier = Modifier.padding(bottom = 16.dp),
                style = Typography.subtitle2,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
    }
}

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
                .background(shape = CircleShape, color = Color.Transparent)
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
                    .background(shape = CircleShape, color = Color.Transparent),
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

fun verifyIfHasSmartBalance(balanceAccountSmart: List<Account?>?): Boolean {

    if (balanceAccountSmart.isNullOrEmpty()) {
        return false
    }
    val balances = balanceAccountSmart.map { it?.totalBalance ?: 0.0 }
    return balances.sum() > 0.0
}
