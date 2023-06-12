package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun MaintenanceSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = MultimoneyTheme.colors.homeCryptoNoticeSectionBackGround
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                modifier = Modifier.padding(6.dp),
                painter = painterResource(id = R.drawable.ic_maintenance_state),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.home_maintenance_state_title),
                modifier = Modifier.padding(bottom = 0.dp),
                style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = stringResource(R.string.home_maintenance_state_description),
                modifier = Modifier.padding(bottom = 0.dp),
                style = Typography.subtitle2,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
    }
}