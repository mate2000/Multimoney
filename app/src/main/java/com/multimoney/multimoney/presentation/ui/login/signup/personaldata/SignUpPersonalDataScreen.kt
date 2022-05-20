package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.util.Nationalities

@Composable
@Preview
fun SignUpPersonalDataScreen(
    viewModel: SignUpPersonalDataViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        sharedViewModel.apply {
            isContinueEnabled = viewModel.validateFields()
            nextAction = {
                userData?.currentStep = SignUpStep.Two.name
                callMutationUpdateUserRegisterUseCase()
            }
        }
    }
    Column(
        Modifier
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp),
            text = stringResource(id = R.string.sign_up_personal_data_nationality_header),
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
            items = stringArrayResource(id = R.array.sign_up_personal_data_nationalities).sorted(),
            onValueChange = {
                viewModel.apply {
                    sharedViewModel.userData?.nationality = getNationality(it)
                    nationalityValue = it
                    validateFields()
                }
            },
            labelText = stringResource(id = R.string.sign_up_personal_data_nationality),
            value = viewModel.nationalityValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_nationality_placeholder)
        )
        when (viewModel.nationalityValue) {
            Nationalities.CostaRicaId.country -> SignUpPersonalDataCrScreen()
            Nationalities.ElSalvador.country -> SignUpPersonalDataSvScreen()
            Nationalities.Guatemala.country -> SignUpPersonalDataGtScreen()
        }
    }
}