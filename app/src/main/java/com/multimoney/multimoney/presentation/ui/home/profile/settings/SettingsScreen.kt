package com.multimoney.multimoney.presentation.ui.home.profile.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency16
import com.multimoney.multimoney.presentation.theme.WhiteTransparency50
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SettingsScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    BackHandler {
        viewModel.onUIEvent(SettingsViewModel.UIEvent.OnNavigateBack)
    }
    SettingsContent(viewModel)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun SettingsContent(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(SettingsViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_settings),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            BiometricsCustomRow(viewModel)

            CustomItemRow(
                title = stringResource(R.string.profile_change_password),
                startIcon = R.drawable.ic_locked_gray,
                endIcon = R.drawable.ic_right_chevron,
                onClick = {
                }
            )
        }
    }
}

@Composable
fun BiometricsCustomRow(viewModel: SettingsViewModel) {
    val dividerColor: Color
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        dividerColor = WhiteTransparency50
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    } else {
        dividerColor = WhiteTransparency50
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    ) {
        val (textColumn, switch) = createRefs()
        Column(modifier = Modifier.constrainAs(textColumn) {

        }) {
            Text(
                text = stringResource(id = R.string.profile_biometrics),
                style = Typography.body2,
                color = titleColor
            )
            Text(
                text = stringResource(id = R.string.profile_enter_the_app_with_biometrics),
                style = Typography.body2.copy(fontWeight = FontWeight.ExtraLight),
                color = subtitleColor
            )
        }
        Switch(
            modifier = Modifier.constrainAs(switch) {
                end.linkTo(parent.end)
            },
            checked = viewModel.uiState.areBiometricsEnabled ?: false,
            onCheckedChange = { newState ->
                if (!newState) {
                    viewModel.onUIEvent(SettingsViewModel.UIEvent.OnShowConfirmationDialog)
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Primary500,
                uncheckedThumbColor = Color.White,
                checkedTrackColor = Primary300,
                uncheckedTrackColor = WhiteTransparency10
            )
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Divider(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth(), dividerColor
    )
}

