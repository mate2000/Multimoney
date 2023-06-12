package com.multimoney.multimoney.presentation.uielement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CustomLottie(resource: Int, modifier: Modifier = Modifier, onLottieComplete: () -> Unit) {
    val splashLottie by rememberLottieComposition(LottieCompositionSpec.RawRes(resource))

    val progress by animateLottieCompositionAsState(splashLottie, restartOnPlay = false)

    LottieAnimation(splashLottie, progress, modifier = modifier)
    if (progress == PROGRESS_COMPLETED)
        LaunchedEffect(true) {
            onLottieComplete()
        }
}

const val PROGRESS_COMPLETED = 1.0f