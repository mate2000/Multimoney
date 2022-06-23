package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.PhoneTextField
import com.togitech.ccp.data.utils.getDefaultLangCode
import com.togitech.ccp.data.utils.getDefaultPhoneCode
import com.togitech.ccp.data.utils.getLibCountries

@Composable
@Preview
fun SignUpPhoneScreen(
    viewModel: SignUpPhoneViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current
    val getDefaultCountryCode = getDefaultLangCode()
    val getDefaultPhoneCode = getDefaultPhoneCode()
    var defaultCountryCode by rememberSaveable { mutableStateOf(getDefaultCountryCode) }

    LaunchedEffect(true) {
        viewModel.apply {
            sharedViewModel.apply {
                nextAction = {
                    userData?.currentStep = SignUpStep.Three.name
                    userData?.contactMeans = gsonHelper.convertToString(contactMeans)
                    callMutationUpdateUserRegisterUseCase()
                }
                userData?.countryCode = getDefaultPhoneCode
                countryCode = getDefaultCountryCode
                isContinueEnabled = isFormValid(countryCode)
            }
            phoneCode = getDefaultPhoneCode
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
            value = viewModel.phoneNumber,
            onValueChange = {
                viewModel.apply {
                    phoneNumber = it
                    clearPhoneError()
                }
                sharedViewModel.apply {
                    userData?.phoneNumber = it
                    isContinueEnabled = viewModel.isFormValid(countryCode)
                }
            },
            onDebounceValidation = {
                viewModel.isPhoneValid(
                    sharedViewModel.countryCode
                )
            },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_phone_label_phone),
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_phone_required),
            isError = viewModel.phoneNumberError.first,
            errorMessage = stringResource(id = viewModel.phoneNumberError.second),
            defaultCountry = getLibCountries().single { it.countryCode == defaultCountryCode },
            pickedCountry = {
                defaultCountryCode = it.countryCode
                sharedViewModel.apply {
                    userData?.countryCode = it.countryPhoneCode
                    countryCode = it.countryCode
                    sharedViewModel.userData?.phoneNumber = null
                }
                viewModel.apply {
                    phoneCode = it.countryPhoneCode
                    clearPhoneError()
                    phoneNumber = ""
                }
                sharedViewModel.apply {
                    isContinueEnabled = viewModel.isFormValid(countryCode)
                }
            }
        )
    }
}