package com.multimoney.multimoney.presentation.ui.credit.payment.points

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsViewModel.UIEvent.OnGetPaymentPoints
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.CustomSearchBar
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentPointsScreen(
    isRestart: Boolean,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentPointsViewModel = hiltViewModel()
) {
    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(OnGetPaymentPoints)
                isOnRestart = false
            }
        }
    }

    BackHandler {
        viewModel.onUIEvent(UIEvent.OnNavigateBack)
    }
    PaymentPointsContent(viewModel)
}

@Composable
@Preview
fun PaymentPointsContent(
    viewModel: PaymentPointsViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnCloseScreenClick) }
        )
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(42.dp))
            Text(
                text = stringResource(id = R.string.payment_points_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomSearchBar(
                modifier = Modifier.padding(top = 24.dp),
                value = viewModel.uiState.queryValue,
                placeHolder = stringResource(id = R.string.payment_points_search),
                onValueChange = {
                    viewModel.onUIEvent(UIEvent.OnQueryValueChange(it))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(modifier = Modifier) {
                items(items = viewModel.uiState.pointsItemsList) { point ->
                    if (
                        (point?.name?.contains(viewModel.uiState.queryValue, true) == true) ||
                        (point?.description?.contains(viewModel.uiState.queryValue, true) == true)
                    ) {
                        CustomItemRow(
                            title = point.name ?: "",
                            subtitle = point.description ?: "",
                            onClick = {
                                viewModel.onUIEvent(
                                    UIEvent.OnItemPointClick(
                                        point.name ?: "",
                                        point.address ?: "",
                                        point.addressDescription ?: "",
                                        point.schedule ?: ""
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction,
            onNegativeAction = viewModel.uiState.dialogParameters.negativeAction
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}
