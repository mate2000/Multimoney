package com.multimoney.multimoney.presentation.ui.payment.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_CURRENCY
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnSetCurrency
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

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
            onUIEvent(OnSetCurrency(navBackStackEntry.arguments?.getString(PAYMENT_CURRENCY, "") ?: ""))
            onUIEvent(OnGetTextResources)
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
            modifier = Modifier.padding(top = 34.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = viewModel.uiState.titleResource),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        LazyColumn {
            items(listOf("Dollar", "Colon")) { account ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                    startIcon = R.drawable.ic_payment_colon,
                    title = stringResource(id = R.string.payment_fee_one_option) + account,
                    subtitle = "₡5,000",
                    onClick = {}
                )
            }
        }
        CustomButton(
            text = stringResource(id = R.string.payment_account_create),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                Toast.makeText(context, "TBD", Toast.LENGTH_SHORT).show()
            },
            buttonType = CustomButtonType.PrimaryTertiary,
            trailingIcon = R.drawable.ic_plus
        )
    }
}
