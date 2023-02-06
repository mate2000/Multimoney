package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnAddFavoriteValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnContinueButtonClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnEmailNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnFavoriteNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanViewModel.UIEvent.OnAccountValueCompleted
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import com.multimoney.multimoney.presentation.util.transformation.VisualTransformationMasks

@Composable
fun SmartTransferRegisterIbanScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SmartTransferRegisterIbanViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
        }
    }

    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                modifier = Modifier.offset(x = (-16).dp),
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isRightButtonVisible = false
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.smart_iban_register_title),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.text
                )
            )

            CustomOutlinedTextField(
                modifier = Modifier.padding(top = 32.dp),
                value = viewModel.uiState.ibanAccountNumber,
                labelText = stringResource(id = R.string.smart_iban_register_account_label),
                leadingIconComposable = { tint ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account_info),
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
                isRequired = true,
                placeHolder = stringResource(id = R.string.iban_account_hint),
                onValueChange = { viewModel.onUIEvent(OnAccountValueChange(it)) },
                canShowNonErrorMessage = true,
                onDebounceValidation = { viewModel.onUIEvent(OnAccountValueCompleted) },
                showInfo = viewModel.uiState.accountInformation.first,
                infoMessage = stringResource(id = viewModel.uiState.accountInformation.second),
                isError = viewModel.uiState.accountError.first || viewModel.uiState.accountValidationError?.first == true,
                errorMessage = viewModel.uiState.accountValidationError?.second
                    ?: stringResource(id = viewModel.uiState.accountError.second),
                customTransformation = MaskVisualTransformation(
                    VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.mask,
                    VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.maskChar
                )
            )

            if (viewModel.uiState.accountValidationError?.first == false && viewModel.uiState.validationFinish) {
                CustomInformativeText(
                    modifier = Modifier.padding(top = 24.dp),
                    leadingIcon = R.drawable.ic_check,
                    text = stringResource(id = R.string.smart_iban_register_full_name_label),
                    textStyle = Typography.caption.copy(color = MultimoneyTheme.colors.textSuccess)
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 28.dp),
                    text = viewModel.uiState.proprietary,
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.titleText
                )
                CustomInformativeText(
                    leadingIcon = R.drawable.ic_check,
                    text = stringResource(id = R.string.smart_iban_register_identification_label),
                    textStyle = Typography.caption.copy(color = MultimoneyTheme.colors.textSuccess)
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = viewModel.uiState.documentNumber,
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.titleText
                )
            }

            CustomOutlinedTextField(
                modifier = Modifier.padding(top = 24.dp),
                value = viewModel.uiState.email,
                labelText = stringResource(id = R.string.smart_iban_register_mail_label),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (viewModel.uiState.addFavorite) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                    onDone = {
                        focusManager.clearFocus()
                    }),
                isRequiredMessage = stringResource(id = R.string.credit_monthly_income_required_income),
                onValueChange = { email ->
                    viewModel.onUIEvent(OnEmailNameValueChange(email))
                },
                isError = viewModel.uiState.userEmailError.first,
                errorMessage = stringResource(id = viewModel.uiState.userEmailError.second),
                isRequired = true,
                onDebounceValidation = { viewModel.onUIEvent(OnValidateUserEmail) },
            )

            CustomCheckBox(
                modifier = Modifier.padding(top = 27.dp),
                checked = viewModel.uiState.addFavorite,
                onCheckedChange = {
                    viewModel.onUIEvent(OnAddFavoriteValueChange(it))
                },
                text = stringResource(id = R.string.smart_iban_register_favorite_checkbox)
            )

            if (viewModel.uiState.addFavorite) {
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 27.dp),
                    value = viewModel.uiState.favoriteName,
                    labelText = stringResource(id = R.string.smart_iban_register_favorite_label),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    onValueChange = { favoriteName ->
                        viewModel.onUIEvent(OnFavoriteNameValueChange(favoriteName))
                    },
                    errorMessage = stringResource(id = R.string.smart_iban_register_account_error),
                )
            }
        }

        Column {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                onClick = {
                    viewModel.onUIEvent(OnContinueButtonClick)
                },
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.button_continue),
                enable = viewModel.uiState.isFormValid
            )
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.closeKeyboard) {
        focusManager.clearFocus()
        viewModel.closeKeyboard = false
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}