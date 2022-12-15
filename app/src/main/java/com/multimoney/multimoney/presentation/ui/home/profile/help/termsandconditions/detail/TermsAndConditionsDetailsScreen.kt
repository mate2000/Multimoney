package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.detail

import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ireward.htmlcompose.HtmlText
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.parseApiDateToTermsAndConditionsDateTime

@Composable
fun TermsAndConditionsDetailsScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: TermsAndConditionsDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }
    BackHandler {
        viewModel.onUIEvent(TermsAndConditionsDetailsViewModel.UIEvent.OnNavigateBack)
    }
    TermsAndConditionsDetailsContent(viewModel)
}

@Composable
fun TermsAndConditionsDetailsContent(viewModel: TermsAndConditionsDetailsViewModel) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(TermsAndConditionsDetailsViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                text = stringResource(
                    id = R.string.profile_terms_and_conditions_title_template,
                    viewModel.uiState.title ?: ""
                ),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        id = R.string.profile_terms_and_conditions_version_template,
                        viewModel.uiState.version.orEmpty()
                    ),
                    style = Typography.body2.copy(fontWeight = FontWeight.ExtraLight),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    parseApiDateToTermsAndConditionsDateTime(viewModel.uiState.dateSigned.orEmpty()),
                    style = Typography.body2.copy(fontWeight = FontWeight.ExtraLight),
                    color = MultimoneyTheme.colors.labelText
                )
            }

            HtmlText(
                modifier = Modifier.padding(top = 16.dp),
                text = viewModel.uiState.html ?: "",
                style = TextStyle(color = MultimoneyTheme.colors.text)
            )
        }
    }
}