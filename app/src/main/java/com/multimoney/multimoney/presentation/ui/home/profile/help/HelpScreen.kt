package com.multimoney.multimoney.presentation.ui.home.profile.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnCallToAttentionCenterClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnChatWithUsClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnFAQClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnGetContactInfo
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnTermsAndConditionsClick
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun HelpScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    viewModel: HelpScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack
        )
        viewModel.onUIEvent(OnGetContactInfo)
    }

    HelpScreenContent(viewModel)
}

@Composable
fun HelpScreenContent(viewModel: HelpScreenViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        HelpOptions(
            onChatWithUsClick = { viewModel.onUIEvent(OnChatWithUsClick) },
            onCallAttentionCenterClick = { viewModel.onUIEvent(OnCallToAttentionCenterClick) },
            onFAQClick = { viewModel.onUIEvent(OnFAQClick) },
            onTermsAndConditions = { viewModel.onUIEvent(OnTermsAndConditionsClick) }
        )
    }
}

@Composable
fun HelpOptions(
    onChatWithUsClick: () -> Unit,
    onCallAttentionCenterClick: () -> Unit,
    onFAQClick: () -> Unit,
    onTermsAndConditions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CustomItemRow(
            title = stringResource(R.string.profile_help_and_info_title),
            startIcon = R.drawable.ic_chat_bot,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onChatWithUsClick,
            startIconColor = MultimoneyTheme.colors.text
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_call_attention_center_label),
            startIcon = R.drawable.ic_phone,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onCallAttentionCenterClick,
            startIconColor = MultimoneyTheme.colors.text
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_faq_label),
            startIcon = R.drawable.ic_help,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onFAQClick
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_terms_and_conditions_label),
            startIcon = R.drawable.ic_document,
            shouldShowDivider = false,
            onClick = onTermsAndConditions
        )
    }
}
