package com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.ownbusiness

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.ownbusiness.OwnBusinessViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
@Preview
fun SmartOwnBusinessSvScreen(
    viewModel: OwnBusinessViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel(), // TODO, pass the correct sharedViewModel
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueVisible(true))
    }
    SmartOwnBusinessSvContent(viewModel.uiState)
}

@Composable
fun SmartOwnBusinessSvContent(uiState: UIState) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.smart_own_business_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomOutlinedTextField(
            value = uiState.companyNameNameValue,
            placeHolder = "a placeholder",
            onValueChange = { document ->
                // TODO, handle this
//                viewModel.onUIEvent(
//                    OnIdentificationValueChange(
//                        identification = document,
//                        identificationShareViewModelChange = {
//                            sharedViewModel.onUIEvent(
//                                OnSharedIdentificationValueChange(document)
//                            )
//                        }
//                    )
//                )
            },
            onDebounceValidation = {
                // TODO, handle this
                //viewModel.onUIEvent(SignUpPersonalDataViewModel.UIEvent.OnValidateDocument())
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.smart_own_business_company_name_label),
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = "nombre de la empresa requerido",
            // isError = viewModel.uiState.personalIdError.first, // FIXME
            //  errorMessage = stringResource(id = viewModel.uiState.personalIdError.second), // FIXME
        )
    }
}