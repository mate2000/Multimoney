package com.multimoney.multimoney.presentation.ui.home.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnGetProfileInfo
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnHelpClick
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnLogoutClick
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnMyAccountsClick
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnMyCardsClick
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileViewModel.UIEvent.OnSettingsClick
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ProfileScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
        viewModel.onUIEvent(OnGetProfileInfo)
    }

    BackHandler { viewModel.onUIEvent(OnNavigateBack) }
    ProfileContent()
}

@Composable
fun ProfileContent(viewModel: ProfileViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        Column(Modifier.verticalScroll(rememberScrollState())) {
            viewModel.apply {
                ProfileHeader(
                    userName = uiState.userName,
                    email = uiState.userEmail,
                    phoneNumber = uiState.phoneNumber,
                    onUpdateClick = {
                        // TODO, handle click
                    }
                )
                ProfileOptions(this)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    email: String,
    phoneNumber: String,
    onUpdateClick: () -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        text = stringResource(R.string.profile_title),
        style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
        color = MultimoneyTheme.colors.labelText,
        textAlign = TextAlign.Left
    )
    CustomInfoButton(
        title = userName,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 20.dp),
        subtitle = email,
        subtitle2 = phoneNumber,
        startIcon = null,
        endIcon = R.drawable.ic_edit_green,
        shouldCenterEndIcon = false,
        onEndIconClick = onUpdateClick
    )
}

@Composable
fun ProfileOptions(viewModel: ProfileViewModel) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        CustomItemRow(
            title = stringResource(R.string.profile_my_accounts),
            startIcon = R.drawable.ic_my_accounts,
            endIcon = R.drawable.ic_right_chevron,
            onClick = { viewModel.onUIEvent(OnMyAccountsClick) }
        )
        // as per requirement, this option should be available only for SV and GT.
        viewModel.uiState.brandId.apply {
            if (this == Brand.ElSalvador.id && this == Brand.Guatemala.id) {
                CustomItemRow(
                    title = stringResource(R.string.profile_my_cards),
                    startIcon = R.drawable.ic_card,
                    endIcon = R.drawable.ic_right_chevron,
                    onClick = { viewModel.onUIEvent(OnMyCardsClick) }
                )
            }
        }
        CustomItemRow(
            title = stringResource(R.string.profile_settings),
            startIcon = R.drawable.ic_settings,
            endIcon = R.drawable.ic_right_chevron,
            onClick = { viewModel.onUIEvent(OnSettingsClick) }
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help),
            startIcon = R.drawable.ic_help,
            endIcon = R.drawable.ic_right_chevron,
            onClick = { viewModel.onUIEvent(OnHelpClick) }
        )
        CustomItemRow(
            title = stringResource(R.string.profile_logout),
            titleFontWeight = FontWeight.SemiBold,
            customTitleColor = SemanticNegative400,
            startIcon = R.drawable.ic_logout,
            shouldShowDivider = false,
            onClick = { viewModel.onUIEvent(OnLogoutClick) }
        )
    }
}

@Preview
@Composable
fun ProfileContentPreview() {
    ProfileContent()
}
