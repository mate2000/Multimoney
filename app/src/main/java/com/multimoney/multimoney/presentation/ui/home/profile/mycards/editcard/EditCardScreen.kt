package com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun EditCardScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: EditCardViewModel = hiltViewModel()
) {
    // Properties

    val focusManager = LocalFocusManager.current

    // Navigation

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }

    //View

    Column {

    }


}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = onBackClick
    )
}

@Composable
private fun Title(title: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = Typography.h6.toSpanStyle()
                    .copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
            ) {
                append(stringResource(id = title))
            }
        },
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}