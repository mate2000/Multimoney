package com.multimoney.multimoney.presentation.ui.home.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale200
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_IDENTITY_INCOMPLETE
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_INFO_INCOMPLETE
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_INITIAL_CARD
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_MAX_ATTEMPTS
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_REJECTED
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetIdBrand
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToVisaActivateScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardCreditMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardGTWithoutCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardOfferSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStarted
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStartedStatus.CreditStatusApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditDetail
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.OngoingCredit
import com.multimoney.multimoney.presentation.ui.home.product.skeleton.ProductScreenSkeleton
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.test.motionlayout.MotionLayoutMM
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.CreditCard
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.RequestCreditCard
import com.multimoney.multimoney.presentation.uielement.CustomBoxVisaBackground
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDotsIndicator
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel()
) {
    viewModel.apply {
        isOnRestart = isRestart
        DisposableEffect(isOnRestart) {
            if (isOnRestart) {
                onUIEvent(OnGetIdBrand)
                executeNavigation(onNavigate = onNavigate)
            }
            onDispose {
                isOnRestart = false
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is ProductViewModel.BaseEvent.OnStartCountDownTimer -> viewModel.countDownTimer.startTimer(event.millisInFuture)
            }
        }
    }

    // Pager
    val productPagerState = rememberPagerState()
    val bottomPagerState = rememberPagerState()

    LaunchedEffect(key1 = productPagerState.currentPage) {
        bottomPagerState.animateScrollToPage(productPagerState.currentPage)
    }

    // todo we have to send the pages to the view pager when the back return
    if (viewModel.uiState.isLoading) {
        ProductScreenSkeleton()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            MotionLayoutMM(mainHeader = {
                TipsAndOffer(
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp),
                    viewModel = viewModel
                )
            }, secondaryHeader = { backPressed ->
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MultimoneyTheme.colors.background)
                ) {
                    TopNavBar(
                        isRightButtonVisible = false,
                        onLeftButtonClick = {
                            backPressed()
                        }
                    )
                }
            }, content = { modifier, headerText ->
                Products(
                    modifier = modifier,
                    pages = NUMBER_PAGES,
                    state = productPagerState,
                    viewModel = viewModel,
                    headerText
                )
            }, footer = {
                ProductExtras(
                    modifier = Modifier.padding(top = 16.dp),
                    pages = NUMBER_PAGES,
                    state = bottomPagerState,
                    viewModel = viewModel
                )
            }, secondaryFooter = {
                ConstraintLayout(
                    Modifier.fillMaxSize()
                ) {
                    val (content, buttons) = createRefs()

                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .constrainAs(content) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(buttons.top)
                                height = Dimension.fillToConstraints
                            }
                    ) {
                        Divider(color = MultimoneyTheme.colors.dividerWhite30)
                        Spacer(modifier = Modifier.height(24.dp))
                        CreditCardView(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(24.dp))
                        CreditDetail(
                            modifier = Modifier
                                .background(MultimoneyTheme.colors.creditDetailBackground)
                                .wrapContentSize(),
                            viewModel = viewModel
                        )
                    }

                    CtaButtons(
                        modifier = Modifier
                            .padding(16.dp)
                            .constrainAs(buttons) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            },
                        onClickPay = { viewModel.onUIEvent(OnNavigateToPaymentProcess) },
                        onClickDisbursement = { viewModel.onUIEvent(OnNavigateToCreditScreen) },
                        canDisburse = viewModel.uiState.hasBalance
                    )
                }
            }, totalPages = NUMBER_PAGES)
        }
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun CtaButtons(
    canDisburse: Boolean,
    modifier: Modifier,
    onClickPay: () -> Unit = {},
    onClickDisbursement: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (canDisburse) {
            CustomButton(
                text = stringResource(R.string.home_pay_fee_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                buttonType = CustomButtonType.PrimarySecondary,
                onClick = {
                    onClickPay()
                }
            )
            Spacer(Modifier.width(16.dp))
            CustomButton(
                text = stringResource(R.string.home_disburse_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    onClickDisbursement()
                }
            )
        } else {
            CustomButton(
                text = stringResource(R.string.home_pay_fee_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    onClickPay()
                }
            )
        }
    }
}

@Composable
fun TipsAndOffer(modifier: Modifier, viewModel: ProductViewModel) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Buen día",
                    style = Typography.h6.copy(letterSpacing = 0.38.sp),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "User Name",
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
                    painter = painterResource(R.drawable.ic_notification),
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
                            // todo action
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
            items(items = viewModel.getCreditOfferAndTips(), itemContent = {
                TipAndOfferItem(viewModel, it)
            })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Products(
    modifier: Modifier,
    pages: Int,
    state: PagerState,
    viewModel: ProductViewModel,
    headerText: Int
) {
    Column(modifier = modifier) {
        // TODO add dynamic titles when other products are implemented
        Text(
            text = stringResource(id = if (headerText == 0) R.string.home_credit_title else headerText),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = if (headerText == 0) Typography.h5.copy(fontWeight = FontWeight.SemiBold) else Typography.body1.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MultimoneyTheme.colors.labelText
        )
        if (pages == 1) {
            CreditProduct(viewModel = viewModel)
        } else {
            val pagesSize = (viewModel.balanceCredit?.balanceCredit?.size
                ?: 0) + (viewModel.balanceCredit?.balanceAccountSmart?.size ?: 0)
            HorizontalPager(
                count = pagesSize,
                modifier = Modifier.padding(top = 8.dp),
                state = state
            ) {
                // todo add the logic for the others pages
                if (currentPage <= (viewModel.balanceCredit?.balanceCredit?.lastIndex ?: 0)) {
                    CreditProduct(viewModel = viewModel)
                } else {
                    viewModel.balanceCredit?.balanceAccountSmart?.let {
                        if (it.isNotEmpty()) {
                            it.forEach {
                                CustomProductBackground(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    type = Primary
                                ) {
                                    CardSmartProduct(
                                        brandId = viewModel.uiState.idBrand.toInt(),
                                        profitMonthly = it?.gainedInterest.toString(),
                                        profitTotal = it?.totalBalance.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Row(Modifier.padding(horizontal = 180.dp)) {
                CustomDotsIndicator(
                    totalDots = pages,
                    selectedIndex = state.currentPage,
                    selectedColor = GrayScale200,
                    unSelectedColor = GrayScale600,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
fun CreditProduct(viewModel: ProductViewModel) {
    val context = LocalContext.current
    val whatsAppLink = stringResource(
        id = R.string.whatsapp_deep_link,
        SignUpViewModel.PHONE_HARDCODED
    )

    viewModel.uiState.userStatus?.apply {
        when (infoCredit?.status) {
            CreditStatus.EXIST_IN_CORE.status -> {
                CustomProductBackground(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    type = Primary
                ) {
                    OngoingCredit(viewModel)
                }
            }
            CreditStatus.APPROVED_CREDIT.status, CreditStatus.CREDIT_PRE_APPROVED.status -> {
                CustomProductBackground(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    type = Primary
                ) {
                    when {
                        viewModel.evaluateCardCondition(CREDIT_INITIAL_CARD, this) -> {
                            val infoPreApprove =
                                viewModel.uiState.userStatus?.infoCredit?.infoPreApprove?.infoProducts?.first()
                            CreditApprovedOrStarted(
                                creditApprovedOrStartedStatus = CreditStatusApproved,
                                infoPreApprove?.amountAvailableFormat,
                                viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(CREDIT_MAX_ATTEMPTS, this) -> {
                            CardCreditMaxAttempts(
                                action = {
                                    viewModel.onUIEvent(
                                        ProductViewModel.UIEvent.OnMaxAttemptsCardClick(
                                            whatsAppLink = whatsAppLink,
                                            context = context
                                        )
                                    )
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(CREDIT_IDENTITY_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessOnFidoIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(CREDIT_INFO_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen)
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(CREDIT_REJECTED, this) -> {
                            CardWithCreditInProcess(
                                type = CreditStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        else -> {
                            CardOfferSmartProduct()
                        }
                    }
                }
            }
            CreditStatus.CREDIT_REJECTED.status, CreditStatus.CREDIT_NOT_PRE_APPROVED.status -> {
                when (viewModel.uiState.idBrand) {
                    Brand.Guatemala.id.toString() -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardGTWithoutCredit(action = {
                                viewModel.onUIEvent(
                                    OnProductClick(
                                        whatsAppLink,
                                        context
                                    )
                                )
                            })
                        }
                    }
                    else -> {
                        // no show card
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductExtras(
    modifier: Modifier,
    pages: Int = 1,
    state: PagerState,
    viewModel: ProductViewModel
) {
    Column(modifier = modifier) {
        HorizontalPager(count = pages, state = state) {
            CreditCardView(viewModel = viewModel)
        }
    }
}

@Composable
fun CreditCardView(viewModel: ProductViewModel) {
    if (viewModel.uiState.userStatus?.infoCredit?.status == CreditStatus.EXIST_IN_CORE.status) {
        viewModel.balanceCredit?.balanceCardInformation?.cardInformation?.let { cardInformation ->
            CustomBoxVisaBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    viewModel.onUIEvent(OnNavigateToVisaActivateScreen)
                },
                type = CreditCard(cardInformation.cardNumber ?: "")
            )
        } ?: run {
            CustomBoxVisaBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    viewModel.onUIEvent(OnNavigateToVisaActivateScreen)
                },
                type = RequestCreditCard
            )
        }
    }
}

@Composable
fun TipAndOfferItem(viewModel: ProductViewModel, creditOfferAndTip: CreditOfferAndTip) {
    TipBox {
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    // TODO: Call appropriate screen when all flows are available
                    // TODO, mocking the first item in order to navigate to the smart origination flow
                    if (creditOfferAndTip.id == "1") {
                        viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow)
                    } else {
                        viewModel.onUIEvent(OnNavigateToCreditScreen)
                    }
                }
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_logo_multimoney,
                modifier = Modifier
                    .size(54.dp, 54.dp)
                    .align(Alignment.BottomEnd)
            )
            Column {
                Text(
                    text = creditOfferAndTip.title,
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    text = creditOfferAndTip.description,
                    modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.labelText,
                    maxLines = 2
                )
                Text(
                    text = creditOfferAndTip.actionName,
                    modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.tipActionColor
                )
            }
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

@Composable
fun ProductDetails(viewModel: ProductViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.home_product_movement_title),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(0.6f),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            ClickableText(
                text = AnnotatedString(stringResource(R.string.home_product_check_all)),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(0.4f),
                style = Typography.button.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.labelText
                ),
                onClick = {
                    // TODO implement event when views added
                }
            )
            LazyColumn {
                items(viewModel.getProductMovement()) { movement ->
                    ProductMovement(
                        title = movement.title,
                        date = movement.date,
                        value = movement.amount
                    )
                }
            }
        }
    }
}

@Composable
fun ProductMovement(title: String, date: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 8.dp)
    ) {
        Column(modifier = Modifier.weight(0.8f)) {
            Text(
                text = title,
                style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = date,
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
        }
        Row(Modifier.weight(0.2f)) {
            CustomImage(
                modifier = Modifier.align(Alignment.CenterVertically),
                drawableResource = R.drawable.ic_close
            )
            Text(
                text = value,
                style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
        }
    }
    Divider(
        color = MultimoneyTheme.colors.bottomNavigationDividerColor,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}

private const val NUMBER_PAGES = 2
private const val PAGE_ZERO = 0
