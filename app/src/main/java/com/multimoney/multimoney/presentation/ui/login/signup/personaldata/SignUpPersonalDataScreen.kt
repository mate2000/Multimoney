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
import com.multimoney.data.util.catalog.SignUpStep.Three
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNationalityValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUseDataValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNationalityChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnStart
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
            onUIEvent(
                SignUpViewModel.UIEvent.OnSetNavigation(
                    nextAction = {
                        viewModel.onUIEvent(
                            OnNextActionClick(
                                onUserDataValueChange = {
                                    onUIEvent(
                                        OnUseDataValueChange(
                                            userData = userData?.copy(
                                                currentStep = Three.name,
                                                fullName = viewModel.getFullName()
                                            )
                                        )
                                    )
                                },
                                onCallMutationUpdateUserRegisterUseCase = {
                                    onUIEvent(OnCallMutationUpdateUserRegisterUseCase)
                                })
                        )
                    },
                    nextStep = Three.id,
                    previousStep = SignUpStep.One.id
                )
            )

            viewModel.onUIEvent(
                OnStart(
                    nationality = userData?.nationality ?: "",
                    identificationType = userData?.identificationValueType ?: "",
                    identificationValue = userData?.identification ?: "",
                    firstName = userData?.firstName ?: "",
                    secondName = userData?.secondName ?: "",
                    firstLastName = userData?.firstLastName ?: "",
                    secondLastName = userData?.secondLastName ?: "",
                    fullName = userData?.fullName ?: ""
                )
            )
            viewModel.baseEvent.collect { event ->
                when (event) {
                    is OnFormValidateCompleted -> onUIEvent(OnContinueEnable(event.isFormValid))
                }
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
            onValueChange = { value ->
                viewModel.onUIEvent(OnNationalityChange(value) {
                    sharedViewModel.onUIEvent(
                        OnNationalityValueChange(it)
                    )
                })
            },
            labelText = stringResource(id = R.string.sign_up_personal_data_nationality),
            value = viewModel.uiState.nationalityValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_nationality_placeholder)
        )
        when (viewModel.uiState.nationalityValue) {
            Nationalities.CostaRicaId.country -> SignUpPersonalDataCrScreen()
            Nationalities.ElSalvador.country -> SignUpPersonalDataSvScreen()
            Nationalities.Guatemala.country -> SignUpPersonalDataGtScreen()
        }
    }
}