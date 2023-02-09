package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.ui.ReactActivity
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun AddCardVDScreen(
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: AddCardVDViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
    }

    AddCardVDScreen()
}

@Composable
@Preview
fun AddCardVDScreen() {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        //startActivity(Intent(this, ReactActivity::class.java))
        ComposeView(context).apply {
            //startActivity(context.Intent(this, ReactActivity::class.java))
            startActivity(context, Intent(context, ReactActivity::class.java), null)
        }
    })
}