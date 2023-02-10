package com.multimoney.multimoney.presentation.ui.login.signup.completed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnNavigateToSignIn
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun SignUpCompleted(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpCompletedViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    val navigateToSignInDebounce = remember { MutableStateFlow(true) }
    val navigateToSignFlow: Flow<Boolean> = remember {
        navigateToSignInDebounce.debounce(TIME_TO_WAIT_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnNavigateToSignIn)
                flowOf(status)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .clickable {
                viewModel.onUIEvent(OnNavigateToSignIn)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(
            drawableResource = drawable.ic_logo_multimoney2,
            modifier = Modifier
                .wrapContentSize()
                .size(200.dp, 72.dp)
        )
        Text(
            text = stringResource(id = R.string.sign_up_complete_title),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText,
            textAlign = TextAlign.Center
        )
    }

    BackHandler {
        viewModel.onUIEvent(OnNavigateToSignIn)
    }

    // this is required to execute the debounce
    val navigateToSignInFlowValue by navigateToSignFlow.collectAsState(false)
    viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.SignUpSuccess)
}

const val TIME_TO_WAIT_IN_MILLI_SECOND = 10000L