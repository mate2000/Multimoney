package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.validateidentity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.Companion.TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnChangeScreen
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun ValidateIdentityScreen(viewModel: SignDocumentProcessViewModel) {
    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND)
            .onEach { status ->
                viewModel.onUIEvent(OnChangeScreen(SIGN_DOCUMENTS_STEP.value))
                flowOf(status)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_logo_multimoney
            )
            Text(
                modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp),
                text = stringResource(id = R.string.validate_identity_title),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(all = 24.dp),
                text = stringResource(id = R.string.validate_identity_subtitle),
                style = Typography.body1,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
    }

    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}
