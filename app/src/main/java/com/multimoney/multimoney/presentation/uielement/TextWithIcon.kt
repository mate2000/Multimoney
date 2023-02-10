package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

@Composable
@Preview
fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String = "",
    annotatedString: AnnotatedString? = null,
    iconResource: Int = R.drawable.ic_verified
) {
    val textColor = if (isSystemInDarkTheme()) {
        WhiteTransparency90
    } else {
        WhiteTransparency90
    }
    Row(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.padding(top = 2.dp),
            painter = painterResource(id = iconResource),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (annotatedString.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = text,
                style = Typography.body1.copy(color = textColor)
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = annotatedString,
                style = Typography.body1.copy(color = textColor)
            )
        }
    }
}
