package com.multimoney.multimoney.presentation.ui.home.product

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.MiniCardsItem
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.GrayScale200
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnDeleteAutomaticPaymentToastEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCallMutationDeactivateClientAutomaticDebit
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnMyProductClick
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnMyProductPageChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.DEFAULT_PRODUCT_PAGES
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCloseCardIssuanceError
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToProfileScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditContent
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditFooter
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditHeaderExpanded
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoContent
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoFooter
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoHeaderExpanded
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartContent
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartFooter
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartHeaderExpanded
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDotsIndicator
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.MotionLayoutMM
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.ProductType
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductScreen(
    sharedViewModel: HomeViewModel,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val deleteAutomaticPaymentToastText =
        stringResource(id = R.string.automatic_payment_edit_bottom_sheet_delete_toast)

    viewModel.onUIEvent(
        OnSetUserData(
            idBrand = sharedViewModel.uiState.idBrand,
            balanceCredit = sharedViewModel.uiState.balance,
            pkUser = sharedViewModel.uiState.pkUser,
            identification = sharedViewModel.uiState.identification,
            email = sharedViewModel.uiState.email,
            userName = sharedViewModel.uiState.userName,
            validateUserStatus = sharedViewModel.uiState.validateUserStatus,
            configurationVersion = sharedViewModel.uiState.configurationVersion,
            productPageList = sharedViewModel.uiState.productPageList,
            smartMovements = sharedViewModel.uiState.smartMovementsList,
            creditMovements = sharedViewModel.uiState.creditMovementsList
        )
    )
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
    }

    LaunchedEffect(key1 = true) {
        sharedViewModel.baseEvent.collect { event ->
            when (event) {
                is HomeViewModel.BaseEvent.OnQuickActionClicked -> {
                    coroutineScope.launch {
                        viewModel.onUIEvent(
                            ProductViewModel.UIEvent.OnQuickActionClicked(
                                event.flow,
                                onLoadingValueChange = {
                                    sharedViewModel.onUIEvent(HomeViewModel.UIEvent.OnLoadingValueChanged(it))
                                }
                            )
                        )
                    }
                }
                is HomeViewModel.BaseEvent.OnMiniCardsClicked -> {
                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnMiniCardsClicked(event.flow))
                }
                is HomeViewModel.BaseEvent.OnEditAutomaticPaymentEvent -> {
                    viewModel.onUIEvent(OnNavigateToScheduleAutomaticPaymentScreen)
                }
                is HomeViewModel.BaseEvent.OnDeleteAutomaticPaymentEvent -> {
                    viewModel.onUIEvent(
                        OnDeleteAutomaticPayment {
                            sharedViewModel.onUIEvent(
                                OnCallMutationDeactivateClientAutomaticDebit
                            )
                        }
                    )
                }
                is OnDeleteAutomaticPaymentToastEvent -> {
                    Toast.makeText(context, deleteAutomaticPaymentToastText, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // Pager
    val headerExpandedPagerState = rememberPagerState()
    val contentPagerState = rememberPagerState()
    val footerPagerState = rememberPagerState()
    val footerExpandedPagerState = rememberPagerState()

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnMyProductPageChange(contentPagerState))
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        headerExpandedPagerState.animateScrollToPage(contentPagerState.currentPage)
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        footerPagerState.animateScrollToPage(contentPagerState.currentPage)
    }
    LaunchedEffect(key1 = contentPagerState.currentPage) {
        footerExpandedPagerState.animateScrollToPage(contentPagerState.currentPage)
    }

    // todo we have to send the pages to the view pager when the back return
    if (sharedViewModel.uiState.isLoading && viewModel.uiState.isExpanded.not()) {
        ProductScreenSkeleton()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            MotionLayoutMM(
                header = {
                    ProductHeader(viewModel = viewModel, sharedViewModel = sharedViewModel)
                },
                headerExpanded = { backPressed ->
                    ProductHeaderExpanded(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MultimoneyTheme.colors.background),
                        state = headerExpandedPagerState,
                        viewModel = viewModel,
                        backPressed = backPressed
                    )
                },
                content = { modifier ->
                    ProductContent(
                        modifier = modifier,
                        state = contentPagerState,
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel
                    )
                },
                footer = {
                    ProductFooter(
                        modifier = Modifier.padding(top = 16.dp),
                        state = footerPagerState,
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel
                    )
                },
                footerExpanded = {
                    ProductFooterExpanded(
                        modifier = Modifier.padding(top = 16.dp),
                        state = footerExpandedPagerState,
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel
                    )
                },
                isExpanded = viewModel.uiState.isExpanded,
                updateIsExpanded = { isExpanded ->
                    viewModel.onUIEvent(OnUpdateIsExpanded(isExpanded))
                },
                forceExpanded = sharedViewModel.uiState.forceIsExpanded,
                updateForceExpanded = { forceExpanded ->
                    sharedViewModel.onUIEvent(OnMyProductClick(forceExpanded))
                }
            )
        }
    }

    if (viewModel.uiState.showCardIssuanceError) {
        AlertResult(
            iconResource = drawable.ic_error_symbol,
            titleResource = string.card_issuance_error_title,
            descriptionResource = viewModel.getCardIssuanceDescriptionError(),
            buttonTextResource = string.understood,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnCloseCardIssuanceError) },
            onButtonClick = { viewModel.onUIEvent(OnCloseCardIssuanceError) }
        )
    }

    LoadingIndicator(sharedViewModel.uiState.isLoading && viewModel.uiState.isExpanded)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun TipsAndOffer(
    modifier: Modifier,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = viewModel.uiState.userStatus?.wording?.textOne ?: "",
                    style = Typography.h6.copy(letterSpacing = 0.38.sp),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = viewModel.uiState.userStatus?.wording?.textTwo ?: "",
                    style = Typography.h5.copy(
                        fontSize = 28.sp,
                        letterSpacing = 0.4.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MultimoneyTheme.colors.labelText
                )
            }
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_notification_large),
                    modifier = Modifier.clickable {
                        // todo action
                    },
                    contentDescription = "",
                    tint = MultimoneyTheme.colors.iconColor
                )
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 2.dp)
                        .clickable {
                            viewModel.onUIEvent(OnNavigateToProfileScreen)
                            },
                    contentDescription = "",
                    tint = MultimoneyTheme.colors.iconColor
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            sharedViewModel.uiState.miniCardList?.let { miniCardList ->
                items(items = miniCardList, itemContent = {
                    TipAndOfferItem(it)
                })
            }
        }
    }
}

@Composable
fun ProductHeader(
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    Column {
        TipsAndOffer(
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
            viewModel = viewModel,
            sharedViewModel = sharedViewModel
        )
        Text(
            text = stringResource(id = R.string.home_product_header_title),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
            style = Typography.body1.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MultimoneyTheme.colors.labelText
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductHeaderExpanded(
    modifier: Modifier,
    state: PagerState,
    viewModel: ProductViewModel,
    backPressed: () -> Unit
) {
    Column(modifier = modifier) {
        HorizontalPager(
            count = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
            state = state,
            userScrollEnabled = false
        ) {
            when (viewModel.uiState.productPageList?.get(currentPage)?.product) {
                ProductType.Credit.value -> CreditHeaderExpanded { backPressed() }
                ProductType.Smart.value -> SmartHeaderExpanded { backPressed() }
                ProductType.Crypto.value -> CryptoHeaderExpanded { backPressed() }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductContent(
    modifier: Modifier,
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    Column(modifier = modifier) {
        HorizontalPager(
            count = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
            modifier = Modifier.padding(top = 8.dp),
            state = state
        ) {
            when (viewModel.uiState.productPageList?.get(currentPage)?.product) {
                ProductType.Credit.value -> CreditContent(viewModel = viewModel)
                ProductType.Smart.value -> SmartContent(viewModel = viewModel, currentPage)
                ProductType.Crypto.value -> CryptoContent(
                    userStatus = viewModel.uiState.userStatus,
                    cryptoBalance = viewModel.balanceCredit?.balanceCryptoAccount,
                    openActionEnable = !sharedViewModel.uiState.forceIsExpanded,
                    clientBalanceHistory = sharedViewModel.uiState.cryptoHistoricalBalance,
                    openCryptoHomeAction = { sharedViewModel.onUIEvent(OnMyProductClick(true)) },
                    openSmartCryptoAction = {
                        viewModel.onUIEvent(OnNavigateToSmartOriginationFlow(comingFromCrypto = true))
                    }
                )
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CustomDotsIndicator(
                totalDots = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
                selectedIndex = state.currentPage,
                selectedColor = GrayScale200,
                unSelectedColor = GrayScale600,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductFooter(
    modifier: Modifier,
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    Column(modifier = modifier) {
        HorizontalPager(
            count = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
            state = state,
            userScrollEnabled = false
        ) {
            when (viewModel.uiState.productPageList?.get(currentPage)?.product) {
                ProductType.Credit.value -> CreditFooter(
                    uiState = viewModel.uiState,
                    balance = viewModel.balanceCredit,
                    onNavigateToDisbursement = { viewModel.onUIEvent(OnNavigateToDisbursement) },
                    onNavigateToVisaActivateScreen = { viewModel.onUIEvent(OnNavigateToHomeMultimoneyVisa) },
                    onCreateMultimoneyVisa = {
                        viewModel.onUIEvent(
                            OnCreateMultimoneyVisa(onLoadingValueChange = {
                                sharedViewModel.onUIEvent(HomeViewModel.UIEvent.OnLoadingValueChanged(it))
                            })
                        )
                    }
                )
                ProductType.Smart.value -> SmartFooter()
                ProductType.Crypto.value -> CryptoFooter()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductFooterExpanded(
    modifier: Modifier,
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    Column(modifier = modifier) {
        HorizontalPager(
            count = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
            state = state,
            userScrollEnabled = false
        ) {
            when (viewModel.uiState.productPageList?.get(currentPage)?.product) {
                ProductType.Credit.value -> CreditFooterExpanded(
                    viewModel = viewModel,
                    sharedViewModel = sharedViewModel
                )
                ProductType.Smart.value -> SmartFooterExpanded(
                    viewModel = viewModel,
                    currentPage
                )
                ProductType.Crypto.value -> CryptoFooterExpanded(
                    userStatus = viewModel.uiState.userStatus,
                    balance = viewModel.balanceCredit,
                    actionMarket = { /* todo send to all coins screen*/ },
                    actionWallet = { /*todo send to "my wallet"*/ },
                    noBalanceAction = {
                        when(viewModel.uiState.idBrand) {
                            Brand.CostaRica.id.toString() -> {
                                viewModel.onUIEvent(
                                    ProductViewModel.UIEvent.OnCartButtonClickWithoutSmartBalance {
                                        viewModel.onUIEvent(OnNavigateToSmartPaymentAccountScreen)
                                    }
                                )
                            }
                            Brand.ElSalvador.id.toString() -> {
                                viewModel.onUIEvent(
                                    ProductViewModel.UIEvent.OnCartButtonClickWithoutSmartBalance {
                                        viewModel.onUIEvent(OnNavigateToSmartPaymentMethodScreen)
                                    }
                                )
                            }
                        }
                    },
                    hasBalanceAction = { /*todo go to buy crypto flow*/ }
                )
            }
        }
    }
}

@Composable
fun TipAndOfferItem(miniCardsItem: MiniCardsItem) {
    TipBox {
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    // TODO: Call appropriate screen when all flows are available
                    // TODO, mocking the first item in order to navigate to the smart origination flow
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(miniCardsItem.imageUrl),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun TipBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(152.dp, 140.dp)
            .padding(
                end = 13.dp
            )
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_tip_background,
            contentScale = ContentScale.FillBounds
        )
        content()
    }
}
