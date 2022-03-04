package com.multimoney.multimoney.presentation.ui.test

import androidx.compose.foundation.layout.Box
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
import kotlinx.coroutines.flow.collect

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

    TestScreen(viewModel.data)
    ChartButton { viewModel.navigateToChart() }
}

@Composable
fun ChartButton(navigateToChart: () -> Unit) {
    Button(onClick = navigateToChart, content = {
        Text(text = "Chart")
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