package com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword.ChangePasswordViewModel.UIEvent.OnNavigateToForgotPassword
import com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword.ChangePasswordViewModel.UIEvent.OnValidatePassword
import com.multimoney.multimoney.presentation.ui.login.signup.password.PasswordRequirementLabels
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomModalWarningBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangePasswordScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate
        )
    }

    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = stringResource(id = viewModel.uiState.alertResultTitle),
            descriptionString = stringResource(viewModel.uiState.alertResultDescription),
            buttonTextResource = R.string.profile_settings_error_try_again_later,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = {
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnAlertButtonClick)
            }
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnValidatePasswordStructure)

        viewModel.onPasswordSaveEvents.collect { event ->
            event.onSuccess { response ->
                when (response?.messageError?.status) {
                    ChangePasswordViewModel.VALID_PASSWORD -> {
                        viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnCallCognitoUpdatePassword)
                    }
                }
            }.onMessage {
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnUpdateLoadingState(false))
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnPasswordSameAsPrevious)
            }.onFailure {
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnShowAlertDialog)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onCognitoPasswordUpdateEvents.collect { event ->
            if (event.first) {
                Toast.makeText(
                    context,
                    context.getString(R.string.profile_settings_password_modified),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnUpdateLocallyStoredPassword)
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnNavigateBack)
            }
        }
    }
    BackHandler {
        when {
            bottomSheetState.isVisible -> {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
            else -> viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnNavigateBack)
        }
    }

    ChangePasswordContent(
        viewModel = viewModel,
        bottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangePasswordContent(
    viewModel: ChangePasswordViewModel,
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .padding(bottom = 40.dp)
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (contentColumn, changePasswordButton) = createRefs()
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .constrainAs(contentColumn) {
                        top.linkTo(parent.top)
                    }
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(id = R.string.profile_settings_change_password),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Left
                )
                CustomOutlinedTextField(
                    value = viewModel.uiState.currentPassword,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnCurrentPasswordValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_current_password),
                    isPassword = true,
                    modifier = Modifier.padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.currentPasswordError.first,
                    errorMessage = if (viewModel.uiState.currentPasswordError.first) {
                        stringResource(id = viewModel.uiState.currentPasswordError.second)
                    } else {
                        null
                    }
                )

                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.profile_settings_forget_password)),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.End),
                    style = Typography.body2.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MultimoneyTheme.colors.textLink
                    ),
                    onClick = { viewModel.onUIEvent(OnNavigateToForgotPassword) }
                )

                CustomOutlinedTextField(
                    value = viewModel.uiState.newPassword,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnNewPasswordValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_new_password),
                    isPassword = true,
                    modifier = Modifier.padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.newPasswordError.first,
                    errorMessage = if (viewModel.uiState.newPasswordError.first) {
                        if (viewModel.uiState.newPasswordError.second == R.string.sign_up_password_requirement_forbidden_words) {
                            stringResource(
                                id = R.string.sign_up_password_requirement_forbidden_words,
                                viewModel.getForbiddenWords(viewModel.uiState.newPassword)
                            )
                        } else {
                            stringResource(id = viewModel.uiState.newPasswordError.second)
                        }
                    } else {
                        null
                    }
                )

                CustomOutlinedTextField(
                    value = viewModel.uiState.newPasswordConfirmation,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnNewPasswordConfirmationValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_confirm_new_password),
                    isPassword = true,
                    modifier = Modifier.padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.newPasswordConfirmationError.first,
                    errorMessage = if (viewModel.uiState.newPasswordConfirmationError.first) {
                        if (viewModel.uiState.newPasswordConfirmationError.second == R.string.sign_up_password_requirement_forbidden_words) {
                            stringResource(
                                id = R.string.sign_up_password_requirement_forbidden_words,
                                viewModel.getForbiddenWords(viewModel.uiState.newPasswordConfirmation)
                            )
                        } else {
                            stringResource(id = viewModel.uiState.newPasswordConfirmationError.second)
                        }
                    } else {
                        null
                    }
                )
                FlowRow(
                    Modifier.padding(top = 8.dp)
                ) {
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
                        state = viewModel.uiState.eightCharactersMinimumState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_uppercase),
                        state = viewModel.uiState.oneUppercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_lowercase),
                        state = viewModel.uiState.oneLowercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_number),
                        state = viewModel.uiState.oneNumberState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_characer),
                        state = viewModel.uiState.oneCharacterState
                    )
                }
            }
            val chainRef = createVerticalChain(
                contentColumn,
                changePasswordButton,
                chainStyle = ChainStyle.SpreadInside
            )

            constrain(chainRef) {
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            }
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 16.dp, end = 16.dp)
                    .constrainAs(changePasswordButton) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(contentColumn.bottom)
                        verticalChainWeight = 1f
                    },
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.profile_settings_change_password_button),
                enable = viewModel.uiState.isButtonEnabled,
                onClick = {
                    viewModel.onUIEvent(OnValidatePassword)
                }
            )
        }
    }
    CustomModalWarningBottomSheet(
        titleResource = R.string.password_security_bottom_sheet_general_title,
        descriptionText = buildAnnotatedString {
            withStyle(
                style = Typography.subtitle1.toSpanStyle().copy(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.password_security_bottom_sheet_general_description))
            }
            withStyle(
                style = Typography.subtitle1.toSpanStyle()
            ) {
                append(stringResource(id = R.string.password_security_bottom_sheet_signup_description))
            }
        },
        modalBottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope
    )
    LoadingIndicator(viewModel.uiState.isLoading)
}
