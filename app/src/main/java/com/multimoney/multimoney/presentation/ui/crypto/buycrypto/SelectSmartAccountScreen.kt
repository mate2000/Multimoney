package com.multimoney.multimoney.presentation.ui.crypto.buycrypto

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
fun SelectSmartAccountScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SelectSmartAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
    }

  //  BackHandler { viewModel.onUIEvent(ProfileViewModel.UIEvent.OnNavigateBack) }
//    SelectSmartAccountContent(viewModel)
}
@Preview
@Composable
fun SelectSmartAccountContent() {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        TopNavBar(
            //onLeftButtonClick = { viewModel.onUIEvent(ProfileViewModel.UIEvent.OnNavigateBack) },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier.padding(top = 8.dp,bottom = 24.dp),
            text = stringResource(id = R.string.crypto_select_smart_account_title_template),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        CustomInfoButton(
            modifier = Modifier.fillMaxWidth(),
            startIcon = R.drawable.ic_multimoney_green_logo,
            title = "Multimoney Smart | $",
            subtitle = "CR****5506 | $1,500,000.00"
        )

    }
}