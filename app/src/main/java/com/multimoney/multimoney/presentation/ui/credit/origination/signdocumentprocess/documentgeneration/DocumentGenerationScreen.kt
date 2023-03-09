package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.documentgeneration

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.Companion.TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND
import com.multimoney.multimoney.presentation.uielement.CustomImage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
@Composable
fun DocumentGenerationScreen(
    idBrand: Int,
    onGetLinkAgain: () -> Unit = {}
) {
    val openStepDebounce = remember { MutableStateFlow(true) }
    val openStepFlow: Flow<Boolean> = remember {
        openStepDebounce.debounce(TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND).onEach { status ->
            onGetLinkAgain()
            flowOf(status)
        }
    }

    DocumentGenerationContent(subtitleResource = if (idBrand == Brand.CostaRica.id) string.document_generation_subtitle_cr else string.document_generation_subtitle)

    // this is required to execute the debounce
    val openStepFlowValue by openStepFlow.collectAsState(false)
}

@Preview
@Composable
fun DocumentGenerationContent(subtitleResource: Int = string.empty) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(0.80f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(modifier = Modifier.size(48.dp, 48.dp), drawableResource = drawable.ic_logo_multimoney)
            Text(
                text = stringResource(id = string.document_generation_title),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MultimoneyTheme.colors.titleText,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = subtitleResource),
                modifier = Modifier.padding(top = 8.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.subTitleText,
                textAlign = Companion.Center
            )
        }
        Row(
            modifier = Modifier.padding(bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MultimoneyTheme.colors.primary
            )
            Text(
                text = stringResource(id = string.document_generation_info),
                modifier = Modifier.padding(start = 12.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
        }
    }
}
