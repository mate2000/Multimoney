package com.multimoney.multimoney.presentation.ui.crypto.transferin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.CustomButton

@Composable
fun LimitExceededDialog(onButtonClick : () -> Unit) {
    LimitExceededDialogContent(onButtonClick = onButtonClick)
}

@Preview
@Composable
fun LimitExceededDialogContent(onButtonClick : () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(modifier = Modifier.size(130.dp),painter = painterResource(id = R.drawable.lock_box), contentDescription = null)
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(id = R.string.amount_exceeded_dialog_title),
                style = MaterialTheme.typography.h5.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.text
                )
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(id = R.string.amount_exceeded_dialog_message),
                style = MaterialTheme.typography.body1.copy(
                    color = MultimoneyTheme.colors.text,
                ),
                textAlign = TextAlign.Center
            )
        }
        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 16.dp, vertical = 40.dp),
        onClick = onButtonClick)
    }
}