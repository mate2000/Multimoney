package com.multimoney.multimoney.presentation.ui.payment.account

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun VisaScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopBackStack: () -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    // Navigation
//    LaunchedEffect(true) {
//        viewModel.apply {
//            executeNavigation(onPopBackStack = onPopBackStack, onPopAndNavigate = onPopAndNavigate)
//            onUIEvent(OnGetTextResources(navBackStackEntry.arguments?.getString(ID_BRAND, "") ?: ""))
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .background(MultimoneyTheme.colors.background)
//            .fillMaxSize()
//    ) {
//        Column(
//            Modifier
//                .weight(0.34f)
//        ) {
//            TopNavBar(
//                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
//                onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) })
//            Text(
//                modifier = Modifier.padding(top = 34.dp, start = 16.dp, end = 16.dp),
//                text = stringResource(id = viewModel.uiState.titleResource),
//                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
//                color = MultimoneyTheme.colors.labelText,
//                textAlign = TextAlign.Left
//            )
//            Text(
//                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
//                text = stringResource(id = viewModel.uiState.subtitleResource),
//                style = Typography.subtitle1,
//                color = MultimoneyTheme.colors.labelText,
//                textAlign = TextAlign.Left
//            )
//        }
//        Column(
//            Modifier
//                .weight(0.46f)
//        ) {
//            CustomCardVisaVertical(
//                modifier = Modifier
//                    .padding(start = 68.dp, end = 68.dp)
//                    .fillMaxSize(),
//                isTextVisible = viewModel.uiState.isTextVisible
//            )
//        }
//        Column(
//            Modifier
//                .weight(0.2f),
//            verticalArrangement = Arrangement.Bottom
//        ) {
//            CustomButton(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
//                onClick = { },
//                text = stringResource(id = R.string.activate),
//                buttonType = PrimaryPrimary,
//
//                )
//        }
//    }
}