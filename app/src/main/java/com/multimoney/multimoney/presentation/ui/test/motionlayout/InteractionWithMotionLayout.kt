package com.multimoney.multimoney.presentation.ui.test.motionlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import kotlin.math.absoluteValue

@Composable
fun InteractionWithMotionLayout() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        MotionLayoutMM()
    }
}

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
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
        HorizontalPager(
            state = headerTitlePagerState,
            userScrollEnabled = false,
            count = 3, modifier = Modifier
                .fillMaxWidth()
                .layoutId("header_title")
        ) { page ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(bottomFinalCardHeight)
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
                when (page) {
                    0 -> Text(
                        text = "Action Bar $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Yellow)
                    )
                    1 -> Text(
                        text = "Action Bar $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Yellow)
                    )
                    2 -> Text(
                        text = "Action Bar $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Yellow)
                    )
                }
            }
        }
        HorizontalPager(
            state = mainCardPagerState,
            count = 3, modifier = Modifier
                .fillMaxWidth()
                .layoutId("main_card")
                .swipeable(
                    state = swipeAbleState,
                    anchors = anchors,
                    thresholds = { _, _ ->
                        // Entre mas se aproxima a 1 se tiene que hacer mas scroll para que se autocomplete la animacion
                        FractionalThreshold(0.8f)
                    },
                    orientation = Orientation.Vertical
                )
        ) { page ->
            Card(
                Modifier
                    .fillMaxSize()
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
                when (page) {
                    0 ->
                        Text(
                            text = "Card Principal $page",
                            color = Color.Black,
                            style = Typography.h4,
                            modifier = Modifier
                                .background(Color.Green)
                                .size(120.dp, 140.dp)
                        )
                    1 -> Text(
                        text = "Card Principal $page",
                        color = Color.Black,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Green)
                            .size(120.dp, 140.dp)
                    )
                    2 -> Text(
                        text = "Card Principal $page",
                        color = Color.Black,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Green)
                            .size(120.dp, 140.dp)
                    )
                }
            }
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
        HorizontalPager(
            state = bottomEndPagerState,
            userScrollEnabled = false,
            count = 3, modifier = Modifier
                .fillMaxWidth()
                .layoutId("bottom_end")
        ) { page ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(bottomFinalCardHeight)
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
                when (page) {
                    0 -> Text(
                        text = "Cards Final $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Black)
                    )
                    1 -> Text(
                        text = "Cards Final $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Black)
                    )
                    2 -> Text(
                        text = "Cards Final $page",
                        color = Color.White,
                        style = Typography.h4,
                        modifier = Modifier
                            .background(Color.Black)
                    )
                }

            }
        }
    }
}

const val TOTAL_PERCENTAGE = 100F
