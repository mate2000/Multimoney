package com.multimoney.multimoney.presentation.ui.payment.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrency

@Composable
fun PaymentAccountScreen(
    navBackStackEntry: NavBackStackEntry,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
            navBackStackEntry.arguments?.apply {
                viewModel.onUIEvent(
                    OnSaveArguments(
                        getString(USER) ?: "",
                        getInt(ID_BRAND),
                        getInt(ID_CLIENT),
                        getInt(ID_LOAN_CLIENT),
                        (get(SUMMARY_LIST) as Array<Summary>).toList()
                    )
                )
            }
            onUIEvent(OnGetTextResources)
            onUIEvent(OnCallQueryGetClientBankAccount())
        }
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Text(
            modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = viewModel.uiState.titleResource),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        viewModel.uiState.clientBankAccountList?.let { clientBankAccountList ->
            LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
                items(clientBankAccountList) { clientBankAccount ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        startIcon = clientBankAccount?.idCurrency?.getCurrency()?.accountIcon ?: 0,
                        title = clientBankAccount?.bankDescription ?: "",
                        subtitle = viewModel.getMaskedAccount(
                            clientBankAccount?.accountNumber ?: "",
                            stringResource(id = R.string.payment_account_masked_text)
                        ),
                        onClick = {
                            viewModel.onUIEvent(OnClientBankAccountSelected(clientBankAccount))
                        }
                    )
                }
            }
        }
        CustomButton(
            text = stringResource(id = R.string.payment_account_create),
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            onClick = {
                Toast.makeText(context, "TBD", Toast.LENGTH_SHORT).show()
            },
            buttonType = CustomButtonType.PrimaryTertiary,
            trailingIcon = R.drawable.ic_plus
        )
    }

    LoadingIndicator(viewModel.uiState.isLoading)

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
