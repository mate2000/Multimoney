package com.multimoney.multimoney.presentation.ui.visa.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnCheckedChange
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomSwitchButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun VisaPreferencesScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: VisaPreferencesViewModel = hiltViewModel(),
    sharedViewModel: HomeViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }

    VisaPreferencesContent(viewModel, sharedViewModel)
}

@Composable
@Preview
fun VisaPreferencesContent(
    viewModel: VisaPreferencesViewModel = hiltViewModel(),
    sharedViewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            rightButtonIcon = R.drawable.ic_gear
        )
        Spacer(modifier = Modifier.height(34.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(id = R.string.card_preferences_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.labelText
            ),
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(Modifier.fillMaxSize()) {
            VisaPreferencesComponent(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                iconResource = if (viewModel.uiState.switchButtonValue) {
                    R.drawable.ic_links
                } else {
                    R.drawable.ic_unlinked
                },
                titleText = stringResource(
                    if (viewModel.uiState.switchButtonValue) {
                        R.string.card_preferences_linked_card_title
                    } else {
                        R.string.card_preferences_unlinked_card_title
                    }
                ),
                titleColor = MultimoneyTheme.colors.titleText,
                descriptionText = stringResource(R.string.card_preferences_linked_card_description),
                hasEndButton = true,
                onDeleteTokenBaseEvent = {
                    sharedViewModel.onUIEvent(HomeViewModel.UIEvent.OnShowUnlinkToast)
                }
            )
        }
    }
    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction,
            isCancelable = false
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun VisaPreferencesComponent(
    viewModel: VisaPreferencesViewModel,
    modifier: Modifier,
    iconResource: Int,
    titleText: String,
    titleColor: Color,
    descriptionText: String,
    hasEndButton: Boolean = false,
    onDeleteTokenBaseEvent: () -> Unit = { }
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (startIcon, title, description, endButton) = createRefs()
        Image(
            modifier = Modifier.constrainAs(startIcon) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            },
            painter = painterResource(id = iconResource),
            contentDescription = ""
        )
        Text(
            modifier = Modifier.constrainAs(title) {
                start.linkTo(startIcon.end, margin = 12.dp)
                top.linkTo(parent.top)
            },
            text = titleText,
            style = Typography.body1.copy(
                color = titleColor
            )
        )
        if (hasEndButton) {
            CustomSwitchButton(
                modifier = Modifier.wrapContentHeight().constrainAs(endButton) {
                    top.linkTo(title.top)
                    bottom.linkTo(title.bottom)
                    end.linkTo(parent.end)
                },
                checked = viewModel.uiState.switchButtonValue
            ) { value ->
                viewModel.onUIEvent(OnCheckedChange(value, onDeleteTokenBaseEvent))
            }
        }
        Text(
            modifier = Modifier.constrainAs(description) {
                top.linkTo(title.bottom, margin = 12.dp)
                start.linkTo(title.start)
            },
            text = descriptionText,
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.subTitleText
            )
        )
    }
}
