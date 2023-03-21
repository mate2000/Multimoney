package com.multimoney.multimoney.presentation.ui.credit.movements

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnDownloadMovements
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnErrorLoading
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnIsLoadingChange
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.ui.credit.movements.workmanager.DownloadCreditMovementsWorker
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.PagingLoadStateView
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun CreditMovementsScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: CreditMovementsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission.value = isGranted
            viewModel.onUIEvent(OnDownloadMovements)
        }
    )

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
        viewModel.isOnRestart = false
        viewModel.onUIEvent(OnGetMovement)
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is CreditMovementsViewModel.BaseEvent.OnStartDownloadCreditMovementsWorker -> {
                    WorkManager.getInstance(context).enqueueUniqueWork(
                        DownloadCreditMovementsWorker.DOWNLOAD_CREDIT_MOVEMENTS_WORKER_NAME,
                        ExistingWorkPolicy.REPLACE,
                        event.oneTimeRequest
                    )
                }
            }
        }
    }

    val creditMoves = viewModel.uiState.movementsPage.collectAsLazyPagingItems()

    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(16.dp)
    ) {
        TopNavBar(
            isLeftButtonVisible = false,
            isRightButtonVisible = true,
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBackToHome) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.90f)) {
                Text(
                    text = stringResource(R.string.home_product_movement_title),
                    style = Typography.h5.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Left
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
                    loadState = creditMoves.loadState,
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
                MovementsList(creditMoves)
            }
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else viewModel.onUIEvent(OnDownloadMovements)
                },
                text = stringResource(id = R.string.credit_movements_download_button),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isLoading.not()
            )
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        viewModel.onUIEvent(OnNavigateBackToHome)
    }
}

@Composable
fun MovementsList(creditMoves: LazyPagingItems<CreditMovement>) {
    LazyColumn {
        items(items = creditMoves) {
            it?.let {
                CreditMovementItem(it)
            }
        }
    }
}
