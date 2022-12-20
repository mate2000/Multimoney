package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.parseApiDateToTermsAndConditionsDateTime

@Composable
fun TermsAndConditionsScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: TermsAndConditionsViewModel = hiltViewModel()
) {
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(true) {
        viewModel.onUIEvent(TermsAndConditionsViewModel.UIEvent.OnStart(isDarkTheme))
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }
    BackHandler {
        viewModel.onUIEvent(TermsAndConditionsViewModel.UIEvent.OnNavigateBack)
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

    TermsAndConditionsContent(viewModel)
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun TermsAndConditionsContent(viewModel: TermsAndConditionsViewModel) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(TermsAndConditionsViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                text = stringResource(id = R.string.profile_terms_and_conditions_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )

            if (viewModel.uiState.termsAndConditionsSigned?.items != null && viewModel.uiState.termsAndConditionsSigned?.items?.isNotEmpty() == true) {
                viewModel.uiState.termsAndConditionsSigned?.items?.onEach { item ->
                    val title = stringResource(id = viewModel.getStringResource(item.type))
                    CustomInfoButton(
                        startIcon = null,
                        title = title,
                        subtitle = parseApiDateToTermsAndConditionsDateTime(item.dateSigned),
                        subtitle2 = item.version,
                        onClick = {
                            viewModel.onUIEvent(
                                TermsAndConditionsViewModel.UIEvent.OnTermsAndConditionsClicked(
                                    title,
                                    item.html,
                                    item.version,
                                    item.dateSigned
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}