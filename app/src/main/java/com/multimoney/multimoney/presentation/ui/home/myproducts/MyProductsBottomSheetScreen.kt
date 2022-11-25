package com.multimoney.multimoney.presentation.ui.home.myproducts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.ComplementaryTwo500
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Secondary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency16
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnMyProductClick
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyProductsBottomSheetScreen(
    shareViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = string.home_my_products_title,
        closeIcon = drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 24.dp)
        ) {
            when (shareViewModel.uiState.idBrand) {

                Brand.CostaRica.id.toString() -> {
                    ContentPerBrand(
                        cr = true,
                        shareViewModel = shareViewModel,
                        modalBottomSheetState = modalBottomSheetState
                    )
                }
                Brand.ElSalvador.id.toString() -> {
                    ContentPerBrand(
                        sv = true,
                        shareViewModel = shareViewModel,
                        modalBottomSheetState = modalBottomSheetState
                    )
                }
                Brand.Guatemala.id.toString() -> {
                    ContentPerBrand(
                        shareViewModel = shareViewModel,
                        modalBottomSheetState = modalBottomSheetState
                    )
                }
                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun ContentPerBrand(
    shareViewModel: HomeViewModel,
    modalBottomSheetState: ModalBottomSheetState,
    cr: Boolean = false,
    sv: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val productScreenPagerState = shareViewModel.uiState.productScreenPagerState

    // credit section
    Text(
        text = stringResource(id = string.home_my_products_title_credit),
        style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
        color = MultimoneyTheme.colors.creditNotApprovedText
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        item {
            MyProductItem(icon = drawable.ic_my_credit,
                label = stringResource(id = string.home_my_products_label_credit),
                backGroundColor = Primary500,
                action = {
                    shareViewModel.onUIEvent(OnMyProductClick(true))
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        productScreenPagerState?.animateScrollToPage(PAGER_INDEX_CREDIT)
                    }
                })
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Divider(modifier = Modifier.fillMaxWidth(), color = WhiteTransparency16)
    Spacer(modifier = Modifier.height(16.dp))
    // smart section
    Text(
        text = stringResource(id = string.home_my_products_title_smart),
        style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
        color = MultimoneyTheme.colors.creditNotApprovedText
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {

        item {
            MyProductItem(icon = drawable.ic_dollars_strong,
                label = stringResource(id = string.home_my_products_label_smart),
                backGroundColor = Secondary500,
                action = {
                    shareViewModel.onUIEvent(OnMyProductClick(true))
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        productScreenPagerState?.animateScrollToPage(PAGER_INDEX_SMART)
                    }
                })
        }
        // only show multi account if brand is Costa Rica
        if (cr) {
            item {
                MyProductItem(icon = drawable.ic_colones_strong,
                    label = stringResource(id = string.home_my_products_label_smart_colones),
                    backGroundColor = Secondary500,
                    action = {
                        shareViewModel.onUIEvent(OnMyProductClick(true))
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            productScreenPagerState?.animateScrollToPage(
                                PAGER_INDEX_SMART_MULTI_ACCOUNT
                            )
                        }
                    })
            }
        }
    }
    // if Costa Rica or El Salvador show crypto
    if (cr || sv) {
        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.fillMaxWidth(), color = WhiteTransparency16)
        Spacer(modifier = Modifier.height(16.dp))
        //crypto section
        Text(
            text = stringResource(id = string.home_my_products_title_crypto),
            style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            val multiAccountSmart = productScreenPagerState?.pageCount ?: DEFAULT_PAGER_INDEX

            item {
                MyProductItem(icon = drawable.ic_union,
                    label = stringResource(id = string.home_my_products_label_crypto),
                    backGroundColor = ComplementaryTwo500,
                    action = {
                        shareViewModel.onUIEvent(OnMyProductClick(true))
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            val page = if (multiAccountSmart > PAGER_COUNT_MULTI_ACCOUNT)
                                PAGER_INDEX_MULTI_ACCOUNT_CRYPTO else PAGER_INDEX_DEFAULT_CRYPTO
                            productScreenPagerState?.animateScrollToPage(page)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MyProductItem(
    icon: Int, label: String, backGroundColor: Color, action: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .width(88.dp)
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(shape = CircleShape, color = backGroundColor)
                .clickable { action() }) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(16.dp)
                    .background(shape = CircleShape, color = Color.Transparent),
                painter = painterResource(id = icon),
                tint = MultimoneyTheme.colors.text,
                contentDescription = label
            )
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.quickActionLabelColor,
            textAlign = TextAlign.Center
        )
    }
}

const val PAGER_INDEX_CREDIT = 0
const val PAGER_INDEX_SMART = 1
const val PAGER_INDEX_SMART_MULTI_ACCOUNT = 2
const val PAGER_INDEX_DEFAULT_CRYPTO = 2
const val PAGER_INDEX_MULTI_ACCOUNT_CRYPTO = 3
const val PAGER_COUNT_MULTI_ACCOUNT = 3
const val DEFAULT_PAGER_INDEX = 2