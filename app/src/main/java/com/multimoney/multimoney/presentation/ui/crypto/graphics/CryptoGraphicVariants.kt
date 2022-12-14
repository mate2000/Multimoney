package com.multimoney.multimoney.presentation.ui.crypto.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.crypto.HistoricalBalanceClient

@Composable
fun HomeCryptoGraphic(
    modifier: Modifier = Modifier,
    clientCryptoBalanceHistory: List<HistoricalBalanceClient>,
    graphicColor: Color
) = Canvas(
    modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(vertical = 8.dp)
) {

    val listX = clientCryptoBalanceHistory.map { it.convertedBalance }

    val path = Path()
    val width = size.width
    val height = size.height
    val stepX = width / listX.size
    val stepY = height / listX.maxOf { it }
    val firstPoint = listX.first()
    path.moveTo(0f, (height - firstPoint * stepY).toFloat())
    listX.forEachIndexed { index, historicalBalanceClient ->
        path.lineTo(index * stepX,
            (height - historicalBalanceClient * stepY).toFloat()
        )
    }
    drawPath(
        path = path,
        color = graphicColor,
        style = Stroke(width = 10f, pathEffect = PathEffect.cornerPathEffect(180f))
    )
}
