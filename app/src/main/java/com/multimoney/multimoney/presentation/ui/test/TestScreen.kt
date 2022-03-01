package com.multimoney.multimoney.presentation.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.multimoney.presentation.util.UiEvent

@Composable
fun TestScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: TestViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getLaunchList()
    }

    TestScreen(viewModel.data)
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