package com.multimoney.multimoney.presentation.uielement

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.home.HomeState.COLLAPSED
import com.multimoney.multimoney.presentation.ui.home.HomeState.EXPANDED

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
@Composable
fun MotionLayoutMM(
    header: @Composable () -> Unit,
    headerExpanded: @Composable (backFunction: () -> Unit) -> Unit,
    content: @Composable (modifier: Modifier) -> Unit,
    contentExpanded: @Composable (modifier: Modifier) -> Unit,
    footer: @Composable () -> Unit,
    isExpanded: Boolean = false,
    updateIsExpanded: (Boolean) -> Unit = {},
    footerExpanded: @Composable () -> Unit = {},
    ctaFooterExpanded: @Composable () -> Unit = {},
    homeState: HomeState,
    updateHomeState: (HomeState) -> Unit = {},
    isSwipeEnabled: Boolean = false,
    isBackPressed: Boolean = false,
    updateIsBackPressed: (Boolean) -> Unit = {},
    isExpandedByClick: Boolean = false,
    updateIsExpandedByClick: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val swipeAbleState = rememberSwipeableState(initialValue = if (isExpanded) EXPANDED else COLLAPSED)
    val anchors = mapOf(ANIMATION_COLLAPSED to COLLAPSED, ANIMATION_EXPANDED to EXPANDED)
    var animationProgress by remember {
        mutableStateOf(
            if (isExpanded) ANIMATION_EXPANDED else ANIMATION_COLLAPSED
        )
    }
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    LaunchedEffect(key1 = isExpandedByClick) {
        if (isExpandedByClick) {
            swipeAbleState.animateTo(EXPANDED, tween(TIMER_FUTURE, TIMER_COUNT_DOWN))
        }
    }

    LaunchedEffect(key1 = isBackPressed) {
        if (isBackPressed) {
            updateIsExpanded(false)
            swipeAbleState.animateTo(COLLAPSED, tween(TIMER_FUTURE, TIMER_COUNT_DOWN))
        }
    }

    if (swipeAbleState.isAnimationRunning) {
        DisposableEffect(Unit) {
            onDispose {
                when (swipeAbleState.currentValue) {
                    EXPANDED -> {
                        updateIsExpanded(true)
                        updateIsBackPressed(false)
                        updateIsExpandedByClick(false)
                        animationProgress = ANIMATION_EXPANDED / ANIMATION_EXPANDED
                    }
                    COLLAPSED -> {
                        updateIsExpanded(false)
                        updateIsBackPressed(false)
                        updateIsExpandedByClick(false)
                        animationProgress = ANIMATION_COLLAPSED
                    }
                    else -> {
                        return@onDispose
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = swipeAbleState.offset.value) {
        if ((swipeAbleState.offset.value / ANIMATION_EXPANDED) > ANIMATION_COLLAPSED) {
            animationProgress = swipeAbleState.offset.value / ANIMATION_EXPANDED
        }
    }

    LaunchedEffect(key1 = homeState) {
        if (homeState == COLLAPSED) {
            updateHomeState(HomeState.OLD_STATE)
            updateIsExpanded(false)
            updateIsBackPressed(false)
            updateIsExpandedByClick(false)
            swipeAbleState.snapTo(COLLAPSED)
            animationProgress = ANIMATION_COLLAPSED
        }
    }
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .background(MultimoneyTheme.colors.background)
                    .fillMaxWidth()
            ) {
                headerExpanded {
                    updateIsBackPressed(true)
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(MultimoneyTheme.colors.background)
                    .fillMaxWidth()
            ) {
                ctaFooterExpanded()
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .background(MultimoneyTheme.colors.background)
                    .padding(it)
                    .fillMaxSize(),
                content = {
                    item {
                        contentExpanded(
                            modifier = Modifier
                                .background(MultimoneyTheme.colors.background)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        if (dragAmount.y > ANIMATION_COLLAPSED) {
                                            updateIsBackPressed(true)
                                        }
                                    }
                                }
                        )
                        Box(
                            modifier = Modifier
                                .background(MultimoneyTheme.colors.background)
                                .fillMaxSize()
                        ) {
                            footerExpanded()
                        }
                    }
                }
            )
        }
    )
    if (isExpanded.not() || swipeAbleState.offset.value == ANIMATION_COLLAPSED) {
        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = if (homeState == EXPANDED) ANIMATION_EXPANDED else animationProgress,
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    updateIsBackPressed(true)
                }
            }
            content(
                modifier = Modifier
                    .layoutId("main_card")
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .swipeable(
                        enabled = isSwipeEnabled,
                        reverseDirection = true,
                        state = swipeAbleState,
                        anchors = anchors,
                        thresholds = { _, _ ->
                            // The closer to 1 you have to scroll more for it to autocomplete the animation
                            FractionalThreshold(COLLAPSED_FRACTIONAL_THRESHOLD)
                        },
                        orientation = Vertical
                    )
                    .clickable(
                        enabled = isSwipeEnabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        updateIsExpandedByClick(true)
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("bottom_start"),
                contentAlignment = Alignment.TopCenter
            ) {
                footer()
            }
            Box(
                modifier = Modifier
                    .layoutId("bottom_end")
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .background(MultimoneyTheme.colors.background)
                        .fillMaxSize(),
                    content = {
                        item {
                            footerExpanded()
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .layoutId("cta_bottom_end")
                    .background(MultimoneyTheme.colors.background)
                    .fillMaxWidth()
            ) {
                ctaFooterExpanded()
            }
        }
    }
}

const val TIMER_FUTURE = 2000
const val TIMER_COUNT_DOWN = 30
const val ANIMATION_EXPANDED = 100f
const val ANIMATION_COLLAPSED = 0f
const val COLLAPSED_FRACTIONAL_THRESHOLD = 0.8f
