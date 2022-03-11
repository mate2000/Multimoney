package com.multimoney.multimoney.presentation.ui.test

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.multimoney.presentation.util.UiEvent
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import kotlinx.coroutines.flow.collect
import timber.log.Timber


@Composable
fun TestScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: TestViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getLaunchList()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    // Display api response data
    TestScreen(viewModel.data)

    // Create start activity result for OnFido
    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onFidoHelper.getOnFidoClient().handleActivityResult(
                result.resultCode,
                result.data,
                object : Onfido.OnfidoResultListener {
                    override fun userCompleted(captures: Captures) {
                        Timber.d("ONFIDO", "Captured")
                    }

                    override fun userExited(exitCode: ExitCode) {
                        Timber.d("ONFIDO", "ExitCode")
                    }

                    override fun onError(exception: OnfidoException) {
                        Timber.d("ONFIDO", "OnfidoException")
                    }
                })

        }

    Column {
        ChartButton {
            viewModel.navigateToChart()
        }
        OnFidoButton {
            launchOnFidoActivityResult.launch(viewModel.onFidoHelper.getOnFidoIntent())
        }
    }
}

@Composable
fun ChartButton(navigateToChart: () -> Unit) {
    Button(onClick = navigateToChart, content = {
        Text(text = "Chart")
    })
}

@Composable
fun OnFidoButton(navigateToChart: () -> Unit) {
    Button(onClick = navigateToChart, content = {
        Text(text = "OnFido")
    })
}

/**
 * Defining the compose screen without viewModel,
 * passing the data directly as parameters makes
 * ui testing easier
 */
@Composable
fun TestScreen(
    data: LaunchConnection?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        data?.let {
            Text(text = data.cursor, color = MaterialTheme.colors.onBackground)
        }
    }
}