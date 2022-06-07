package com.multimoney.multimoney.presentation.ui.test.motionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun InteractionWithMotionLayout() {
    var progress by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        MotionLayoutMM(progress = progress)
        Slider(
            value = progress,
            onValueChange = {
                progress = it
            },
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun MotionLayoutMM(progress: Float) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val bottomInitialCardHeight = (screenHeight.value * 0.45).dp
    val bottomFinalCardHeight = (screenHeight.value * 0.60).dp
    val context = LocalContext.current
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }
    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = progress,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .layoutId("header_cards"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cards Iniciales", color = Color.White, style = Typography.h4)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Yellow)
                .layoutId("header_title"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Action Bar", color = Color.Black, style = Typography.h4)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .layoutId("main_card"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Card Principal", color = Color.Black, style = Typography.h4)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomInitialCardHeight)
                .background(Color.Red)
                .layoutId("bottom_start"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Contenido Inical", color = Color.White, style = Typography.h4)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomFinalCardHeight)
                .background(Color.Black)
                .layoutId("bottom_end"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cards Final", color = Color.White, style = Typography.h4)
        }
    }
}
