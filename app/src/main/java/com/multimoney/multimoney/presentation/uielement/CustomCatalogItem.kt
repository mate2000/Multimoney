package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryGray5
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
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
        modifier = modifier.clickable { onClick() },
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
        backgroundColor = ComplementaryGray5,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(GradientGrey1, GradientGrey2)
            ),
        )
    ) {

        val labelColor: Color = if (isSystemInDarkTheme()) {
            WhiteTransparency60
        } else {
            WhiteTransparency60
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 8.dp)
        ) {
            Image(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(0.41f),
                painter = painterResource(iconId.getIconDrawableById()),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter,
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(0.59f),
                text = label,
                textAlign = TextAlign.Center,
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = labelColor,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview
@Composable
fun CustomCatalogItemPreview() {
    CustomCatalogItem(
        modifier = Modifier
            .defaultMinSize(minHeight = 140.dp)
            .height(140.dp),
        iconId = R.drawable.ic_freelancer,
        label = "Otro"
    )
}

/**
 * cast the specific Economic Activity (source of income flow)
 * for each country SV and CR.
 * @param iconId to determine the iconType
 */
sealed class IconType(val iconId: Int) {
    object Salaried : IconType(1)
    object FreeLancer : IconType(2)
    object OwnBusiness : IconType(3)
    object Retired : IconType(4)
    object FormalSalaried : IconType(6)
    object OwnBusinessOnPersonalBasis : IconType(7)
    object OwnBusinessInPartnership : IconType(8)
    object Other : IconType(5 or 9)
}
