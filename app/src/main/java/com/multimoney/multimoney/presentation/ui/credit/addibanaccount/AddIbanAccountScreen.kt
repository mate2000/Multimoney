package com.multimoney.multimoney.presentation.ui.credit.addibanaccount

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnEditAccount
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import com.multimoney.multimoney.presentation.util.transformation.VisualTransformationMasks

@Composable
fun AddIbanAccountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: AddIbanAccountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(
                onPopBackStack = onPopBackStack,
                onNavigate = onNavigate
            )
            onUIEvent(OnStart)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnBackClick) },
                isRightButtonVisible = false
            )
            Text(
                text = stringResource(id = viewModel.uiState.title),
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
                color = MultimoneyTheme.colors.labelText
            )

            if (viewModel.uiState.ibanSuccess) {
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    startIcon = viewModel.validateAccount?.currency?.getCurrency()?.accountIcon,
                    title = viewModel.validateAccount?.bankName ?: "",
                    subtitle = getMaskedAccountIban(
                        viewModel.uiState.accountNumber,
                        stringResource(id = R.string.payment_account_masked_text)
                    ),
                    endIcon = R.drawable.ic_edit_green,
                    onEndIconClick = {
                        viewModel.onUIEvent(OnEditAccount)
                    }
                )
            } else {
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    value = viewModel.uiState.accountNumber,
                    leadingIconComposable = { tint ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_account),
                                contentDescription = "",
                                tint = tint
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.iban_account_cr),
                                style = Typography.body2.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = tint
                                )
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    isRequired = false,
                    placeHolder = stringResource(id = R.string.iban_account_hint),
                    onValueChange = { viewModel.onUIEvent(OnAccountValueChange(it)) },
                    canShowNonErrorMessage = true,
                    showInfo = viewModel.uiState.accountInformation.first,
                    infoMessage = stringResource(id = viewModel.uiState.accountInformation.second),
                    isError = viewModel.uiState.accountError.first,
                    errorMessage = viewModel.uiState.validationError
                        ?: stringResource(id = viewModel.uiState.accountError.second),
                    customTransformation = MaskVisualTransformation(
                        VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.mask,
                        VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.maskChar
                    )
                )
            }
        }
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            onClick = { viewModel.onUIEvent(OnContinueClick) },
            buttonType = PrimaryPrimary,
            text = stringResource(id = string.button_continue),
            enable = viewModel.uiState.isFormValid
        )
    }
    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource).ifEmpty { viewModel.uiState.dialogParameters.description },
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}
