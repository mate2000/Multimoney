package com.multimoney.multimoney.presentation.ui.home.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.Yellow
import com.multimoney.multimoney.presentation.uielement.CustomImage


@OptIn(ExperimentalPagerApi::class)
@Composable
@Preview
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    // we have to send the pages to the view pager when the back return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TipsAndOffer(
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
            pages = 3,
            viewModel = viewModel
        )
        Products(
            modifier = Modifier.padding(top = 25.dp, start = 16.dp, end = 16.dp),
            pages = 3,
            viewModel = viewModel
        )
        Extras(
            modifier = Modifier.padding(top = 5.dp, start = 16.dp, end = 16.dp),
            pages = 1,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TipsAndOffer(modifier: Modifier, pages: Int, viewModel: ProductViewModel) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Buen día",
                    style = Typography.h6.copy(letterSpacing = 0.38.sp),
                    color = MultimoneyTheme.colors.onBoardingTitleText
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "User Name",
                    style = Typography.h5.copy(
                        fontSize = 28.sp,
                        letterSpacing = 0.4.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MultimoneyTheme.colors.onBoardingSubText
                )
            }
            Row {
                IconButton(
                    onClick = {
                        //action
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = "",
                        tint = MultimoneyTheme.colors.iconColor
                    )
                }
                IconButton(
                    onClick = {
                        //action
                    },
                    modifier = Modifier.padding(end = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = "",
                        tint = MultimoneyTheme.colors.iconColor
                    )
                }
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            items(items = viewModel.getCreditOfferAndTips(), itemContent = { item ->
                TipAndOfferItem(item)
            })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Products(modifier: Modifier, pages: Int, viewModel: ProductViewModel) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.home_my_products),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.onBoardingTitleText
        )
        HorizontalPager(count = pages, modifier = Modifier.padding(top = 8.dp)) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Primary500, Primary500, Yellow),
                            start = Offset(0f, Float.POSITIVE_INFINITY),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
                    .blur(0.24.dp)

            ) {
                CustomImage(
                    drawableResource = drawable.ic_swipe_indicator, modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.TopCenter)
                )
                // here we have to identify the state and show the correct state
                WithOutProductNotApproved()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Extras(modifier: Modifier, pages: Int, viewModel: ProductViewModel) {
    Column(modifier = modifier) {

    }
}

@Composable
fun TipAndOfferItem(tipOrOffer: CreditOfferAndTip) {
    TipBox(type = tipOrOffer.type) {
        Box(Modifier.fillMaxSize()) {
            CustomImage(
                drawableResource = R.drawable.ic_logo_multimoney,
                modifier = Modifier
                    .size(54.dp, 54.dp)
                    .align(Alignment.BottomEnd)
            )
            Column {
                Text(
                    text = "Ahorra Smart",
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.onBoardingTitleText
                )
                Text(
                    text = "La mejor tasa del 3.5% anual",
                    modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.onBoardingTitleText,
                    maxLines = 2
                )
                Text(
                    text = "Solicitar", modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.tipActionColor
                )
            }
        }
    }
}

@Composable
fun TipBox(type: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(152.dp, 140.dp)
            .padding(
                end = 13.dp
            )
    ) {
        CustomImage(drawableResource = R.drawable.ic_tip_background, contentScale = ContentScale.FillBounds)
        content()
    }
}

@Composable
fun WithOutProductNotApproved() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_credit_not_approved_title),
            modifier = Modifier.padding(top = 20.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = stringResource(id = R.string.home_credit_not_approved_description),
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.home_credit_not_approved_action),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
        )
    }
}
