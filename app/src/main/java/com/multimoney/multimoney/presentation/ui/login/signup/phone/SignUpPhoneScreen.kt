package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnShowCloseIcon
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnSetupSharedEvents
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.PhoneTextField
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import com.togitech.ccp.data.utils.getLibCountries

@Composable
@Preview
fun SignUpPhoneScreen(
    viewModel: SignUpPhoneViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnSetupSharedEvents(
                onLoadingValueChange = {
                    sharedViewModel.onUIEvent(OnLoadingValueChange(it))
                },
                onFailureWithDialog = { isLoading, dialogParameters ->
                    sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
                }
            )
        )
        sharedViewModel.onUIEvent(OnShowCloseIcon(true))
        sharedViewModel.apply {
            onUIEvent(
                SignUpViewModel.UIEvent.OnSetNavigation(
                    nextAction = {
                        viewModel.onUIEvent(
                            SignUpPhoneViewModel.UIEvent.OnNextActionClick(
                                {
                                    onUIEvent(
                                        SignUpViewModel.UIEvent.OnUseDataValueChange(
                                            userData?.copy(
                                                currentStep = viewModel.getNextStep(
                                                    sharedViewModel.isPhoneVerified,
                                                    isOnFidoVerified
                                                ).name
                                            )
                                        )
                                    )
                                },
                                { onUIEvent(SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase) }
                            )
                        )
                        sharedViewModel.logEvents(
                            FireBaseEvents.SignUpThree,
                            AdjustEventType.SIGNUP_3_2003
                        )
                    },
                    nextStep = viewModel.getNextStep(
                        sharedViewModel.isPhoneVerified,
                        isOnFidoVerified
                    ).id,
                    previousStep = SignUpStep.Two.id
                )
            )
            viewModel.baseEvent.collect { event ->
                when (event) {
                    is SignUpPhoneViewModel.BaseEvent.OnFormValidateCompleted -> onUIEvent(
                        SignUpViewModel.UIEvent.OnContinueEnable(event.isFormValid)
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(
            SignUpPhoneViewModel.UIEvent.OnSetupDefaultCountry(
                sharedViewModel.idBrand ?: 0,
                sharedViewModel.userData?.identification
            )
        )
        viewModel.uiState.selectedCountry?.let { countryData ->
            viewModel.onUIEvent(
                SignUpPhoneViewModel.UIEvent.OnStart(
                    phoneCode = countryData.countryPhoneCode,
                    countryCode = countryData.countryCode,
                    phoneNumber = sharedViewModel.userData?.phoneNumber ?: "",
                    signUpStartData = {
                        sharedViewModel.onUIEvent(
                            SignUpViewModel.UIEvent.OnCountryCountryCodeValueChange(
                                countryData.countryCode,
                                countryData.countryPhoneCode,
                                false
                            )
                        )
                    },
                    onFailure = {
                        viewModel.onUIEvent(SignUpPhoneViewModel.UIEvent.OnQueryError)
                    }
                )
            )
        }
    }

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            style = Typography.h6.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            ),
            text = stringResource(id = R.string.sign_up_phone_title),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomInformativeText(
            modifier = Modifier.padding(top = 12.dp),
            leadingIcon = R.drawable.ic_information,
            text = stringResource(id = R.string.sign_up_phone_information),
            textStyle = Typography.subtitle2.copy(color = MultimoneyTheme.colors.labelText)
        )

        // Fields
        PhoneTextField(
            value = viewModel.uiState.phoneNumber,
            onValueChange = {
                viewModel.onUIEvent(
                    SignUpPhoneViewModel.UIEvent.OnUserPhoneValueChanged(
                        phoneNumber = it,
                        countryCode = sharedViewModel.countryCode,
                        updateUserInfoPhone = {
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnPhoneNumberValueChange(it)
                            )
                        }
                    )
                )
            },
            onDebounceValidation = {
                viewModel.onUIEvent(SignUpPhoneViewModel.UIEvent.OnValidatePhone(sharedViewModel.countryCode))
            },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_phone_label_phone),
            modifier = Modifier.padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_phone_required),
            isError = viewModel.uiState.phoneNumberError.first,
            errorMessage = viewModel.uiState.phoneNumberError.third.ifEmpty { stringResource(id = viewModel.uiState.phoneNumberError.second) },
            defaultCountry = getLibCountries.find { it.countryCode == viewModel.uiState.currentBrand.countryCode }
                ?: getLibCountries.first(),
            pickedCountry = {
                viewModel.onUIEvent(
                    SignUpPhoneViewModel.UIEvent.OnCountryCodeValueChanged(
                        phoneCode = it.countryPhoneCode,
                        countryCode = it.countryCode,
                        updateUserCountryCode = {
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnCountryCountryCodeValueChange(
                                    it.countryCode,
                                    it.countryPhoneCode,
                                    true
                                )
                            )
                        }
                    )
                )
            },
            countriesList = viewModel.uiState.countriesList
        )
    }
}
