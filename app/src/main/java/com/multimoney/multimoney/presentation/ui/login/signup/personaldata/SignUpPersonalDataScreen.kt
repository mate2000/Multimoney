package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Nationalities
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.SignUpStep.Three
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNationalityValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.BaseEvent.OnGetCountriesSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnCallQueryGetCountry
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNationalityChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
@Preview
fun SignUpPersonalDataScreen(
    viewModel: SignUpPersonalDataViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel(),
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryGetCountry("", onLoadingValueChange = { isLoading ->
            sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(isLoading))
        }))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(OnContinueEnable(event.isFormValid))
                is OnGetCountriesSuccess -> {
                    sharedViewModel.apply {
                        viewModel.onUIEvent(
                            SignUpPersonalDataViewModel.UIEvent.OnStart(
                                nationality = userData?.nationality ?: "",
                                identificationType = userData?.strIdIdentification ?: strIdIdentification,
                                identificationValue = userData?.identification ?: "",
                                firstName = userData?.firstName ?: "",
                                secondName = userData?.secondName ?: "",
                                firstLastName = userData?.firstLastName ?: "",
                                secondLastName = userData?.secondLastName ?: "",
                                fullName = userData?.fullName ?: "",
                                updateNationality = { nationality, idBrand ->
                                    sharedViewModel.onUIEvent(
                                        OnNationalityValueChange(nationality, idBrand)
                                    )
                                },
                                onLoadingValueChange = { isLoading ->
                                    sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(isLoading))
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        sharedViewModel.apply {
            onUIEvent(
                SignUpViewModel.UIEvent.OnSetNavigation(
                    nextAction = {
                        viewModel.onUIEvent(
                            OnNextActionClick(
                                email = userData?.email ?: "",
                                nextStep = Three.name,
                                idBrand = idBrand ?: 0
                            )
                        )
                    },
                    nextStep = Three.id,
                    previousStep = SignUpStep.One.id
                )
            )
        }
        viewModel.onUserDataValidationEvent.collect { result ->
            result.onSuccess { userData ->
                viewModel.onUIEvent(
                    SignUpPersonalDataViewModel.UIEvent.OnUserDataValidationSuccess(
                        currentStep = sharedViewModel.uiState.currentStep,
                        userData = userData,
                        onUseDataValueChange = {
                            sharedViewModel.strIdIdentification = viewModel.uiState.identificationValueType
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnUseDataValueChange(
                                    sharedViewModel.userData?.copy(
                                        pkUser = userData?.pkUser,
                                        fullName = viewModel.getFullName(),
                                        firstName = userData?.firstName,
                                        secondName = userData?.secondName,
                                        firstLastName = userData?.firstLastName,
                                        secondLastName = userData?.secondLastName,
                                        identification = userData?.identification,
                                        currentStep = userData?.currentStep
                                    )
                                )
                            )
                        },
                        onCallMutationUpdateUserRegisterUseCase = {
                            sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase)
                        },
                        onMoveToStep = { step ->
                            viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.SingUpTwo)
                            sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnMoveToStep(step))
                        },
                        onLoadingValueChange = {
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnLoadingValueChange(
                                    false
                                )
                            )
                        }
                    )
                )
            }.onLoading {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(true))
            }.onMessage {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnOpenDialogValueChange(
                        DialogParameters(
                            description = it?.message ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }.onFailure {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            title = string.error_empty,
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
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
            items = viewModel.uiState.countryList,
            onValueChange = { value ->
                viewModel.onUIEvent(
                    OnNationalityChange(
                        viewModel.uiState.countryList.indexOf(value),
                        updateNationality = { nationality, idBrand ->
                            sharedViewModel.onUIEvent(
                                OnNationalityValueChange(nationality, idBrand)
                            )
                        },
                        onLoadingValueChange = { isLoading ->
                            sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(isLoading))
                        }
                    )
                )
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