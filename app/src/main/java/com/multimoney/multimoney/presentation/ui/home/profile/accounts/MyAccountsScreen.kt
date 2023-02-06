@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButtonFavoriteAccount
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.SimpleItemRow
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import kotlinx.coroutines.CoroutineScope

@Composable
fun MyAccountsScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: MyAccountsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnStart(stringResource(id = R.string.profile_my_account_account_updated)))

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }



    if (viewModel.uiState.showSnackBar) {
        Log.e("TAG","show snackbar")
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = viewModel.uiState.snackBarTitle
            )
            when (snackBarResult) {
                SnackbarResult.Dismissed -> {
                    Log.e("TAG","dismissed")
                    viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnDismissSnackBar)
                }
                SnackbarResult.ActionPerformed -> {
                    Log.e("TAG","performed")
                }
            }
        }

    }


    BackHandler {
        if (viewModel.uiState.isEditing)
            viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnGoBackToMyAccounts)
        else
            viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnNavigateBack)
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
        if (viewModel.uiState.isEditing) {
            EditAccountScreen(
                viewModel = viewModel
            )
        } else {
            MyAccountsContent(
                favoriteAccounts = viewModel.uiState.favoriteAccounts,
                registeredAccounts = viewModel.uiState.registeredAccounts,
                viewModel = viewModel
            )
        }
    }

    BottomSheetDialog(
        modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
        coroutineScope = coroutineScope,
        firstActionTitle = "Agregar como favorito",
        firstActionIcon = R.drawable.ic_error_green,
        firstActionClick = {},
        secondActionTitle = "Editar apodo",
        secondActionIcon = R.drawable.ic_edit_green,
        secondActionClick = {
            viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnEditNickname)
        },
        thirdActionTitle = "Eliminar cuenta",
        thirdActionIcon = R.drawable.ic_delete,
        thirdActionClick = {}
    )
    LoadingIndicator(viewModel.uiState.isLoading)
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
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.profile_my_accounts),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
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
    viewModel: MyAccountsViewModel
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
                //focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = "Apodo de la cuenta",
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.profile_my_account_nickname_is_required),
            value = viewModel.uiState.accountNickname
//                isError = viewModel.uiState.userEmailError.first,
//                errorMessage = stringResource(id = viewModel.uiState.userEmailError.second)

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
            text = "Guardar cambios",
            onClick = {
                viewModel.onUIEvent(MyAccountsViewModel.UIEvent.OnUpdateAccount)
            }
        )
    }
}
