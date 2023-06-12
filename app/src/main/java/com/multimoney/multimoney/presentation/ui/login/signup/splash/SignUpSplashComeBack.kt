package com.multimoney.multimoney.presentation.ui.login.signup.splash

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
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBackViewModel.UIEvent.OnOpenStep
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun SignUpSplashComeBack(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpSplashComeBackViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(TIME_TO_WAIT_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnOpenStep)
                flowOf(status)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .clickable {
                viewModel.onUIEvent(OnOpenStep)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_smile,
            modifier = Modifier
                .wrapContentSize()
                .size(80.dp, 80.dp),
        )
        Text(
            text = stringResource(id = R.string.sign_up_splash_come_back),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText,
            textAlign = TextAlign.Center,
        )
    }

    BackHandler {
        viewModel.onUIEvent(OnOpenStep)
    }

    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}

const val TIME_TO_WAIT_IN_MILLI_SECOND = 3000L
const val DEFAULT_STEP = "0"
