package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit

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
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit.EditSavedIbanAccountViewModel.UIEvent.OnGetAccountInformation
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit.EditSavedIbanAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit.EditSavedIbanAccountViewModel.UIEvent.OnNicknameChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit.EditSavedIbanAccountViewModel.UIEvent.OnSaveButtonClick
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import com.multimoney.multimoney.presentation.util.transformation.VisualTransformationMasks

@Composable
fun EditSavedIbanAccountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: EditSavedIbanAccountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            viewModel.onUIEvent(OnGetAccountInformation)
        }
    }

    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(R.string.smart_iban_transfer_edit_title),
                    style = Typography.h5.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.text
                    )
                )
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 32.dp),
                    labelText = stringResource(id = R.string.smart_iban_register_account_label),
                    enabled = false,
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
                    keyboardActions = KeyboardActions(),
                    value = viewModel.uiState.accountNumber,
                    customTransformation = MaskVisualTransformation(
                        VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.mask,
                        VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.maskChar
                    )
                )
                if (viewModel.uiState.validationFinish) {
                    CustomInformativeText(
                        modifier = Modifier.padding(top = 24.dp),
                        leadingIcon = R.drawable.ic_check,
                        text = stringResource(id = R.string.smart_iban_register_full_name_label),
                        textStyle = Typography.caption.copy(color = MultimoneyTheme.colors.textSuccess)
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 28.dp),
                        text = viewModel.uiState.titularName,
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
                    modifier = Modifier.padding(top = 27.dp),
                    value = viewModel.uiState.nickname,
                    labelText = stringResource(id = R.string.smart_iban_register_favorite_label),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    onValueChange = { favoriteName ->
                        viewModel.onUIEvent(OnNicknameChange(favoriteName))
                    },
                    isRequired = false
                )
            }
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(48.dp),
                onClick = {
                    viewModel.onUIEvent(OnSaveButtonClick)
                },
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.save),
                enable = viewModel.uiState.isButtonEnabled
            )
        }
    }
}