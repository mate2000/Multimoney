package com.multimoney.multimoney.presentation.ui.splash

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnCallQueryGetConfigurationVersion
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LockScreenOrientation
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.delay

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
    }
}

@Composable
fun SplashScreen(duration: Long, navigateToNextScreen: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.backgroundSplash),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(key1 = true) {
            delay(duration)
            navigateToNextScreen()
        }
        CustomImage(
            modifier = Modifier
                .width(200.dp)
                .height(72.dp),
            drawableResource = R.drawable.ic_logo_multimoney2
        )
    }
}
