package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryGray5
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.getIconDrawableById

/**
 * This custom view represent a catalog of items, it can be used
 * in any list where an icon and text need to be shown in a card.
 * @param modifier to apply custom style to the view
 * @param iconId intended to show the proper icon
 * @param label the text of the item
 * @param onClick the action that each item will have
 */
@Composable
fun CustomCatalogItem(
    modifier: Modifier = Modifier,
    iconId: Int,
    label: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
        backgroundColor = ComplementaryGray5,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(GradientGrey1, GradientGrey2)
            ),
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 14.dp)
        ) {
            Image(
                modifier = Modifier.height(28.dp),
                painter = painterResource(iconId.getIconDrawableById()),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = 0.24.sp,
                        color = MultimoneyTheme.colors.text,
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomCatalogItemPreview() {
    CustomCatalogItem(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp),
        iconId = R.drawable.ic_freelancer,
        label = "Otro"
    )
}

sealed class IconType(val iconId: Int) {
    object Salaried : IconType(1)
    object FreeLancer : IconType(2)
    object OwnBusiness : IconType(3)
    object Retired : IconType(4)
}
