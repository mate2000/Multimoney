package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.processingtransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.CustomImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@Composable
fun ProcessingTransactionScreen(viewModel: SignDocumentProcessViewModel) {
    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(SignDocumentProcessViewModel.TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnNavigateToHome)
                flowOf(status)
            }
    }
    ProcessingTransactionContent()
    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}

@Preview
@Composable
fun ProcessingTransactionContent() {
    Column(
        modifier = Modifier.fillMaxSize().background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(modifier = Modifier.size(48.dp, 48.dp), drawableResource = R.drawable.ic_logo_multimoney)
        Text(
            text = stringResource(id = R.string.origination_processing_transaction),
            modifier = Modifier.wrapContentWidth().padding(top = 24.dp),
            style = Typography.body1,
            color = MultimoneyTheme.colors.titleText
        )
    }
}
