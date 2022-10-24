package com.multimoney.multimoney.presentation.ui.test.motionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import kotlin.math.absoluteValue

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MotionLayoutMM(
    mainHeader: @Composable () -> Unit,
    secondaryHeader: @Composable (backFunction: () -> Unit) -> Unit,
    content: @Composable (modifier: Modifier, headerText: Int) -> Unit,
    footer: @Composable () -> Unit,
    secondaryFooter: @Composable () -> Unit,
    totalPages: Int = TOTAL_PAGES
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
    var isExpanded by remember { mutableStateOf(false) }

    // Pager
    val headerTitlePagerState = rememberPagerState()
    val mainCardPagerState = rememberPagerState()
    val bottomEndPagerState = rememberPagerState()

    LaunchedEffect(key1 = true) {
        mainCardPagerState.scrollToPage(1)
    }

    LaunchedEffect(key1 = mainCardPagerState.currentPage) {
        headerTitlePagerState.animateScrollToPage(mainCardPagerState.currentPage)
    }
    LaunchedEffect(key1 = mainCardPagerState.currentPage) {
        bottomEndPagerState.animateScrollToPage(mainCardPagerState.currentPage)
    }

    LaunchedEffect(key1 = isExpanded) {
        if (isExpanded) {
            swipeAbleState.animateTo(BEGINNING_ANIMATION)
            isExpanded = false
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
            mainHeader()
        }
        HorizontalPager(
            state = headerTitlePagerState,
            userScrollEnabled = false,
            count = totalPages,
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("header_title")
        ) { page ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                secondaryHeader {
                    isExpanded = true
                }
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
                ),
            headerText = if (swipeAbleState.offset.value > FRACTIONAL_THRESHOLD) CUSTOM_HEADER else R.string.home_my_products
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
        HorizontalPager(
            state = bottomEndPagerState,
            userScrollEnabled = false,
            count = totalPages,
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.background)
                .layoutId("bottom_end")
        ) {
            secondaryFooter()
        }
    }
}

const val FRACTIONAL_THRESHOLD = 0.8f
const val BEGINNING_ANIMATION = 0
const val CUSTOM_HEADER = 0
const val TOTAL_PAGES = 3
const val TOTAL_PERCENTAGE = 100F
