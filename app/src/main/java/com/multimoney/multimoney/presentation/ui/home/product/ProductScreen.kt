package com.multimoney.multimoney.presentation.ui.home.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.CREDIT_STEP_PRE_APPROVED
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnGetIdBrand
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardCreditMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardGTWithoutCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.credit.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStarted
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStartedStatus.CreditStatusApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.skeleton.ProductScreenSkeleton
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.RequestCreditCard
import com.multimoney.multimoney.presentation.uielement.CustomBoxVisaBackground
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary
import com.multimoney.multimoney.presentation.util.NavEvent

@OptIn(ExperimentalPagerApi::class)
@Composable
@Preview
fun ProductScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnGetIdBrand)
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate)
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
            TipsAndOffer(
                modifier = Modifier.padding(start = 16.dp, top = 20.dp),
                pages = NUMBER_PAGES,
                viewModel = viewModel
            )
            Products(
                modifier = Modifier.padding(top = 25.dp),
                pages = NUMBER_PAGES,
                state = productPagerState,
                viewModel = viewModel
            )
            ProductExtras(
                modifier = Modifier.padding(top = 32.dp),
                pages = NUMBER_PAGES,
                state = bottomPagerState,
                viewModel = viewModel
            )
        }
    }
}

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
            items(items = viewModel.getCreditOfferAndTips(), itemContent = { item ->
                TipAndOfferItem(item, viewModel)
            })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Products(modifier: Modifier, pages: Int, state: PagerState, viewModel: ProductViewModel) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.home_my_products),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        HorizontalPager(
            count = pages,
            modifier = Modifier.padding(top = 8.dp),
            state = state
        ) { page ->
            // todo add the logic for the others pages
            CreditProduct(viewModel = viewModel)
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
            CreditStatus.APPROVED_CREDIT.status, CreditStatus.CREDIT_PRE_APPROVED.status -> {
                CustomProductBackground(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    onClick = { viewModel.onUIEvent(OnProductClick(whatsAppLink, context)) },
                    type = Primary
                ) {
                    when {
                        hasToShowCreditInitialCard(this) -> {
                            CreditApprovedOrStarted(
                                creditApprovedOrStartedStatus = CreditStatusApproved,
                                viewModel.uiState.userStatus?.infoCredit?.infoPreApprove?.infoProducts?.first()?.amountAvailable
                            )
                        }
                        infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.OVER_COUNTER.status -> {
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
                        (infoUser?.statusOnfido != CreditOnFidoOrFirmStatus.APPROVED.status) && (CreditStep.Search.getIdByName(
                            infoCredit?.infoPreApprove?.currentStep
                        ) == CreditStep.Six.id) -> CardWithCreditInProcess(type = CreditProcessOnFidoIncomplete)
                        infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.REJECTED.status -> CardWithCreditInProcess(
                            type = CreditStartProcessIncomplete
                        )
                        else -> CardSmartProduct()
                    }
                }
            }
            CreditStatus.CREDIT_REJECTED.status, CreditStatus.CREDIT_NOT_PRE_APPROVED.status -> {
                when (viewModel.uiState.idBrand) {
                    Brand.Guatemala.id.toString() -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            onClick = { viewModel.onUIEvent(OnProductClick(whatsAppLink, context)) },
                            type = Primary
                        ) {
                            CardGTWithoutCredit()
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

fun hasToShowCreditInitialCard(validateUserStatus: ValidateUserStatus?): Boolean {
    return validateUserStatus?.infoUser?.statusOnfido == CreditOnFidoOrFirmStatus.PENDING.status
            && validateUserStatus.infoCredit?.infoPreApprove?.statusFirm == CreditOnFidoOrFirmStatus.PENDING.status
            && (validateUserStatus.infoCredit?.infoPreApprove?.currentStep.isNullOrEmpty() || validateUserStatus.infoCredit?.infoPreApprove?.currentStep == CREDIT_STEP_PRE_APPROVED)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductExtras(modifier: Modifier, pages: Int, state: PagerState, viewModel: ProductViewModel) {
    Column(modifier = modifier) {
        HorizontalPager(count = pages, state = state) { page ->
            if (viewModel.uiState.userStatus?.infoCredit?.status != CreditStatus.CREDIT_REJECTED.status) {
                CustomBoxVisaBackground(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { type ->
                        // todo Add logic when the user click the button
                        // TODO: Remove this button when all functionalities are implemented
                        viewModel.popAndNavigateTo(
                            route = "${Screen.AlertResultScreen.baseRoute}/${R.drawable.ic_alert}/${R.string.sign_document_reject_title}/${R.string.sign_document_reject_description}/${R.string.understood}",
                            popTo = Screen.AlertResultScreen.baseRoute
                        )
                    },
                    type = RequestCreditCard
                )
            }
        }
    }
}

@Composable
fun TipAndOfferItem(tipOrOffer: CreditOfferAndTip, viewModel: ProductViewModel) {
    TipBox(type = tipOrOffer.type) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    // TODO: Call appropriate screen when all flows are available
                    viewModel.onUIEvent(OnNavigateToCreditScreen)
                }) {
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
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    text = "La mejor tasa del 3.5% anual",
                    modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.labelText,
                    maxLines = 2
                )
                Text(
                    text = "Solicitar",
                    modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp),
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
        CustomImage(
            drawableResource = R.drawable.ic_tip_background,
            contentScale = ContentScale.FillBounds
        )
        content()
    }
}

private const val NUMBER_PAGES = 1