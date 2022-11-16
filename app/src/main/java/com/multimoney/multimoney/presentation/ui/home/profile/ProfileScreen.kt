package com.multimoney.multimoney.presentation.ui.home.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
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
        viewModel.onUIEvent(ProfileViewModel.UIEvent.OnGetProfileInfo)
    }

    ProfileContent()
    BackHandler {
        viewModel.onUIEvent(ProfileViewModel.UIEvent.OnNavigateBack)
    }
}

@Preview
@Composable
fun ProfileContent(viewModel: ProfileViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(ProfileViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        viewModel.apply {
            ProfileHeader(
                userName = uiState.userName,
                email = uiState.userEmail,
                phoneNumber = uiState.phoneNumber,
                onUpdateClick = {
                    // TODO, handle click
                }
            )
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
        text = stringResource(R.string.my_profile_title),
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
