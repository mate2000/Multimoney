package com.multimoney.multimoney.presentation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.CustomLottie
import com.multimoney.multimoney.presentation.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun SplashScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    SplashScreen {
        viewModel.navigateToChart()
    }
}

@Composable
fun SplashScreen(navigateToLogin: () -> Unit) {
    Column(
        Modifier
            .background(Color.White)
            .fillMaxHeight()
    ) {
        Image(
            painterResource(R.drawable.ic_splash_top),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(Alignment.Top)
        )
        CustomLottie(resource = R.raw.placeholder_splash, Modifier.weight(4f)) {
            navigateToLogin()
        }
        Image(
            painterResource(R.drawable.ic_splash_bottom),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(Alignment.Bottom)
        )
    }
}