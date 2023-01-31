package com.multimoney.multimoney.presentation.uielement

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val swipeAbleState = rememberSwipeableState(initialValue = COLLAPSED)
    val anchors = mapOf(0f to COLLAPSED, TOTAL_PERCENTAGE to EXPANDED)
    var isAnimationRunning by remember { mutableStateOf(false) }
    var animationProgress by remember {
        mutableStateOf(
            if (isExpanded) {
                ANIMATION_EXPANDED
            } else {
                ANIMATION_COLLAPSED
            }
        )
    }

    LaunchedEffect(key1 = swipeAbleState.offset.value) {
        if (isAnimationRunning.not() && swipeAbleState.offset.value != ANIMATION_COLLAPSED && swipeAbleState.offset.value != TOTAL_PERCENTAGE) {
            animationProgress = swipeAbleState.offset.value / TOTAL_PERCENTAGE
        }
    }

    var timer: CountDownTimer?
    if (isExpandedByClick) {
        updateIsExpandedByClick(false)
        isAnimationRunning = true
        animationProgress = ANIMATION_COLLAPSED
        timer = object : CountDownTimer(TIMER_FUTURE, TIMER_COUNT_DOWN) {
            override fun onTick(millisMainUntilFinished: Long) {
                animationProgress = animationProgress.plus(ANIMATION_FRACTION)
            }

            override fun onFinish() {
                if (isAnimationRunning) {
                    animationProgress = ANIMATION_EXPANDED
                    isAnimationRunning = false
                    updateIsExpanded(true)
                    timer = null
                }
            }
        }
        timer?.start()
    }

    if (isExpanded && isBackPressed) {
        updateIsExpanded(false)
        isAnimationRunning = true
        timer = object : CountDownTimer(TIMER_FUTURE, TIMER_COUNT_DOWN) {
            override fun onTick(millisMainUntilFinished: Long) {
                animationProgress = if (animationProgress > 0f) animationProgress.minus(ANIMATION_FRACTION) else 0f
            }

            override fun onFinish() {
                if (isAnimationRunning) {
                    animationProgress = ANIMATION_COLLAPSED
                    updateIsBackPressed(false)
                    isAnimationRunning = false
                    timer = null
                }
            }
        }
        timer?.start()
    }

    LaunchedEffect(key1 = true) {
        if (homeState == COLLAPSED) {
            updateHomeState(HomeState.OLD_STATE)
            animationProgress = ANIMATION_COLLAPSED
            updateIsBackPressed(false)
        }
    }

    if (animationProgress == ANIMATION_EXPANDED && isExpanded) {
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
                            content(
                                modifier = Modifier
                                    .background(MultimoneyTheme.colors.background)
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .swipeable(
                                        enabled = isSwipeEnabled,
                                        reverseDirection = true,
                                        state = swipeAbleState,
                                        anchors = anchors,
                                        thresholds = { _, _ ->
                                            // The closer to 1 you have to scroll more for it to autocomplete the animation
                                            FractionalThreshold(EXPANDED_FRACTIONAL_THRESHOLD)
                                        },
                                        orientation = Vertical
                                    )
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
    } else {
        if (animationProgress != ANIMATION_EXPANDED && isExpanded && swipeAbleState.isAnimationRunning.not()) {
            updateIsBackPressed(true)
        } else if (animationProgress == ANIMATION_EXPANDED && isExpanded.not()) {
            updateIsExpanded(true)
        }
        val motionSceneContent = remember {
            context.resources
                .openRawResource(R.raw.motion_scene)
                .readBytes()
                .decodeToString()
        }
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
            Box(
                modifier = Modifier.layoutId("main_card")
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                content(
                    modifier = Modifier
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
            }
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
                footerExpanded()
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

const val TIMER_FUTURE = 1000L
const val TIMER_COUNT_DOWN = 60L
const val ANIMATION_FRACTION = 0.06f
const val ANIMATION_EXPANDED = 1f
const val ANIMATION_COLLAPSED = 0f
const val COLLAPSED_FRACTIONAL_THRESHOLD = 0.8f
const val EXPANDED_FRACTIONAL_THRESHOLD = 0.5f
const val TOTAL_PERCENTAGE = 100f
