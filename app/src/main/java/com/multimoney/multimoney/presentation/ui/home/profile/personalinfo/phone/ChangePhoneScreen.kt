package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.PhoneTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.getLibCountries

@Preview
@Composable
fun ChangePhoneScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ChangePhoneViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    BackHandler {
        viewModel.onUIEvent(ChangePhoneViewModel.UIEvent.OnNavigateBack)
    }
    val focusManager = LocalFocusManager.current
    val selectedCountry = getLibCountries().first {
        it.countryCode == viewModel.uiState.countryCode
    }
    viewModel.onUIEvent(ChangePhoneViewModel.UIEvent.OnStart(phoneCode = selectedCountry.countryPhoneCode))
    ChangePhoneScreenContent(viewModel, selectedCountry, focusManager)
}

@Composable
private fun ChangePhoneScreenContent(
    viewModel: ChangePhoneViewModel, selectedCountry: CountryData, focusManager: FocusManager
) {
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, phoneInputColumn, continueButton) = createRefs()

        TopNavBar(
            modifier = Modifier.constrainAs(topNavBar) {
                top.linkTo(parent.top)
            },
            onLeftButtonClick = {
                viewModel.onUIEvent(ChangePhoneViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .constrainAs(phoneInputColumn) {
                    top.linkTo(topNavBar.bottom)
                }
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_change_phone_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )

            PhoneTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                value = viewModel.uiState.newPhoneNumber,
                onValueChange = {
                    viewModel.onUIEvent(
                        ChangePhoneViewModel.UIEvent.OnUserPhoneValueChanged(
                            phoneNumber = it,
                            countryCode = viewModel.uiState.countryCode ?: "",
                        )
                    )
                },
                onDebounceValidation = {
                    viewModel.onUIEvent(ChangePhoneViewModel.UIEvent.OnValidatePhone(viewModel.uiState.countryCode))
                },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                labelText = stringResource(id = R.string.profile_new_phone_number),
                isRequired = true,
                isRequiredMessage = stringResource(id = R.string.sign_up_phone_required),
                isError = viewModel.uiState.phoneNumberError.first,
                errorMessage = stringResource(id = viewModel.uiState.phoneNumberError.second),
                defaultCountry = selectedCountry,
                pickedCountry = {
                    viewModel.onUIEvent(
                        ChangePhoneViewModel.UIEvent.OnCountryCodeValueChanged(
                            phoneCode = it.countryPhoneCode,
                            countryCode = it.countryCode,
                        )
                    )
                }
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.profile_we_will_send_you_a_code),
                style = Typography.body2.copy(fontWeight = FontWeight.Light),
                color = MultimoneyTheme.colors.labelText
            )
        }
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .constrainAs(continueButton) {
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                },
            buttonType = CustomButtonType.PrimaryPrimary,
            text = stringResource(id = R.string.profile_change_phone_title),
            enable = viewModel.uiState.isButtonEnabled,
            onClick = {
                viewModel.onUIEvent(ChangePhoneViewModel.UIEvent.OnContinueButtonClicked)
            }
        )
    }
}