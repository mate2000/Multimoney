package com.multimoney.multimoney.presentation.ui.splash

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnCallQueryGetConfigurationVersion
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.uielement.LockScreenOrientation
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SplashScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }
    SplashScreen(duration = SplashScreenViewModel.SPLASH_DURATION) {
        viewModel.onUIEvent(OnCallQueryGetConfigurationVersion)
    SplashScreen {
        viewModel.onUIEvent(OnNavigateToNextScreen)
    }
}

@Composable
fun SplashScreen(navigateToNextScreen: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.backgroundSplash),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(key1 = true) {
            navigateToNextScreen()
        }
    }
}
