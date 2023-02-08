@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButtonFavoriteAccount
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView
import com.multimoney.multimoney.presentation.uielement.SimpleItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyAccountsScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: MyAccountsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current

    viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnStart)

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    BackHandler {
        if (viewModel.uiState.isEditing)
            viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnGoBackToMyAccounts)
        else
            viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnNavigateBack)
    }

    Scaffold(scaffoldState = scaffoldState) {
        if (viewModel.uiState.showSnackBar) {
            val message = stringResource(id = viewModel.uiState.snackBarTitleResource)
            LaunchedEffect(scaffoldState.snackbarHostState) {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(message)
                when (snackBarResult) {
                    SnackbarResult.Dismissed -> {
                        viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnDismissSnackBar)
                    }
                    SnackbarResult.ActionPerformed -> {
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize()
        ) {
            TopNavBar(
                onLeftButtonClick = {
                    if (viewModel.uiState.isEditing)
                        viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnGoBackToMyAccounts)
                    else
                        viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnNavigateBack)
                },
                isRightButtonVisible = false
            )
            if (viewModel.uiState.isEditing.not()) {
                Text(
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.profile_my_accounts),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Left
                )
            }
            if (viewModel.uiState.isEditing) {
                EditAccountScreen(
                    viewModel,
                    focusManager
                )
            } else {
                if (viewModel.uiState.favoritesLoaded && viewModel.uiState.registeredLoaded) {
                    if (viewModel.uiState.favoriteAccounts.isEmpty() && viewModel.uiState.registeredAccounts.isEmpty()) {
                        MyAccountsEmptyState(viewModel.uiState.idBrand)
                    }
                    MyAccountsContent(
                        favoriteAccounts = viewModel.uiState.favoriteAccounts,
                        registeredAccounts = viewModel.uiState.registeredAccounts,
                        viewModel = viewModel
                    )
                } else {
                    MyAccountsSkeleton()
                }
            }
        }

        BottomSheetDialog(
            modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
            coroutineScope = coroutineScope,
            firstActionTitle = stringResource(id = viewModel.uiState.favoriteTextResource),
            firstActionIcon = if( viewModel.uiState.selectedAccount?.isFavorite == true) R.drawable.ic_error_green else R.drawable.ic_star_outline,
            firstActionClick = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnChangeFavorite)
            },
            secondActionTitle = stringResource(id = R.string.profile_my_accounts_edit_nickname),
            secondActionIcon = R.drawable.ic_edit_green,
            secondActionClick = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnEditNickname)
            },
            thirdActionTitle = stringResource(id = R.string.profile_my_accounts_delete_account),
            thirdActionIcon = R.drawable.ic_delete,
            thirdActionClick = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnOpenDeleteDialog)
            }
        )

        if (viewModel.uiState.favoriteDialogParemeters.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.favoriteDialogParemeters.titleResource),
                message = stringResource(id = viewModel.uiState.favoriteDialogParemeters.descriptionResource),
                negativeButtonText = stringResource(id = viewModel.uiState.favoriteDialogParemeters.negativeResource),
                positiveButtonText = stringResource(id = viewModel.uiState.favoriteDialogParemeters.positiveResource),
                openDialogCustom = viewModel.uiState.favoriteDialogParemeters.isActive,
                onPositiveAction = {
                    viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnDeleteAccount)
                }
            )
        }

        if (viewModel.uiState.showErrorDialog) {
            AlertResult(
                titleString = stringResource(id = R.string.profile_settings_error_new_password_something_went_wrong),
                descriptionString = stringResource(id = R.string.profile_settings_error_we_are_sorry_try_again_later),
                buttonTextResource = R.string.profile_error_changing_phone_button,
                isLeftButtonVisible = false,
                isRightButtonVisible = false,
                onButtonClick = {
                    viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnNavigateBack)
                }
            )
        }
        LoadingIndicator(viewModel.uiState.isLoading)
    }
}

@Composable
fun BottomSheetDialog(
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    firstActionTitle: String,
    firstActionClick: () -> Unit,
    firstActionIcon: Int,
    secondActionTitle: String,
    secondActionClick: () -> Unit,
    secondActionIcon: Int,
    thirdActionTitle: String,
    thirdActionClick: () -> Unit,
    thirdActionIcon: Int,
) {
    CustomModalBottomSheet(
        title = R.string.profile_my_acounts_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        SimpleItemRow(
            title = firstActionTitle,
            startIcon = firstActionIcon,
            onClick = firstActionClick
        )
        SimpleItemRow(
            title = secondActionTitle,
            startIcon = secondActionIcon,
            onClick = {
                secondActionClick.invoke()
            }
        )
        SimpleItemRow(
            title = thirdActionTitle,
            startIcon = thirdActionIcon,
            onClick = thirdActionClick
        )
    }
}

@Composable
fun MyAccountsContent(
    favoriteAccounts: List<SinpeAccount?> = listOf(),
    registeredAccounts: List<SinpeAccount?> = listOf(),
    viewModel: MyAccountsViewModel
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        LazyColumn {
            if (favoriteAccounts.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = stringResource(id = R.string.profile_accounts_favorites),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText,
                        textAlign = TextAlign.Left
                    )
                }
                items(favoriteAccounts) { account ->
                    CustomInfoButtonFavoriteAccount(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        startIcon = R.drawable.ic_bank_account,
                        endIcon = R.drawable.ic_options,
                        title = account?.nameAccount ?: "",
                        subtitle = getMaskedAccount(
                            account?.sinpeAccount ?: "",
                            stringResource(id = R.string.payment_account_masked_text)
                        ),
                        onEndIconClick = {
                            if (account != null)
                                viewModel.onUIEvent(
                                    MyAccountsViewModel.UIEvent.OnAccountClicked(
                                        account
                                    )
                                )
                        }
                    )
                }
            }
            if (registeredAccounts.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = stringResource(id = R.string.profile_accounts_registered),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText,
                        textAlign = TextAlign.Left
                    )
                }
                items(registeredAccounts) { account ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        startIcon = R.drawable.ic_bank_account,
                        endIcon = R.drawable.ic_options,
                        title = account?.nameAccount ?: "",
                        subtitle = getMaskedAccount(
                            account?.sinpeAccount ?: "",
                            stringResource(id = R.string.payment_account_masked_text)
                        ),
                        onEndIconClick = {
                            if (account != null)
                                viewModel.onUIEvent(
                                    MyAccountsViewModel.UIEvent.OnAccountClicked(
                                        account
                                    )
                                )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditAccountScreen(
    viewModel: MyAccountsViewModel,
    focusManager: FocusManager
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (titleId, accountId, inputId, buttonId) = createRefs()
        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(titleId) {
                    top.linkTo(parent.top)
                },
            text = stringResource(id = R.string.profile_my_accounts_edit_nickname),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        CustomInfoButtonFavoriteAccount(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 16.dp)
                .constrainAs(accountId) {
                    top.linkTo(titleId.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
            startIcon = R.drawable.ic_bank_account,
            endIcon = null,
            title = viewModel.uiState.selectedAccount?.nameAccount ?: "",
            subtitle = getMaskedAccount(
                viewModel.uiState.selectedAccount?.sinpeAccount ?: "",
                stringResource(id = R.string.payment_account_masked_text)
            ),
        )
        CustomOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .constrainAs(inputId) {
                    top.linkTo(accountId.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onValueChange = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnValueChanged(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.profile_my_accounts_account_nickname),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.profile_my_account_nickname_is_required),
            value = viewModel.uiState.accountNickname
        )
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(buttonId) {
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = stringResource(id = R.string.profile_my_accounts_save_changes),
            onClick = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnUpdateAccount)
            },
            enable = viewModel.uiState.isButtonEnabled
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun MyAccountsSkeleton() {
    ShimmerBoxView() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    radius = 12.dp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    radius = 12.dp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    radius = 12.dp,
                )
            }
        }
    }
}

@Preview
@Composable
fun MyAccountsEmptyState(idBrand: Int = 1) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (iconId, titleId) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.ic_bank),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(iconId) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )
        Text(
            modifier = Modifier
                .constrainAs(titleId) {
                    top.linkTo(iconId.bottom)
                    end.linkTo(iconId.end)
                    start.linkTo(iconId.start)
                }
                .padding(top = 24.dp),
            text = if (idBrand == Brand.Guatemala.id) stringResource(id = R.string.profile_my_accounts_empty_state_title_gt) else stringResource(
                id = R.string.profile_my_accounts_empty_state_title
            ),
            style = Typography.body1.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.labelText
            ),
        )
    }
}