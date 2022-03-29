package com.multimoney.multimoney.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    // Properties
    val focusManager = LocalFocusManager.current

    // View
    ProvideWindowInsets {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsWithImePadding()

        ) {

            // Welcome section
            CustomImage(
                drawableResource = R.drawable.ic_logo_multimoney,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 56.dp)
            )

            Text(
                text = viewModel.userName?.let {
                    buildAnnotatedString {
                        withStyle(
                            style = Typography.h5.toSpanStyle()
                                .copy(fontWeight = FontWeight.SemiBold)
                        ) {
                            append(stringResource(id = R.string.login_welcome_name, it))
                        }
                        withStyle(style = Typography.subtitle1.toSpanStyle()) {
                            append(stringResource(id = R.string.login_welcome_no_name))
                        }
                    }
                } ?: run {
                    buildAnnotatedString {
                        withStyle(
                            style = Typography.h5.toSpanStyle()
                                .copy(fontWeight = FontWeight.SemiBold)
                        ) {
                            append(stringResource(id = R.string.login_welcome))
                        }

                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            )

            // Fields
            CustomOutlinedTextField(
                value = viewModel.userEmail,
                onValueChange = { viewModel.userEmail = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                labelText = stringResource(id = R.string.login_label_email),
                leadingIcon = R.drawable.ic_envelope,
                modifier = Modifier.padding(top = 44.dp)
            )
            CustomOutlinedTextField(
                value = viewModel.userPassword,
                onValueChange = { viewModel.userPassword = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                labelText = stringResource(id = R.string.login_label_password),
                isPassword = true,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.login_forgot_password),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp),
                style = Typography.body2.copy(textDecoration = TextDecoration.Underline),
                color = MultimoneyTheme.colors.link
            )
            CustomCheckBox(
                checked = viewModel.isFingerprintChecked,
                onCheckedChange = { viewModel.isFingerprintChecked = it },
                text = stringResource(id = R.string.login_activate_fingerprint),
                modifier = Modifier.padding(top = 51.dp)
            )
            CustomButton(
                onClick = { viewModel.login() },
                text = stringResource(id = R.string.login),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            Text(
                text = stringResource(id = R.string.login_create_account),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp),
                style = Typography.body2.copy(textDecoration = TextDecoration.Underline),
                color = MultimoneyTheme.colors.link
            )
        }
    }
}
