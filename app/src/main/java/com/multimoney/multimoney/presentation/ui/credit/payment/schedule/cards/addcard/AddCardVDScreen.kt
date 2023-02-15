package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.ui.PackageTrackerModule
import com.multimoney.multimoney.presentation.ui.ReactActivity
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun AddCardVDScreen(
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: AddCardVDViewModel = hiltViewModel()
) {

    val activity = LocalContext.current.findActivity()

    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
        viewModel.onUIEvent(AddCardVDViewModel.UIEvent.InitAddCardFlow(activity))
        /*viewModel.addCardResult.subscribe(object : PackageTrackerModule.Result {
            override fun onCallBackResult(response: String) {
                Toast.makeText(context, "Testing", Toast.LENGTH_LONG).show()
            }
        })*/
    }

    AddCardVDScreen()

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun AddCardVDScreen() {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        //startActivity(Intent(this, ReactActivity::class.java))
        ComposeView(context).apply {
            //startActivity(context.Intent(this, ReactActivity::class.java))
            startActivityForResult(context as Activity, Intent(context, ReactActivity::class.java), 1, null)
        }
    })
}