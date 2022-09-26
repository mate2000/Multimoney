package com.multimoney.multimoney.presentation.ui.splash

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.uielement.CustomLottie
import com.multimoney.multimoney.presentation.uielement.LockScreenOrientation
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
fun SplashScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel(),
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }
    SplashScreen {
        viewModel.onUIEvent(OnNavigateToNextScreen)
    }
}

@Composable
fun SplashScreen(navigateToNextScreen: () -> Unit) {
    FireBaseEvents.Splash.logEvent(LocalContext.current)
    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.backgroundSplash),
        verticalArrangement = Arrangement.Center
    ) {
        CustomLottie(resource = R.raw.placeholder_splash) {
            navigateToNextScreen()
        }
    }
}