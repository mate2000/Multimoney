package com.multimoney.multimoney.presentation.ui.splash

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomLottie
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
    SplashScreen {
        viewModel.popAndNavigateTo(
            route = Screen.OnBoardingScreen.route,
            popTo = Screen.SplashScreen.route
        )
    }
}

@Composable
fun SplashScreen(popAndNavigateToScreen: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.backgroundSplash)
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_splash_top,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top)
                .weight(1f)
        )
        CustomLottie(resource = R.raw.placeholder_splash, Modifier.weight(4f)) {
            popAndNavigateToScreen()
        }
        CustomImage(
            drawableResource = R.drawable.ic_splash_bottom,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Bottom)
                .weight(1f)
        )
    }
}