package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
@Preview
fun SignUpPhoneScreen(
    viewModel: SignUpPhoneViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current
    LaunchedEffect(true) {
        sharedViewModel.isContinueEnabled = viewModel.isFormValid()
    }

    Column(modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)) {
        Text(
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
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
        CustomOutlinedTextField(
            value = viewModel.userEmail,
            onValueChange = {
                viewModel.apply {
                    userEmail = it
                    sharedViewModel.isContinueEnabled = isFormValid()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.label_email),
            leadingIcon = R.drawable.ic_envelope,
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_email_required),
            isError = viewModel.userEmailError.first,
            errorMessage = stringResource(id = viewModel.userEmailError.second)
        )
        Text(
            text = stringResource(id = R.string.sign_up_phone_contact_by),
            style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.textSubhead),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 12.dp)
        )
        CustomCheckBox(
            checked = true,
            enabled = false,
            onCheckedChange = { },
            text = stringResource(id = R.string.sign_up_phone_contact_by_email),
            isTextStart = true,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 6.dp, end = 9.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        Divider(color = MultimoneyTheme.colors.divider, thickness = 2.dp)

        CustomCheckBox(
            checked = viewModel.whatsapp,
            onCheckedChange = { viewModel.whatsapp = it },
            text = stringResource(id = R.string.sign_up_phone_contact_by_whatsapp),
            isTextStart = true,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 6.dp, end = 9.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        Divider(color = MultimoneyTheme.colors.divider, thickness = 2.dp)

        CustomCheckBox(
            checked = viewModel.call,
            onCheckedChange = { viewModel.call = it },
            text = stringResource(id = R.string.sign_up_phone_contact_by_call),
            isTextStart = true,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 6.dp, end = 9.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        Divider(color = MultimoneyTheme.colors.divider, thickness = 2.dp)
    }
}