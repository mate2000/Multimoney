package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.ShadowColorComplementaryOne
import com.multimoney.multimoney.presentation.theme.ShadowColorComplementaryTwo
import com.multimoney.multimoney.presentation.theme.ShadowColorPrimary
import com.multimoney.multimoney.presentation.theme.ShadowColorSecondary
import com.multimoney.multimoney.presentation.theme.ShadowColorTertiary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.ComplementaryTwo
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.ComplementaryOne
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Secondary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Tertiary
import com.multimoney.multimoney.presentation.util.coloredShadow

const val SIXTY_PERCENT = 0.90
@Preview
@Composable
fun CustomProductBackground(
    modifier: Modifier = Modifier,
    type: ProductBackGroundType = Primary,
    content: @Composable () -> Unit = {}
) {

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val startOffset = screenWidth.value * SIXTY_PERCENT

    val shadowCardColor: Color
    val cardResourceId: Int
    when (type) {
        Primary -> {
            shadowCardColor = ShadowColorPrimary
            cardResourceId = drawable.bg_card_credit
        }
        Secondary -> {
            shadowCardColor = ShadowColorSecondary
            cardResourceId = drawable.bg_card_smart
        }
        Tertiary -> {
            shadowCardColor = ShadowColorTertiary
            cardResourceId = drawable.bg_card_tertiary
        }
        ComplementaryOne -> {
            shadowCardColor = ShadowColorComplementaryOne
            cardResourceId = drawable.bg_card_complementary_one
        }
        ComplementaryTwo -> {
            shadowCardColor = ShadowColorComplementaryTwo
            cardResourceId = drawable.bg_card_crypto
        }
    }

    Row(
        modifier = Modifier
            .coloredShadow(shadowCardColor)
            .padding(bottom = 16.dp)
    ) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .blur(0.24.dp)

            ) {
                CustomImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    drawableResource = cardResourceId,
                    contentScale = ContentScale.FillBounds
                )
                CustomImage(
                    drawableResource = drawable.ic_swipe_indicator, modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.TopCenter)
                )
                content()
            }
        }
    }
}

sealed class ProductBackGroundType {
    object Primary : ProductBackGroundType()
    object Secondary : ProductBackGroundType()
    object Tertiary : ProductBackGroundType()
    object ComplementaryOne : ProductBackGroundType()
    object ComplementaryTwo : ProductBackGroundType()
}