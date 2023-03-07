package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.Companion.TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.loadingProgressIndicator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun SmartDocumentGenerationScreen(
    viewModel: SmartSignViewModel,
    icon: Int? = null,
    title: Int? = null,
    subtitle: Int? = null
) {
    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnNavigateToHome)
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
            CustomImage(drawableResource = icon ?: drawable.ic_logo_multimoney3)
            Text(
                text = stringResource(id = title ?: string.document_generation_title),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                ),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(
                    id = subtitle ?: string.smart_other_generating_document_subtitle
                ),
                modifier = Modifier.padding(top = 10.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
        }

        //LoadingIndicator(viewModel.uiState.isLoading)
        loadingProgressIndicator(
            viewModel.uiState.isLoading,
            stringResource(id = R.string.smart_loading_label)
        )

    }


    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}