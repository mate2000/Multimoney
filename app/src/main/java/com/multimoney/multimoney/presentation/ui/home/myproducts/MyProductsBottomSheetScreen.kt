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
import androidx.compose.foundation.lazy.items
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
import com.google.accompanist.pager.PagerState
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
import com.multimoney.multimoney.presentation.util.catalog.ProductPage
import com.multimoney.multimoney.presentation.util.catalog.ProductType
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
            MyProductsContent(shareViewModel, modalBottomSheetState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MyProductsContent(
    shareViewModel: HomeViewModel,
    modalBottomSheetState: ModalBottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    val productScreenPagerState = shareViewModel.uiState.productScreenPagerState
    val productPageList = shareViewModel.uiState.productPageList

    // credit section
    val creditProducts = productPageList.filter { it.product == ProductType.Credit.value }

    MyProductSection(
        creditProducts,
        shareViewModel,
        coroutineScope,
        modalBottomSheetState,
        productScreenPagerState,
        Primary500,
        stringResource(id = string.home_my_products_title_credit),
    )

    // smart section
    val smartProducts = productPageList.filter { it.product == ProductType.Smart.value }

    MyProductSection(
        products = smartProducts,
        shareViewModel = shareViewModel,
        coroutineScope = coroutineScope,
        modalBottomSheetState = modalBottomSheetState,
        productScreenPagerState = productScreenPagerState,
        backGroundColor = Secondary500,
        labelText = stringResource(id = string.home_my_products_title_smart),
        true
    )

    //crypto section
    val cryptoProducts = productPageList.filter { it.product == ProductType.Crypto.value }

    MyProductSection(
        products = cryptoProducts,
        shareViewModel = shareViewModel,
        coroutineScope = coroutineScope,
        modalBottomSheetState = modalBottomSheetState,
        productScreenPagerState = productScreenPagerState,
        backGroundColor = ComplementaryTwo500,
        labelText = stringResource(id = string.home_my_products_title_crypto),
        true
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
private fun MyProductSection(
    products: List<ProductPage>,
    shareViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    productScreenPagerState: PagerState?,
    backGroundColor: Color,
    labelText: String,
    showDivider: Boolean = false
) {

    if (products.isNotEmpty()) {
        if (showDivider) {
            CustomDivider()
        }
        Text(
            text = labelText,
            style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            items(products) { productPage ->
                MyProductItem(
                    icon = productPage.resourceIcon,
                    label = stringResource(id = productPage.resourceText),
                    backGroundColor = backGroundColor,
                    action = {
                        shareViewModel.onUIEvent(OnMyProductClick(true))
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            productScreenPagerState?.animateScrollToPage(productPage.index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MyProductItem(
    icon: Int, label: String,
    backGroundColor: Color,
    action: () -> Unit = {}
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
                .clickable { action() }
        ) {
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

@Composable
private fun CustomDivider() {
    Spacer(modifier = Modifier.height(16.dp))
    Divider(modifier = Modifier.fillMaxWidth(), color = WhiteTransparency16)
    Spacer(modifier = Modifier.height(16.dp))
}