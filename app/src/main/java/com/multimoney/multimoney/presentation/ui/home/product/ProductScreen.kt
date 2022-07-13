package com.multimoney.multimoney.presentation.ui.home.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@OptIn(ExperimentalPagerApi::class)
@Composable
@Preview
fun CreditHome(
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
            modifier = Modifier.padding(top = 5.dp, start = 16.dp, end = 16.dp),
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
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    text = "Buen día",
                    style = Typography.h6.copy(letterSpacing = 0.38.sp),
                    color = MultimoneyTheme.colors.onBoardingTitleText
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "User Name",
                    style = Typography.h5.copy(letterSpacing = 0.4.sp),
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
                    modifier = Modifier.padding(start = 21.dp, end = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = "",
                        tint = MultimoneyTheme.colors.iconColor
                    )
                }
            }
        }
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(items = viewModel.getCreditOfferAndTips(), itemContent = { item ->

            })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Products(modifier: Modifier, pages: Int, viewModel: ProductViewModel) {
    HorizontalPager(count = pages, modifier = modifier) { page ->

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Extras(modifier: Modifier, pages: Int, viewModel: ProductViewModel) {
    Column(modifier = modifier) {

    }
}
