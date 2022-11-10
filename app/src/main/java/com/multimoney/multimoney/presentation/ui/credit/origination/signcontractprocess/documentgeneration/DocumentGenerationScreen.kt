package com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.documentgeneration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign.Companion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.documentgeneration.DocumentGenerationViewModel.UIEvent.OnOpenSignDocument
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@Composable
fun DocumentGenerationScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: DocumentGenerationViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
@Preview
fun DocumentGenerationContent(viewModel: DocumentGenerationViewModel = hiltViewModel()) {
    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(TIME_TO_WAIT_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnOpenSignDocument(DUMMY_URL))
                flowOf(status)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.80f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(drawableResource = drawable.ic_frame)
            Text(
                text = stringResource(id = string.document_generation_title),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                ),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = string.document_generation_subtitle),
                modifier = Modifier.padding(top = 8.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.text,
                textAlign = Companion.Center
            )
        }
        Row(modifier = Modifier.padding(bottom = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color =
                MultimoneyTheme.colors.primary
            )
            Text(
                text = stringResource(id = string.document_generation_info),
                modifier = Modifier.padding(start = 12.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
        }
    }
    // this is require to block the onBack event
    BackHandler(onBack = {})

    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}

const val DUMMY_URL = "www.google.com"
const val TIME_TO_WAIT_IN_MILLI_SECOND = 30000L
