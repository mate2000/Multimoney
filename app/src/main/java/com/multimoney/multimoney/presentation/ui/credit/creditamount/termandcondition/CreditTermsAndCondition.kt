package com.multimoney.multimoney.presentation.ui.credit.creditamount.termandcondition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.util.MmWebViewHtml

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreditTermAndCondition(
    onAcceptTermsAndCondition: () -> Unit,
    isActive: MutableState<Boolean>,
    viewModel: CreditTermsAndConditionViewModel = hiltViewModel()
) {
    val isSystemInDrkTheme = isSystemInDarkTheme()
    LaunchedEffect(key1 = true) {
        viewModel.fetchTermsAndConditions(isSystemInDrkTheme)
    }

    Dialog(
        onDismissRequest = {
            isActive.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BackCloseNavBar(isBackVisible = false, onCloseClick = {
                isActive.value = false
            })
            if (viewModel.uiState.html.isNotEmpty()){
                Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    MmWebViewHtml(
                        viewModel.uiState.html,
                        LocalContext.current,
                    )
                }
            }
            CustomButton(
                modifier = Modifier
                    .padding(bottom = 40.dp, top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                text = stringResource(id = R.string.accept),
                buttonType = PrimaryPrimary,
                onClick = {
                    isActive.value = false
                    onAcceptTermsAndCondition()
                }
            )
        }
    }

    BackHandler {
        isActive.value = false
    }
}