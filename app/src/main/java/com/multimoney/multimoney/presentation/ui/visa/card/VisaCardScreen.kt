package com.multimoney.multimoney.presentation.ui.visa.card

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigatePreferences
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnOpenDialogConfirmToStartTokenizationProcess
import com.multimoney.multimoney.presentation.uielement.CustomButtonBig
import com.multimoney.multimoney.presentation.uielement.CustomCardVisaVertical
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
@Preview
fun VisaCardScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: VisaCardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopBackStack = onPopBackStack,
            onPopAndNavigate = onPopAndNavigate
        )
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        if (true/*viewModel.deviceHasNFC()*/) {
            TopNavBar(
                rightButtonIcon = R.drawable.ic_gear,
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onRightButtonClick = {
                    viewModel.onUIEvent(OnNavigatePreferences)
                }
            )
        } else {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isRightButtonVisible = false
            )
        }

        CustomCardVisaVertical(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 42.dp, bottom = 16.dp)
                .fillMaxWidth(),
            isTextVisible = viewModel.uiState.isTextVisible
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomInformativeChip(
                text = stringResource(
                    id = R.string.visa_card_available_amount,
                    viewModel.availableBalanceLabel.orEmpty()
                ),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                onClick = {
                    viewModel.onUIEvent(UIEvent.OnAvailableAmountClick)
                },
                shape = RoundedCornerShape(24.dp),
                background = MultimoneyTheme.colors.backgroundInformativeChip,
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Large
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (viewModel.uiState.isNfcAvailable && viewModel.uiState.isCardTokenize.not()) {
                CustomButtonBig(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    icon = R.drawable.ic_link,
                    text = stringResource(id = R.string.link),
                    onClick = {
                        viewModel.onUIEvent(OnOpenDialogConfirmToStartTokenizationProcess)
                    }
                )
            }
            CustomButtonBig(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                icon = R.drawable.ic_eye,
                text = stringResource(id = R.string.see_data),
                onClick = {
                    // TODO: Execute action when implemented
                    Toast.makeText(
                        context,
                        "TBD2",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
            CustomButtonBig(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                icon = R.drawable.ic_locked,
                text = stringResource(id = R.string.locked),
                onClick = {
                    // TODO: Execute action when implemented
                    Toast.makeText(
                        context,
                        "TBD3",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource).ifEmpty { viewModel.uiState.dialogParameters.description },
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}
