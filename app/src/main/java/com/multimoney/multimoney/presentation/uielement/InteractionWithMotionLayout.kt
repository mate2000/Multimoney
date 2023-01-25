package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.IntOffset
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.home.HomeState.EXPANDED
import kotlin.math.absoluteValue

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
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
//    headerTitleHeightPx: Float = 0f,
//    mainCardHeightPx: Float = 0f,
    footerExpandedHeightPx: Float = 0f,
//    ctaFooterExpandedHeightPx: Float = 0f,
    maxFooterExpandedScrollPx: Float = 0f,
    currentPage: Int = 0,
    isSwipeEnabled: Boolean = false
) {
    var offset by remember { mutableStateOf(0f) }
//    var columnHeightPx by remember {
//        mutableStateOf(0f)
//    }
//    val configuration = LocalConfiguration.current
//    val localDensity = LocalDensity.current
//    val screenHeightPx = with(localDensity) { configuration.screenHeightDp.dp.toPx() }

//    var previousFooterExpandedHeightPx by remember { mutableStateOf(-1f) }
//    var headerTitleHeightPx by remember { mutableStateOf(0f) }
//    var mainCardHeightPx by remember { mutableStateOf(0f) }
//    var ctaFooterExpandedHeightPx by remember { mutableStateOf(0f) }
//    var maxFooterExpandedScrollPx by remember { mutableStateOf(columnHeightPx - screenHeightPx + headerTitleHeightPx) }

//    var footerExpandedHeight by remember { mutableStateOf(0f) }
//    var ctaFooterExpandedHeightPx by remember { mutableStateOf(0f) }

//    var maxFooterExpandedScrollPx by remember { mutableStateOf(0f) }

//    maxFooterExpandedScrollPx = columnHeightPx - screenHeightPx + headerTitleHeightPx

    val context = LocalContext.current

//    maxFooterExpandedScrollPx =
//        footerExpandedHeightPx + ctaFooterExpandedHeightPx //- mainCardHeightPx - (headerTitleHeightPx / 2) + ctaFooterExpandedHeightPx
//    LaunchedEffect(
//        keys = arrayOf(
//            footerExpandedHeight,
//            mainCardHeightPx,
//            headerTitleHeightPx,
//            ctaFooterExpandedHeightPx
//        )
//    ) {
//        maxFooterExpandedScrollPx =
//            footerExpandedHeight - mainCardHeightPx - (headerTitleHeightPx / 2) + ctaFooterExpandedHeightPx
//    }

    // This if is needed to avoid unnecessary recompositions
//    if (previousFooterExpandedHeightPx != footerExpandedHeight) {
//        maxFooterExpandedScrollPx =
//            footerExpandedHeight - mainCardHeightPx - (headerTitleHeightPx / 2) + ctaFooterExpandedHeightPx
//        val motionSceneResource =
//        motionSceneContent = motionSceneContent.replace("'footerExpandedHeight'", footerExpandedHeight.toInt().toString())
//        previousFooterExpandedHeightPx = footerExpandedHeight
//    }

    val swipeAbleState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, TOTAL_PERCENTAGE to 1)
    var isBackPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = currentPage) {
        offset = 0f
    }

    LaunchedEffect(key1 = swipeAbleState.offset.value) {
        offset = 0f
        updateIsExpanded(swipeAbleState.offset.value == TOTAL_PERCENTAGE)
        updateForceExpanded(swipeAbleState.offset.value == TOTAL_PERCENTAGE)
    }

    LaunchedEffect(key1 = isBackPressed) {
        if ((isExpanded && isBackPressed) || (forceExpanded && isBackPressed)) {
            offset = 0f
            swipeAbleState.animateTo(BEGINNING_ANIMATION)
            isBackPressed = false
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
//    var columnHeightPx = remember{ 0f }
    var motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    motionSceneContent =
        motionSceneContent.replace(FOOTER_EXPANDED_HEIGHT_TAG, footerExpandedHeightPx.toInt().toString())

    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = if (forceExpanded || homeState == EXPANDED) 1f else (swipeAbleState.offset.value / TOTAL_PERCENTAGE),
        modifier = Modifier.fillMaxHeight()
//            .onGloballyPositioned { coordinates ->
//                columnHeightPx = coordinates.size.height.toFloat()
//        }
    ) {
        content(
            modifier = Modifier.offset {
                if (maxFooterExpandedScrollPx > 0) {
                    IntOffset(0, offset.toInt())
                } else {
                    IntOffset(0, 0)
                }
            }
                .fillMaxWidth()
                .layoutId("main_card")
//                .onSizeChanged { size ->
// //                    if (mainCardHeightPx != size.height.toFloat()) {
// //                        mainCardHeightPx = size.height.toFloat()
// //                    }
//                }
//                .scrollable(
//                    orientation = Vertical,
//                    state = rememberScrollableState { delta ->
//                        if (isExpanded && ((offset + delta).absoluteValue <= maxFooterExpandedScrollPx) && (offset + delta) <= 0) {
//                            offset += delta
//                        }
//                        delta
//                    }
//                )
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
//                .clickable {
                    if (isSwipeEnabled) {
                        offset = 0f
                        updateIsExpanded(true)
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
                .background(MultimoneyTheme.colors.background)
                .layoutId("bottom_start"),
            contentAlignment = Alignment.TopCenter
        ) {
            footer()
        }
        Box(
            modifier = Modifier
                .layoutId("bottom_end")
                .fillMaxWidth()
//                .wrapContentHeight().onSizeChanged { size ->
// //                    if (footerExpandedHeight != size.height.toFloat()) {
//                    footerExpandedHeight = size.height.toFloat()
// //                    }
//                }
                .offset {
                    if (maxFooterExpandedScrollPx > 0) {
                        IntOffset(0, offset.toInt())
                    } else {
                        IntOffset(0, 0)
                    }
                }
                .scrollable(
                    orientation = Vertical,
                    state = rememberScrollableState { delta ->
                        if (isExpanded && ((offset + delta).absoluteValue <= maxFooterExpandedScrollPx) && (offset + delta) <= 0) {
                            offset += delta
                        }
                        delta
                    }
                )
        ) {
            footerExpanded()
        }
        Box(
            modifier = Modifier
                .layoutId("cta_bottom_end")
                .background(MultimoneyTheme.colors.background)
                .fillMaxWidth()
//                .wrapContentHeight()
//                .onSizeChanged { size ->
//                    if (ctaFooterExpandedHeightPx != size.height.toFloat()) {
//                        ctaFooterExpandedHeightPx = size.height.toFloat()
//                    }
//                }
                .scrollable(
                    orientation = Vertical,
                    state = rememberScrollableState { delta ->
                        if (isExpanded && ((offset + delta).absoluteValue <= maxFooterExpandedScrollPx) && (offset + delta) <= 0) {
                            offset += delta
                        }
                        delta
                    }
                )
        ) {
            ctaFooterExpanded()
        }
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
//                .onSizeChanged { size ->
//                    if (headerTitleHeightPx != size.height.toFloat()) {
//                        headerTitleHeightPx = size.height.toFloat()
//                    }
//                }
        ) {
            headerExpanded {
                isBackPressed = true
            }
        }
    }
}

const val FRACTIONAL_THRESHOLD = 0.8f
const val BEGINNING_ANIMATION = 0
const val TOTAL_PERCENTAGE = 100F
const val FOOTER_EXPANDED_HEIGHT_TAG = "'footerExpandedHeight'"
