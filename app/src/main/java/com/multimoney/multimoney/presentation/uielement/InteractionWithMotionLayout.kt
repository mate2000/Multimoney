package com.multimoney.multimoney.presentation.uielement

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.home.HomeState.EXPANDED
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun MotionLayoutMM(
    header: @Composable () -> Unit,
    headerExpanded: @Composable (backFunction: () -> Unit) -> Unit,
    content: @Composable (modifier: Modifier) -> Unit,
    footer: @Composable () -> Unit,
    isExpanded: Boolean = false,
    forceExpanded: Boolean = false,
    updateIsExpanded: (Boolean) -> Unit = {},
    updateForceExpanded: (Boolean) -> Unit = {},
    footerExpanded: @Composable () -> Unit = {},
    ctaFooterExpanded: @Composable () -> Unit = {},
    homeState: HomeState,
    updateHomeState: (HomeState) -> Unit = {},
    isSwipeEnabled: Boolean = false
) {
    val context = LocalContext.current
    val swipeAbleState = rememberSwipeableState(initialValue = HomeState.UNEXPANDED)
    val anchors = mapOf(0f to HomeState.UNEXPANDED, TOTAL_PERCENTAGE to HomeState.EXPANDED)
    var isBackPressed by remember { mutableStateOf(false) }
    var isAnimationRunning by remember { mutableStateOf(false) }
//    var animationRemainingTime by remember { mutableStateOf(5000.milliseconds) }
    var animationProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = swipeAbleState.offset.value) {
        if (isAnimationRunning.not()) {
            animationProgress = swipeAbleState.offset.value / TOTAL_PERCENTAGE
        }
        if (swipeAbleState.targetValue == HomeState.EXPANDED) {
            updateIsExpanded(true)
//            updateForceExpanded(true)
        } else if (swipeAbleState.currentValue == HomeState.UNEXPANDED && isExpanded) {
            updateIsExpanded(false)
            isAnimationRunning = true
//            swipeAbleState.animateTo(HomeState.UNEXPANDED)
            animationProgress = 1f
            //val timer = object : CountDownTimer(1500, 90) {
            val timer = object : CountDownTimer(1000, 60) {
                override fun onTick(millisMainUntilFinished: Long) {
//                    animationRemainingTime = animationRemainingTime.minus(1.milliseconds)
                    animationProgress = animationProgress.minus(0.06f)
                }

                override fun onFinish() {
                    animationProgress = 0f
                    isAnimationRunning = false
                }
            }
            timer.start()

//            animationProgress = 1f
//            animationRemainingTime = 5000.milliseconds
//            tickerFlow(
//                period = 1.milliseconds,
//                initialDelay = 1.milliseconds,
//                duration = animationRemainingTime
//            )
//                .takeWhile { isAnimationRunning }
//                .map {
//                    LocalDateTime.now()
//                }
//                .distinctUntilChanged { old, new ->
//                    old.nano == new.nano
//                }
//                .onEach {
//                    if (animationRemainingTime.inWholeMilliseconds > 0 && isAnimationRunning) {
//                        animationRemainingTime = animationRemainingTime.minus(1.milliseconds)
//                        animationProgress = animationProgress.minus(0.0002f)
//                    } else if (isAnimationRunning) {
//                        isAnimationRunning = false
//                    }
//                }
//                .launchIn(this)
        }
    }

//    LaunchedEffect(key1 = swipeAbleState.offset.value) {
// //        if (isAnimationRunning.not()) {
// //            animationProgress = swipeAbleState.offset.value / TOTAL_PERCENTAGE
// //        }
//        animationProgress = swipeAbleState.offset.value / TOTAL_PERCENTAGE
// //        updateIsExpanded(swipeAbleState.offset.value == TOTAL_PERCENTAGE)
// //        updateForceExpanded(swipeAbleState.offset.value == TOTAL_PERCENTAGE)
//    }

    LaunchedEffect(key1 = isBackPressed) {
        if ((isExpanded && isBackPressed) || (forceExpanded && isBackPressed)) {
            isBackPressed = false
            swipeAbleState.animateTo(HomeState.UNEXPANDED)
            updateIsExpanded(false)
            if (forceExpanded) {
                updateForceExpanded(false)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        if (homeState == HomeState.UNEXPANDED) {
            updateHomeState(HomeState.OLD_STATE)
            isBackPressed = true
        }
    }
    if (swipeAbleState.currentValue == HomeState.EXPANDED) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .background(MultimoneyTheme.colors.background)
                        .fillMaxWidth()
                ) {
                    headerExpanded {
                        isBackPressed = true
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
                        .nestedScroll(rememberNestedScrollInteropConnection())
                        .background(MultimoneyTheme.colors.background)
                        .padding(it)
                        .fillMaxSize(),
                    content = {
                        item {
                            content(
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .swipeable(
                                        enabled = isSwipeEnabled,
                                        reverseDirection = true,
                                        state = swipeAbleState,
                                        anchors = anchors,
                                        thresholds = { _, _ ->
                                            // The closer to 1 you have to scroll more for it to autocomplete the animation
                                            FractionalThreshold(0.8f)
                                        },
                                        orientation = Vertical
                                    )
                            )
                            Box(
                                modifier = Modifier
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
        val motionSceneContent = remember {
            context.resources
                .openRawResource(R.raw.motion_scene)
                .readBytes()
                .decodeToString()
        }
        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            // progress = if (forceExpanded || homeState == EXPANDED) 1f else if (isExpanded.not()) (swipeAbleState.offset.value / TOTAL_PERCENTAGE) else 0f,
            progress = if (forceExpanded || homeState == EXPANDED) 1f else animationProgress, // if (isAnimationRunning) animationProgress else (swipeAbleState.offset.value / TOTAL_PERCENTAGE),
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(MultimoneyTheme.colors.background)
                    .layoutId("header_cards"),
                contentAlignment = Alignment.Center
            ) {
                header()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(MultimoneyTheme.colors.background)
                    .layoutId("header_title")
            ) {
                headerExpanded {
                    isBackPressed = true
                }
            }
            content(
                modifier = Modifier
                    .layoutId("main_card")
                    .fillMaxWidth()
                    .wrapContentHeight()
//                    .background(MultimoneyTheme.colors.background)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        if (isSwipeEnabled) {
                            updateForceExpanded(true)
                        }
                    }
                    .swipeable(
                        enabled = isSwipeEnabled,
                        reverseDirection = true,
                        state = swipeAbleState,
                        anchors = anchors,
                        thresholds = { _, _ ->
                            // The closer to 1 you have to scroll more for it to autocomplete the animation
                            FractionalThreshold(FRACTIONAL_THRESHOLD)
                        },
                        orientation = Vertical
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(MultimoneyTheme.colors.background)
                    .layoutId("bottom_start"),
                contentAlignment = Alignment.TopCenter
            ) {
                footer()
            }
            Box(
                modifier = Modifier
                    .layoutId("bottom_end")
//                    .background(MultimoneyTheme.colors.background)
                    .fillMaxWidth()
            ) {
                footerExpanded()
            }
            Box(
                modifier = Modifier
                    .layoutId("cta_bottom_end")
//                    .background(MultimoneyTheme.colors.background)
                    .fillMaxWidth()
            ) {
                ctaFooterExpanded()
            }
        }
    }
}

const val FRACTIONAL_THRESHOLD = 0.8f
const val BEGINNING_ANIMATION = 0
const val TOTAL_PERCENTAGE = 100F
const val FOOTER_EXPANDED_HEIGHT_TAG = "'footerExpandedHeight'"
