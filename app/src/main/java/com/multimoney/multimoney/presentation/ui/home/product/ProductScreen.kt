package com.multimoney.multimoney.presentation.ui.home.product

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.ConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnDeleteAutomaticPaymentToastEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCallMutationDeactivateClientAutomaticDebit
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnMyProductPageChange
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnUpdateIsExpandedByClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.BaseEvent.OnShowCardIssuanceError
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.BaseEvent.OnShowTbdToastEvent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.DEFAULT_PRODUCT_PAGES
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetCryptoMovements
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCryptoMarket
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCryptoMovements
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCryptoWallet
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToProfileScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNoVoConfig
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsBackPressed
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnUpdateIsExpanded
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnVisaCardExpiredDialog
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditContent
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditCtaFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditFooter
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditHeaderExpanded
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoContent
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoCtaFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoFooter
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.crypto.CryptoHeaderExpanded
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartContent
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartCtaFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartFooter
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartFooterExpanded
import com.multimoney.multimoney.presentation.ui.home.product.smart.SmartHeaderExpanded
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDotsIndicator
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.MotionLayoutMM
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.ProductType
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProductScreen(
    sharedViewModel: HomeViewModel,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel()
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
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
        viewModel.onUIEvent(OnNoVoConfig)
        viewModel.onUIEvent(OnGetCryptoMovements)
    }

    LaunchedEffect(key1 = sharedViewModel.uiState.balance) {
        viewModel.onUIEvent(
            OnVisaCardExpiredDialog(
                idBrand = sharedViewModel.uiState.idBrand,
                balance = sharedViewModel.uiState.balance
            )
        )
    }

    // BaseEvent from ProductViewModel
    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnShowCardIssuanceError -> sharedViewModel.onUIEvent(UIEvent.OnShowCardIssuanceError)
                is OnShowTbdToastEvent -> Toast.makeText(context, "TBD", Toast.LENGTH_SHORT).show()
                is ProductViewModel.BaseEvent.OnShowDisclaimer -> {
                    bottomSheetState.show()
                }
            }
        }
    }

    // BaseEvent from HomeViewModel
    LaunchedEffect(key1 = true) {
        sharedViewModel.baseEvent.collect { event ->
            when (event) {
                is HomeViewModel.BaseEvent.OnQuickActionClicked -> {
                    coroutineScope.launch {
                        viewModel.onUIEvent(
                            ProductViewModel.UIEvent.OnQuickActionClicked(
                                event.flow,
                                onLoadingValueChange = {
                                    sharedViewModel.onUIEvent(
                                        HomeViewModel.UIEvent.OnLoadingValueChanged(
                                            it
                                        )
                                    )
                                }
                            )
                        )
                    }
                }
                is HomeViewModel.BaseEvent.OnMiniCardsClicked -> {
                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnMiniCardsClicked(event.flow))
                }
                is HomeViewModel.BaseEvent.OnEditAutomaticPaymentEvent -> {
                    viewModel.onUIEvent(OnNavigateToScheduleAutomaticPaymentScreen(true))
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
    val ctaFooterExpandedPagerState = rememberPagerState()

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnMyProductPageChange(contentPagerState))
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        headerExpandedPagerState.scrollToPage(contentPagerState.currentPage)
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        footerPagerState.scrollToPage(contentPagerState.currentPage)
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        footerExpandedPagerState.scrollToPage(contentPagerState.currentPage)
    }

    LaunchedEffect(key1 = contentPagerState.currentPage) {
        ctaFooterExpandedPagerState.scrollToPage(contentPagerState.currentPage)
    }

    if (sharedViewModel.uiState.isLoading && viewModel.uiState.isExpanded.not()) {
        ProductScreenSkeleton()
    } else {
        MotionLayoutMM(
            header = {
                ProductHeader(viewModel = viewModel, sharedViewModel = sharedViewModel)
            },
            headerExpanded = { backPressed ->
                ProductHeaderExpanded(
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
                    state = footerPagerState,
                    viewModel = viewModel,
                    sharedViewModel = sharedViewModel
                )
            },
            footerExpanded = {
                ProductFooterExpanded(
                    state = footerExpandedPagerState,
                    viewModel = viewModel,
                    sharedViewModel = sharedViewModel
                )
            },
            ctaFooterExpanded = {
                ProductCtaFooterExpanded(
                    state = ctaFooterExpandedPagerState,
                    viewModel = viewModel,
                    sharedViewModel = sharedViewModel
                )
            },
            isExpanded = viewModel.uiState.isExpanded,
            updateIsExpanded = { isExpanded ->
                if (isExpanded != viewModel.uiState.isExpanded) {
                    viewModel.onUIEvent(OnUpdateIsExpanded(isExpanded))
                }
            },
            homeState = sharedViewModel.uiState.homeState,
            updateHomeState = { homeState ->
                sharedViewModel.onUIEvent(UIEvent.OnSetHomeState(homeState))
            },
            isSwipeEnabled = if (viewModel.uiState.productPageList?.isNotEmpty() == true) {
                viewModel.uiState.productPageList?.get(contentPagerState.currentPage)?.enabled
                    ?: false
            } else {
                false
            },
            isBackPressed = viewModel.uiState.isBackPressed,
            updateIsBackPressed = { isBackPressed ->
                viewModel.onUIEvent(OnUpdateIsBackPressed(isBackPressed))
            },
            isExpandedByClick = sharedViewModel.uiState.isExpandedByClick,
            updateIsExpandedByClick = { isExpandedByClick ->
                sharedViewModel.onUIEvent(OnUpdateIsExpandedByClick(isExpandedByClick))
            }
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
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }
    ConfirmationBottomSheet(
        modalBottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope,
        onCheckedChange = {
            viewModel.onUIEvent(ProductViewModel.UIEvent.OnDisclaimerChecked(it))
        },
        onContinueClicked = {
            viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToPurchaseCryptoFlow)
            viewModel.onUIEvent(ProductViewModel.UIEvent.OnUpdateShouldShowDisclaimer(viewModel.uiState.dontShowAgainChecked))
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        checked = viewModel.uiState.dontShowAgainChecked
    )
}

@Composable
fun TipsAndOffer(
    modifier: Modifier,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    val poppinsRegularFontFamily = FontFamily(
        Font(R.font.poppins_regular)
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = viewModel.uiState.userStatus?.wording?.textOne ?: "",
                    style = Typography.h6.copy(
                        fontFamily = poppinsRegularFontFamily,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.15.sp
                    ),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = viewModel.uiState.userStatus?.wording?.textTwo ?: "",
                    style = Typography.h5.copy(
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MultimoneyTheme.colors.labelText
                )
            }
            Row(modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            // todo action
                        },
                    contentDescription = "",
                    tint = MultimoneyTheme.colors.iconColor
                )
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(32.dp)
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
            modifier = Modifier
                .padding(start = 16.dp, top = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            viewModel = viewModel,
            sharedViewModel = sharedViewModel
        )
        Text(
            text = stringResource(id = viewModel.getProductScreenTitle()),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
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
    state: PagerState,
    viewModel: ProductViewModel,
    backPressed: () -> Unit
) {
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductContent(
    modifier: Modifier,
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    val pagerCount =
        if (viewModel.uiState.isExpanded) viewModel.uiState.productPageList?.count { it.enabled }
            ?: DEFAULT_PRODUCT_PAGES else viewModel.uiState.productPageList?.count()
            ?: DEFAULT_PRODUCT_PAGES
    Column(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier
                .padding(top = 8.dp),
            count = pagerCount,
            state = state
        ) { page ->
            when (viewModel.uiState.productPageList?.get(page)?.product) {
                ProductType.Credit.value -> CreditContent(viewModel = viewModel)
                ProductType.Smart.value -> SmartContent(viewModel = viewModel, page)
                ProductType.Crypto.value -> CryptoContent(
                    userStatus = viewModel.uiState.userStatus,
                    cryptoBalance = viewModel.balanceCredit?.balanceCryptoAccount,
                    cryptoEmptyState = viewModel.uiState.userStatus?.infoCrypto?.profileEnable
                        ?: false,
                    clientBalanceHistory = sharedViewModel.uiState.cryptoHistoricalBalance,
                    openSmartCryptoAction = {
                        viewModel.onUIEvent(OnNavigateToSmartOriginationFlow(comingFromCrypto = true))
                    }
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomDotsIndicator(
                totalDots = pagerCount,
                selectedIndex = state.currentPage,
                selectedColor = WhiteTransparency90,
                unSelectedColor = WhiteTransparency30,
                dotSize = 8.dp
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductFooter(
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
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
                onNavigateToVisaActivateScreen = {
                    viewModel.onUIEvent(
                        OnNavigateToHomeMultimoneyVisa
                    )
                },
                onCreateMultimoneyVisa = {
                    viewModel.onUIEvent(
                        OnCreateMultimoneyVisa(onLoadingValueChange = {
                            sharedViewModel.onUIEvent(
                                HomeViewModel.UIEvent.OnLoadingValueChanged(
                                    it
                                )
                            )
                        })
                    )
                }
            )
            ProductType.Smart.value -> SmartFooter()
            ProductType.Crypto.value -> CryptoFooter()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductFooterExpanded(
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
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
            ) {
                sharedViewModel.onUIEvent(UIEvent.OnLoadingValueChanged(it))
            }
            ProductType.Crypto.value -> CryptoFooterExpanded(
                userStatus = viewModel.uiState.userStatus,
                balance = viewModel.balanceCredit,
                cryptoMovements = viewModel.uiState.cryptoCurrencyMovements,
                onShowAllClick = { viewModel.onUIEvent(OnNavigateToCryptoMovements) },
                actionMarket = { viewModel.onUIEvent(OnNavigateToCryptoMarket) },
                actionWallet = { viewModel.onUIEvent(OnNavigateToCryptoWallet) }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductCtaFooterExpanded(
    state: PagerState,
    viewModel: ProductViewModel,
    sharedViewModel: HomeViewModel
) {
    HorizontalPager(
        count = viewModel.uiState.productPageList?.count() ?: DEFAULT_PRODUCT_PAGES,
        state = state,
        userScrollEnabled = false
    ) {
        when (viewModel.uiState.productPageList?.get(currentPage)?.product) {
            ProductType.Credit.value -> CreditCtaFooterExpanded(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel
            )
            ProductType.Smart.value -> SmartCtaFooterExpanded(
                viewModel = viewModel,
                currentPage
            ) {
                sharedViewModel.onUIEvent(UIEvent.OnLoadingValueChanged(it))
            }
            ProductType.Crypto.value -> CryptoCtaFooterExpanded(
                balance = viewModel.balanceCredit,
                idBrand = viewModel.uiState.idBrand,
                profileEnable = viewModel.uiState.userStatus?.infoCrypto?.profileEnable,
                noBalanceAction = {
                    when (viewModel.uiState.idBrand) {
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
                hasBalanceAction = {
                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToPurchaseCryptoFlow)
                },
                onSendActionClicked = {
                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSendCryptoFlow)
                }
            )
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
                end = 12.dp
            )
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_tip_background,
            contentScale = ContentScale.FillBounds
        )
        content()
    }
}
