package com.multimoney.multimoney.presentation.ui.smart.transfer.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.Companion.FORMAT_VALUE
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnAddFavoriteValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnContinueButtonClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnEmailNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnFavoriteNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnIdentificationTypeChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnQueryDocumentList
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnValidateDocument
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdownTextField
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
            onUIEvent(OnQueryDocumentList)
            executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
        }
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
                onLeftButtonClick = { viewModel.onUIEvent(SmartTransferRegisterIbanViewModel.UIEvent.OnNavigateBack) },
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
                isRequired = false,
                placeHolder = stringResource(id = R.string.iban_account_hint),
                onValueChange = { viewModel.onUIEvent(OnAccountValueChange(it)) },
                canShowNonErrorMessage = true,
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

            CustomDropdownTextField(
                modifier = Modifier.padding(top = 15.dp),
                labelText = stringResource(id = R.string.smart_iban_register_document_label),
                value = viewModel.uiState.documentNumber,
                placeHolder = if (viewModel.uiState.documentFormat != "") viewModel.uiState.documentFormat.replace(
                    viewModel.uiState.documentFormat.last(),
                    FORMAT_VALUE,
                    false
                ) else "",
                isError = viewModel.uiState.personalIdError.first,
                errorMessage = stringResource(id = viewModel.uiState.personalIdError.second),
                onValueChange = { documentNumber ->
                    viewModel.onUIEvent(OnIdentificationValueChange(documentNumber))
                },
                onSelectionChange = { documentType ->
                    viewModel.onUIEvent(OnIdentificationTypeChange(documentType))
                },
                optionList = viewModel.uiState.documentList,
                optionSelected = viewModel.uiState.identificationValueType,
                customTransformation = if (viewModel.uiState.documentFormat != "") MaskVisualTransformation(
                    viewModel.uiState.documentFormat,
                    viewModel.uiState.documentFormat.last()
                ) else null,
                onDebounceValidation = {
                    viewModel.onUIEvent(OnValidateDocument(it))
                }
            )

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
                    .padding(16.dp)
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