package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PersonalInfoScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: PersonalInfoViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
        //viewModel.onUIEvent(ProfileViewModel.UIEvent.OnGetProfileInfo)
    }

    BackHandler {
        viewModel.onUIEvent(PersonalInfoViewModel.UIEvent.OnNavigateBack)
    }
        PersonalInfoContent(viewModel)
}


@Composable
fun PersonalInfoContent(viewModel : PersonalInfoViewModel){
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(PersonalInfoViewModel.UIEvent.OnNavigateBack)
                                },
            isRightButtonVisible = false
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_personal_info_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            CustomItemRow(
                title = stringResource(R.string.profile_change_email_address),
                startIcon = R.drawable.ic_envelope,
                endIcon = R.drawable.ic_right_chevron,
                onClick = { }
            )
            CustomItemRow(
                title = stringResource(R.string.profile_change_phone),
                startIcon = R.drawable.ic_phone,
                endIcon = R.drawable.ic_right_chevron,
                onClick = {
                    viewModel.onUIEvent(PersonalInfoViewModel.UIEvent.OnNavigateToEditPhone)
                }
            )
//            viewModel.apply {
//                ProfileHeader(
//                    userName = uiState.userName,
//                    email = uiState.userEmail,
//                    phoneNumber = uiState.phoneNumber,
//                    onUpdateClick = { onUIEvent(ProfileViewModel.UIEvent.OnUpdateProfileClick) }
//                )
//                ProfileOptions(
//                    uiState = uiState,
//                    onMyAccountsClick = { onUIEvent(ProfileViewModel.UIEvent.OnMyAccountsClick) },
//                    onMyCardsClick = { onUIEvent(ProfileViewModel.UIEvent.OnMyCardsClick) },
//                    onSettingsClick = { onUIEvent(ProfileViewModel.UIEvent.OnSettingsClick) },
//                    onHelpClick = { onUIEvent(ProfileViewModel.UIEvent.OnHelpClick) },
//                    onLogoutClick = { onUIEvent(ProfileViewModel.UIEvent.OnLogoutClick) }
//                )
//            }
        }
    }
}
