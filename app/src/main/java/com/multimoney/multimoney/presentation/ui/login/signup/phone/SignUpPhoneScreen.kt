package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.PhoneTextField
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import com.togitech.ccp.data.utils.getDefaultLangCode
import com.togitech.ccp.data.utils.getDefaultPhoneCode
import com.togitech.ccp.data.utils.getLibCountries

@Composable
@Preview
fun SignUpPhoneScreen(
    viewModel: SignUpPhoneViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel(),
) {

    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val getDefaultCountryCode = getDefaultLangCode()
    val getDefaultPhoneCode = getDefaultPhoneCode()
    val selectedCountry =
        getLibCountries().first {
            it.countryPhoneCode == if (sharedViewModel.userData?.countryCode.isNullOrEmpty()) {
                getDefaultPhoneCode
            } else {
                sharedViewModel.userData?.countryCode
            }
        }

    LaunchedEffect(true) {
        sharedViewModel.apply {
            onUIEvent(
                SignUpViewModel.UIEvent.OnSetNavigation(
                    nextAction = {
                        viewModel.onUIEvent(SignUpPhoneViewModel.UIEvent.OnNextActionClick({
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
                            { onUIEvent(SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase) }))
                        FireBaseEvents.SignUpThree.logEvent(context)
                        viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.SignUpThree)
                    },
                    nextStep = viewModel.getNextStep(sharedViewModel.isPhoneVerified,
                        isOnFidoVerified).id,
                    previousStep = SignUpStep.Two.id
                )
            )
            viewModel.baseEvent.collect { event ->
                when (event) {
                    is SignUpPhoneViewModel.BaseEvent.OnFormValidateCompleted -> onUIEvent(
                        SignUpViewModel.UIEvent.OnContinueEnable(
                            event.isFormValid
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(true) {
        var countryCodeValue = ""
        sharedViewModel.userData?.countryCode?.let {
            countryCodeValue = selectedCountry.countryCode
        } ?: run {
            countryCodeValue = getDefaultCountryCode
        }

        viewModel.onUIEvent(
            SignUpPhoneViewModel.UIEvent.OnStart(
                phoneCode = selectedCountry.countryPhoneCode.ifBlank { getDefaultPhoneCode },
                countryCode = countryCodeValue,
                phoneNumber = sharedViewModel.userData?.phoneNumber ?: "",
                signUpStartData = {
                    sharedViewModel.onUIEvent(
                        SignUpViewModel.UIEvent.OnCountryCountryCodeValueChange(
                            countryCodeValue,
                            selectedCountry.countryPhoneCode,
                            false
                        )
                    )
                }
            )
        )
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

        Row(modifier = Modifier.padding(top = 12.dp)) {
            CustomImage(
                drawableResource = R.drawable.ic_information,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.sign_up_phone_information),
                style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.textInformation),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 9.dp)
            )
        }

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
                        })
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
            errorMessage = stringResource(id = viewModel.uiState.phoneNumberError.second),
            defaultCountry = selectedCountry,
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
                        })
                )
            }
        )
    }
}