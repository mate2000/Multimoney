package com.multimoney.multimoney.presentation.ui.visa.novotokenization

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.BaseEvent.OnOpenTapAndPayConfig
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.Companion.TIME_TO_WAITING_NOVO_STEP
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGetAndroidId
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnNavigateToHomeVisa
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnShowSuccessTokenizationScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnStartNovoTokenization
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LockScreenOrientation
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getAndroidId
import com.multimoney.multimoney.presentation.util.getTapAndPayIntent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun VisaTokenizationWaitingScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: VisaTokenizationWaitingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val color = MultimoneyTheme.colors.text
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val launch = rememberLauncherForActivityResult(contract = StartActivityForResult(), onResult = {
        viewModel.onUIEvent(OnShowSuccessTokenizationScreen)
    })

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
        viewModel.onUIEvent(OnGetAndroidId(context.getAndroidId()))
        viewModel.onUIEvent(OnGoToNextScreen(context, color))
        viewModel.onUIEvent(OnStartNovoTokenization)
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { baseEvent ->
            when (baseEvent) {
                is OnOpenTapAndPayConfig -> launch.launch(context.getTapAndPayIntent())
            }
        }
    }

    val changeStepDebounce = remember { MutableStateFlow(false) }
    val changeStepFlow: Flow<Boolean> = remember {
        changeStepDebounce.debounce(TIME_TO_WAITING_NOVO_STEP).onEach { status ->
            changeStepDebounce.value = changeStepDebounce.value.not()
            viewModel.onUIEvent(OnGoToNextScreen(context, color))
            flowOf(status)
        }
    }

    if (viewModel.uiState.isAlertResultVisible) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = alertResultDescriptionResource,
                buttonTextResource = alertResultButtonResource,
                isLeftButtonVisible = false,
                isRightButtonVisible = true,
                onRightButtonClick = { viewModel.onUIEvent(OnAlertCloseClick) },
                onButtonClick = { viewModel.onUIEvent(OnAlertButtonClick) }
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(MultimoneyTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally
            ) {
                CustomImage(
                    drawableResource = viewModel.uiState.icon
                )
                Text(
                    text = viewModel.uiState.description,
                    modifier = Modifier.padding(all = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 40.dp).align(BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp, 32.dp),
                    color = MultimoneyTheme.colors.circularProgressIndicator
                )
                Text(
                    text = stringResource(id = R.string.visa_tokenization_waiting_loading_label),
                    modifier = Modifier.padding(start = 12.dp),
                    color = MultimoneyTheme.colors.text,
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }

    if (viewModel.uiState.showSuccessTokenizationScreen) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = R.drawable.ic_success_symbol,
                titleResource = R.string.visa_tokenization_success_title,
                descriptionResource = viewModel.getDescriptionTokenizationSuccess(),
                buttonTextResource = R.string.visa_tokenization_success_button_action,
                isLeftButtonVisible = false,
                isRightButtonVisible = true,
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHomeVisa) },
                onButtonClick = { viewModel.onUIEvent(OnNavigateToHomeVisa) }
            )
        }
    }

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

    // this is necessary to block the systems back button
    BackHandler(onBack = {})

    // this is required to execute the debounce
    val openStepFlowValue by changeStepFlow.collectAsState(false)
}
