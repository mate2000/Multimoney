package com.multimoney.multimoney.presentation.ui.personal

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignUpPersonalDataScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    SignUpNationalPersonaData(viewModel)
}

@Composable
fun SignUpNationalPersonaData(viewModel: SignUpPersonalDataViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val countries = stringArrayResource(id = R.array.sign_up_nationalities).sorted()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp),
            text = stringResource(id = R.string.sign_up_nationality_header),
            style = Typography.h6.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = countries,
            onValueChange = {
                viewModel.nationalityValue = it
            },
            labelText = stringResource(id = R.string.sign_up_nationality),
            value = viewModel.nationalityValue,
            placeHolder = stringResource(id = R.string.sign_up_nationality_placeholder)
        )
        when (viewModel.nationalityValue) {
            countries[0] -> SignUpPersonalDataCrScreen()
            countries[1] -> SignUpPersonalDataSvScreen()
            countries[2] -> SignUpPersonalDataGtScreen()
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    SignUpNationalPersonaData(SignUpPersonalDataViewModel())
}