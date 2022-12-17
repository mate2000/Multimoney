package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun InfoPaymentDateItem(
    currentDate: String,
    currentTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                painter = painterResource(drawable.ic_calendar_voucher),
                tint = MultimoneyTheme.colors.iconTintVoucher,
                contentDescription = ""
            )
            Text(
                text = currentDate,
                modifier = Modifier.padding(start = 14.dp),
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
        }
        Text(
            text = currentTime,
            modifier = Modifier.padding(bottom = 16.dp),
            style = Typography.body2,
            color = MultimoneyTheme.colors.labelText
        )
    }
}

@Composable
fun InfoReferenceNumberItem(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    tintIcon: Color = Color.Transparent,
    title: String,
    subTitle: String
) {
    Row(modifier = modifier) {
        icon?.let {
            Icon(painter = painterResource(id = it), contentDescription = "", tint = tintIcon)
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = MultimoneyTheme.colors.labelText
            )
        }
    }
}

@Composable
fun InfoPaymentSourceItem(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    tintIcon: Color = Color.Transparent,
    title: String,
    subTitle: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "",
                tint = tintIcon,
                modifier = Modifier.height(24.dp).width(24.dp).alpha(0.4f)
            )
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = title,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = subTitle,
                style = Typography.body2,
                color = MultimoneyTheme.colors.labelText
            )
        }
    }
}
