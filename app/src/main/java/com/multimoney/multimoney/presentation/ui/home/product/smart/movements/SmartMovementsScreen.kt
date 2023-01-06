package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnErrorLoading
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnIsLoadingChange
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.PagingLoadStateView
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun SmartMovementsScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartMovementsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
        viewModel.isOnRestart = false
        viewModel.onUIEvent(OnGetMovement)
    }
    val smartMoves = viewModel.uiState.movementsPage.collectAsLazyPagingItems()

    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(16.dp)
    ) {
        TopNavBar(
            isLeftButtonVisible = true,
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBackToHome) }
        )

        Text(
            text = stringResource(R.string.home_product_movement_title),
            style = Typography.h4.copy(
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = viewModel.uiState.openDialog.description.ifBlank {
                    stringResource(viewModel.uiState.openDialog.descriptionResource)
                },
                positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction
            )
        }
        PagingLoadStateView(
            loadState = smartMoves.loadState,
            onLoad = {
                viewModel.onUIEvent(OnIsLoadingChange(it))
            },
            onError = {
                viewModel.onUIEvent(
                    OnErrorLoading(
                        failureDialog = DialogParameters(
                            titleResource = R.string.error,
                            description = it,
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
        )
        LoadingIndicator(viewModel.uiState.isLoading)
        MovementsList(smartMoves)
    }

    BackHandler {
        viewModel.onUIEvent(OnNavigateBackToHome)
    }
}

@Composable
fun MovementsList(smartMoves: LazyPagingItems<SmartMovement>) {
    LazyColumn {
        items(items = smartMoves) {
            it?.let {
                SmartMovementDisplayer(it)
            }
        }
    }
}
