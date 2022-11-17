package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.google.accompanist.pager.ExperimentalPagerApi
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MotionLayoutMM(
    header: @Composable () -> Unit,
    headerExpanded: @Composable (backFunction: () -> Unit) -> Unit,
    content: @Composable (modifier: Modifier) -> Unit,
    footer: @Composable () -> Unit,
    isExpanded: Boolean = false,
    updateIsExpanded: (Boolean) -> Unit,
    footerExpanded: @Composable () -> Unit
) {
    val context = LocalContext.current
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    val swipeAbleState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, TOTAL_PERCENTAGE to 1)
    var isBackPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = swipeAbleState.offset.value) {
        updateIsExpanded(swipeAbleState.offset.value == TOTAL_PERCENTAGE)
    }

    LaunchedEffect(key1 = isBackPressed) {
        if (isExpanded && isBackPressed) {
            swipeAbleState.animateTo(BEGINNING_ANIMATION)
            isBackPressed = false
        }
    }

    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = (swipeAbleState.offset.value / TOTAL_PERCENTAGE),
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.background)
                .layoutId("header_cards"),
            contentAlignment = Alignment.Center
        ) {
            header()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("header_title")
        ) {
            headerExpanded {
                isBackPressed = true
            }
        }
        content(
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("main_card")
                .swipeable(
                    reverseDirection = true,
                    state = swipeAbleState,
                    anchors = anchors,
                    thresholds = { _, _ ->
                        // The closer to 1 you have to scroll more for it to autocomplete the animation
                        FractionalThreshold(FRACTIONAL_THRESHOLD)
                    },
                    orientation = Orientation.Vertical
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.background)
                .layoutId("bottom_start"),
            contentAlignment = Alignment.TopCenter
        ) {
            footer()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.background)
                .layoutId("bottom_end")
        ) {
            footerExpanded()
        }
    }
}

const val FRACTIONAL_THRESHOLD = 0.8f
const val BEGINNING_ANIMATION = 0
const val TOTAL_PERCENTAGE = 100F
