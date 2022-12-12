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

//todo setting up all graphics components
@Composable
fun HomeCryptoGraphic(
    clientCryptoBalanceHistory: List<HistoricalBalanceClient>
) {

    val historicalBalanceXLine = clientCryptoBalanceHistory.mapTo(arrayListOf()) { it.convertedBalance }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(150.dp)
    ) {
        val width = size.width.toDouble()
        val height = size.height.toDouble()
        val step = width / historicalBalanceXLine.size
        val max = historicalBalanceXLine.maxOrNull() ?: 0.0
        val min = historicalBalanceXLine.minOrNull() ?: 0.0
        val stepY = height / (max - min)
        val path = Path()
        path.moveTo(0f, (height - (historicalBalanceXLine[0] - min) * stepY).toFloat())
        for (i in 1 until historicalBalanceXLine.size) {
            path.lineTo((i * step).toFloat(), (height - (historicalBalanceXLine[i] - min) * stepY).toFloat())
        }
        drawPath(
            path = path,
            color = Color.Green,
            style = Stroke(
                width = 10f,
                pathEffect = PathEffect.cornerPathEffect(180.0f)
            )
        )
    }
}