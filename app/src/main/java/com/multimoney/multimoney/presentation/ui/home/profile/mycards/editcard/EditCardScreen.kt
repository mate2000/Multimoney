package com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnNicknameValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnCvvValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnDisclaimerClick
import com.multimoney.multimoney.presentation.uielement.*

@Composable
fun EditCardScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: EditCardViewModel = hiltViewModel()
) {
    // Properties

    val focusManager = LocalFocusManager.current

    // Navigation

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }

    //View

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopBar { viewModel.onUIEvent(OnBackClick(focusManager)) }
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Title(title = R.string.profile_my_cards_edit_card_title)
                CardBox(
                    title = viewModel.uiState.nickname,
                    subtitle = viewModel.cardMasked.orEmpty()
                )
                CardNickName(
                    value = viewModel.uiState.nickname,
                    onValueChange = { viewModel.onUIEvent(OnNicknameValueChange(it)) },
                    onError = viewModel.uiState.nicknameError,
                    focusManager = focusManager
                )
                Row {
                    CardValidDate(
                        value = viewModel.cardValidDate.orEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                    CardCVV(
                        value = viewModel.uiState.cvv,
                        onValueChange = { viewModel.onUIEvent(OnCvvValueChange(it)) },
                        onDisclaimerClick = { viewModel.onUIEvent(OnDisclaimerClick(focusManager)) },
                        onError = viewModel.uiState.cvvError,
                        focusManager = focusManager,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        SaveChanges(enable = viewModel.uiState.isSaveChangesEnabled) {
        }
    }

    LoadingIndicator(isLoading = viewModel.uiState.isLoading)
    Dialog(openDialog = viewModel.uiState.openDialog)
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = onBackClick
    )
}

@Composable
private fun Title(title: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = Typography.h6.toSpanStyle()
                    .copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
            ) {
                append(stringResource(id = title))
            }
        },
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CardBox(
    title: String,
    subtitle: String,
) {
    CustomInfoButton(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        imageModifier = Modifier.size(48.dp),
        startIcon = R.drawable.ic_visa_card_item,
        endIcon = null,
        title = title,
        subtitle = stringResource(
            id = R.string.visa_card_masked_number,
            subtitle
        )
    )
}

@Composable
private fun CardNickName(
    value: String,
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager
) {
    CustomOutlinedTextField(
        modifier = Modifier
            .padding(top = 24.dp),
        value = value,
        onValueChange = onValueChange,
        labelText = stringResource(id = R.string.profile_my_cards_edit_card_nickname_label),
        placeHolder = value,
        isRequiredMessage = stringResource(id = R.string.profile_my_cards_edit_card_field_required),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        isError = onError.first,
        errorMessage = stringResource(id = onError.second)
    )
}

@Composable
private fun CardValidDate(
    value: String,
    modifier: Modifier = Modifier
) {
    CustomOutlinedTextField(
        modifier = modifier
            .padding(end = 4.dp, top = 16.dp),
        value = value,
        isClickable = true,
        labelText = stringResource(id = R.string.profile_my_cards_edit_card_valid_until_label),
        placeHolder = stringResource(id = R.string.profile_my_cards_edit_card_valid_until_placeholder),
        keyboardOptions = KeyboardOptions(),
        keyboardActions = KeyboardActions()
    )
}

@Composable
private fun CardCVV(
    value: String,
    onValueChange: (String) -> Unit,
    onDisclaimerClick: () -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    CustomOutlinedTextField(
        modifier = modifier
            .padding(start = 4.dp, top = 16.dp),
        value = value,
        onValueChange = onValueChange,
        labelText = stringResource(id = R.string.profile_my_cards_edit_card_cvv_label),
        placeHolder = value,
        trailingIconActionEnabled = true,
        trailingIcon = R.drawable.ic_information,
        trailingIconAction = onDisclaimerClick,
        isRequiredMessage = stringResource(id = R.string.profile_my_cards_edit_card_field_required),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        isError = onError.first,
        errorMessage = stringResource(id = onError.second)
    )
}

@Composable
private fun SaveChanges(enable: Boolean, onClick: () -> Unit) {
    CustomButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        onClick = onClick,
        buttonType = CustomButtonType.PrimaryPrimary,
        text = stringResource(id = R.string.profile_my_cards_edit_card_save_changes_label),
        enable = enable
    )
}

@Composable
private fun Dialog(openDialog: DialogParameters) {
    if (openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = openDialog.titleResource),
            message = stringResource(id = openDialog.descriptionResource),
            positiveButtonText = stringResource(id = openDialog.positiveResource),
            negativeButtonText = stringResource(id = openDialog.negativeResource),
            openDialogCustom = openDialog.isActive,
            onPositiveAction = openDialog.positiveAction
        )
    }
}