package com.multimoney.multimoney.presentation.ui.test.chart

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ChartScreen(
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: ChartViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
    }

    ChartScreen()
}

/**
 * Defining the compose screen without viewModel,
 * passing the data directly as parameters makes
 * ui testing easier
 */
@Composable
@Preview
fun ChartScreen() {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        CandleStickChart(context).apply {
            setBackgroundColor(Primary500.hashCode())
            description.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(true)

            // Set Candle entry
            val values = arrayListOf<CandleEntry>()
            for (i in 1..100) {

                val value = (Math.random() * 40).toFloat()
                val high = (Math.random() * 9 + 8).toFloat()
                val low = (Math.random() * 9 + 8).toFloat()
                val open = (Math.random() * 6).toFloat() + 1f
                val close = (Math.random() * 6).toFloat() + 1f
                val even = i % 2 == 0

                values.add(
                    CandleEntry(
                        i.toFloat(), value + high,
                        value - low,
                        if (even) value + open else value - open,
                        if (even) value - close else value + close//,
                        //resources.getDrawable(R.drawable.star)
                    )
                )
            }
            val set1 = CandleDataSet(values, "GetHistoricalClientBalance Set")
            set1.setDrawIcons(false)
            set1.axisDependency = AxisDependency.LEFT
//        set1.setColor(Color.rgb(80, 80, 80));
            //        set1.setColor(Color.rgb(80, 80, 80));
            set1.shadowColor = Color.DKGRAY
            set1.shadowWidth = 0.7f
            set1.decreasingColor = Color.RED
            set1.decreasingPaintStyle = Paint.Style.FILL
            set1.increasingColor = Color.rgb(122, 242, 84)
            set1.increasingPaintStyle = Paint.Style.STROKE
            set1.neutralColor = Color.BLUE

            //set1.setHighlightLineWidth(1f);
            val data = CandleData(set1)

            setData(data)
            invalidate()
        }
    })
}