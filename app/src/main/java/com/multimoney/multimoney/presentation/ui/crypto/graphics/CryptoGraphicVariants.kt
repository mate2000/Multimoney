package com.multimoney.multimoney.presentation.ui.crypto.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.crypto.HistoricalBalanceClient

@Composable
fun HomeCryptoGraphic(
    clientCryptoBalanceHistory: List<HistoricalBalanceClient>,
    graphicColor: Color
) {

    val convertedBalances = clientCryptoBalanceHistory.map { it.convertedBalance }

    CryptoGraphic(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp),
        convertedBalances = convertedBalances,
        graphicColor = graphicColor
    )
}

@Composable
fun WalletCryptoGraphic(
    clientCryptoBalanceHistory: List<HistoricalBalanceClient>,
    graphicColor: Color
) {

    val convertedBalances = clientCryptoBalanceHistory
        .map { it.convertedBalance }.takeLast(MAXIMUM_GRAPHIC_POINTS)

    CryptoGraphic(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 24.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
        fromHome = false,
        convertedBalances = convertedBalances,
        graphicColor = graphicColor
    )
}

@Composable
fun CryptoGraphic(
    modifier: Modifier = Modifier,
    convertedBalances: List<Double>,
    fromHome: Boolean = true,
    graphicColor: Color
) {

    if (convertedBalances.isEmpty()
        || convertedBalances.size == MINIMUM_GRAPHIC_POINTS
        || convertedBalances.all { convertedBalances.firstOrNull() == it }
    ) {

        if (fromHome) {
            EmptyCryptoGraphic(graphicColor = graphicColor)
        } else {
            LargeEmptyCryptoGraphic(graphicColor = graphicColor)
        }
    } else {

        LineGraphic(
            modifier = modifier,
            lineGraphicEntries = convertedBalances,
            graphicColor = graphicColor
        )
    }
}

//show only rect line when the list is empty or only contains one element
@Composable
fun EmptyCryptoGraphic(
    graphicColor: Color
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
                .height(4.dp),
            color = graphicColor
        )
    }
}

@Composable
fun LargeEmptyCryptoGraphic(
    graphicColor: Color
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp)
                .height(4.dp),
            color = graphicColor
        )
    }
}

@Composable
fun LineGraphic(
    modifier: Modifier = Modifier,
    lineGraphicEntries: List<Double>,
    graphicColor: Color
) {

    val spacing = 0f

    val (lowerValue, upperValue) = remember(key1 = lineGraphicEntries) {
        Pair(
            lineGraphicEntries.minOf { it },
            lineGraphicEntries.maxOf { it }
        )
    }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / lineGraphicEntries.size

        var lastX: Float
        val strokePath = Path().apply {
            val height = size.height
            lineGraphicEntries.indices.forEach { i ->
                val info = lineGraphicEntries[i]
                val nextInfo = lineGraphicEntries.getOrNull(i + 1) ?: lineGraphicEntries.last()
                val leftRatio = (info - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()

                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f

                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }

        drawPath(
            path = strokePath,
            color = graphicColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

const val MAXIMUM_GRAPHIC_POINTS = 120
const val MINIMUM_GRAPHIC_POINTS = 1
