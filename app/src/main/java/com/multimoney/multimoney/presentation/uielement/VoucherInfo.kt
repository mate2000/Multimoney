package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency40
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

@Composable
fun VoucherAccountInfo(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    title: String = "",
    subTitle: String = ""
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val titleColor: Color
        val subtitleColor: Color
        val tintIconColor: Color

        if (isSystemInDarkTheme()) {
            titleColor = WhiteTransparency90
            subtitleColor = WhiteTransparency90
            tintIconColor = WhiteTransparency40
        } else {
            titleColor = WhiteTransparency90
            subtitleColor = WhiteTransparency90
            tintIconColor = WhiteTransparency40
        }

        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "",
                tint = tintIconColor,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .alpha(0.4f)
            )
        }
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = subtitleColor
            )
        }
    }
}

@Composable
fun VoucherCryptoAddressInfo(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    title: String = "",
    subTitle: String = ""
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val titleColor: Color
        val subtitleColor: Color
        val tintIconColor: Color

        if (isSystemInDarkTheme()) {
            titleColor = WhiteTransparency90
            subtitleColor = WhiteTransparency90
            tintIconColor = WhiteTransparency40
        } else {
            titleColor = WhiteTransparency90
            subtitleColor = WhiteTransparency90
            tintIconColor = WhiteTransparency40
        }

        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "",
                tint = tintIconColor,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .alpha(0.4f)
            )
        }
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = subTitle,
                style = Typography.body2.copy(fontWeight = FontWeight.W100),
                color = subtitleColor,
            )
        }
    }
}

@Composable
fun VoucherNumberInfo(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    title: String,
    subTitle: String
) {
    val titleColor: Color
    val subtitleColor: Color
    val tintIconColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency90
        tintIconColor = WhiteTransparency40
    } else {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency90
        tintIconColor = WhiteTransparency40
    }

    Row(modifier = modifier) {
        icon?.let {
            Icon(painter = painterResource(id = it), contentDescription = "", tint = tintIconColor)
        }
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = subtitleColor
            )
        }
    }
}


@Composable
fun VoucherTotalAmountInfo(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    title: String,
    subTitle: String
) {
    val titleColor: Color
    val subtitleColor: Color
    val tintIconColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency90
        tintIconColor = WhiteTransparency40
    } else {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency90
        tintIconColor = WhiteTransparency40
    }

    Row(modifier = modifier) {
        icon?.let {
            Icon(painter = painterResource(id = it), contentDescription = "", tint = tintIconColor)
        }
        Column(modifier = Modifier.padding(start = 13.5.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = subtitleColor
            )
        }
    }
}

@Composable
fun VoucherCurrencyExchangeInfo(
    leftTitleResource: Int = R.string.empty,
    rightTitleResource: Int = R.string.empty,
    exchangeRateText: String = "",
    convertedAmountText: String = "",
    displayIcon: Boolean = true,
    textColumnAlign: Alignment.Horizontal = Alignment.Start,
    mainRowAlignment: Arrangement.Horizontal = Arrangement.Start
) {
    val titleColor: Color
    val subtitleColor: Color
    val tintIconColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        subtitleColor = DefaultWhite
        tintIconColor = WhiteTransparency40
    } else {
        titleColor = WhiteTransparency90
        subtitleColor = DefaultWhite
        tintIconColor = WhiteTransparency40
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 27.dp),
        horizontalArrangement = mainRowAlignment
    ) {
        if (displayIcon) {
            Icon(
                painter = painterResource(id = R.drawable.ic_money_gray),
                tint = tintIconColor,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 13.5.dp)
                    .height(24.dp)
                    .width(24.dp)
            )
        }
        Column(
            horizontalAlignment = textColumnAlign
        ) {
            Text(
                text = stringResource(id = leftTitleResource),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = titleColor,
                textAlign = TextAlign.Start
            )
            Text(
                text = exchangeRateText,
                style = Typography.body2,
                color = subtitleColor,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Divider(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.padding(start = 13.5.dp),
            horizontalAlignment = textColumnAlign
        ) {
            Text(
                text = stringResource(id = rightTitleResource),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = titleColor,
                textAlign = TextAlign.Start
            )
            Text(
                text = convertedAmountText,
                style = Typography.body2,
                color = subtitleColor,
                textAlign = TextAlign.Start
            )
        }
    }
}
