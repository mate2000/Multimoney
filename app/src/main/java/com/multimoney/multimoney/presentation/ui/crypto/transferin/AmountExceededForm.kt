package com.multimoney.multimoney.presentation.ui.crypto.transferin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun AmountExceededFormScreen(
    viewModel: AmountExceededViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        backgroundColor = MultimoneyTheme.colors.background,
        topBar = {TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = {
            //viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
        },
    )}) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.amount_exceeded_title),
                    style = Typography.h6.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.text
                    )
                )
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        tint = MultimoneyTheme.colors.textInformation,
                        imageVector = ImageVector.vectorResource(id = R.drawable.info_blue_icon),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.amount_exceeded_message),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text
                        )
                    )
                }
                CustomOutlinedTextField(
                    value = viewModel.uiState.name,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AmountExceededViewModel.UIEvent.OnNameChange(
                                it
                            )
                        )
                    },
                    labelText = stringResource(R.string.amount_exceeded_name),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    modifier = Modifier.padding(top = 24.dp)
                )
                CustomOutlinedTextField(
                    value = viewModel.uiState.platformName,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AmountExceededViewModel.UIEvent.OnPlatformNameChange(
                                it
                            )
                        )
                    },
                    labelText = stringResource(R.string.amount_exceeded_platform),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    modifier = Modifier.padding(top = 24.dp)
                )
                CustomOutlinedTextField(
                    value = viewModel.uiState.reason,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AmountExceededViewModel.UIEvent.OnReasonChange(
                                it
                            )
                        )
                    },
                    labelText = stringResource(R.string.amount_exceeded_reason),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    modifier = Modifier.padding(top = 24.dp)
                )

            }
            CustomButton(
                text = stringResource(id = R.string.amount_exceeded_button),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    viewModel.onUIEvent(AmountExceededViewModel.UIEvent.OnReleaseDeposit)
                },
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isFormValid
            )
        }
    }
}