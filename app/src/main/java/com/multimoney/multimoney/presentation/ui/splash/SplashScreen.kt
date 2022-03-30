package com.multimoney.multimoney.presentation.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomLottie
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SplashScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }
    SplashScreen {
        viewModel.popAndNavigateTo(
            route = Screen.LoginScreen.route,
            popTo = Screen.SplashScreen.route
        )
    }
}

@Composable
fun SplashScreen(popAndNavigateToScreen: () -> Unit) {
    Column(
        Modifier
            .background(MultimoneyTheme.colors.backgroundSplash)
            .fillMaxHeight()
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_splash_top,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(Alignment.Top)
        )
        CustomLottie(resource = R.raw.placeholder_splash, Modifier.weight(4f)) {
            popAndNavigateToScreen()
        }
        CustomImage(
            drawableResource = R.drawable.ic_splash_bottom,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(Alignment.Bottom)
        )
    }
}