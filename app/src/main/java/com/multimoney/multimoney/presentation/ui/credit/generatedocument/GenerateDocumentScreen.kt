package com.multimoney.multimoney.presentation.ui.credit.generatedocument

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
fun GenerateDocumentScreen(
    sharedViewModel: CreditViewModel,
    viewModel: GenerateDocumentViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CustomImage(drawableResource = R.drawable.ic_frame)
            Text(
                text = stringResource(id = R.string.generate_document_title),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 28.sp
                ),
                color = MultimoneyTheme.colors.text
            )
            Text(
                text = stringResource(id = R.string.generate_document_subtitle),
                modifier = Modifier.padding(top = 8.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.text
            )
        }
        Row {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp), color =
                MultimoneyTheme.colors.primary
            )
            Text(
                text = stringResource(id = R.string.generate_document_info),
                modifier = Modifier.padding(start = 12.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
        }
    }
}