package com.multimoney.multimoney.presentation.ui.test.motionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
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
    Box(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        MotionLayoutMM()
    }
}

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
@Composable
fun MotionLayoutMM() {
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

    val swipeAbleState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, TOTAL_PERCENTAGE to 1)

    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = (swipeAbleState.offset.value / TOTAL_PERCENTAGE),
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
                .layoutId("main_card")
                .swipeable(
                    state = swipeAbleState,
                    anchors = anchors,
                    thresholds = { _, _ ->
                        // Entre mas se aproxima a 1 se tiene que hacer mas scroll para que se autocomplete la animacion
                        FractionalThreshold(0.8f)
                    },
                    orientation = Orientation.Vertical
                ),
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

const val TOTAL_PERCENTAGE = 100F
