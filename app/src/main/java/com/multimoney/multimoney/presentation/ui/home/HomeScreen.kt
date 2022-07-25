package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCallValidateUserStatus
import com.multimoney.multimoney.presentation.ui.home.product.ProductScreen
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlin.random.Random

@Composable
@Preview
fun HomeScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallValidateUserStatus())
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
        }
    }

    if (getRandom() == ZERO) {
        ProductScreen()
    } else {
        LazyColumn {
            viewModel.uiState.balanceCredit?.balanceCredit?.forEachIndexed { index, balanceCredit ->
                item {
                    Text(
                        text = "Balance Credit $index",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                balanceCredit?.summary?.let { summaryList ->
                    items(summaryList) { summary ->
                        Text(
                            text = "Linea de crédito: ${summary.availableBalanceLabel}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                        Text(
                            text = "Saldo de crédito: ${summary.currentBalanceLabel}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                        Text(
                            text = "Monto de cuota por pagar: ${summary.monthlyQuotaLabel}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                        Text(
                            text = "Fecha de vencimiento: ${summary.paymentDateLabel}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                    }
                }
            }
            viewModel.uiState.balanceCredit?.balanceCardInformation?.let { balanceCardInformation ->
                item {
                    Text(
                        text = "Balance Card Information",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "Ultimos 4 digitos tarjeta: ${
                            balanceCardInformation.cardInformation?.cardNumber?.substring(
                                12,
                                16
                            )
                        }",
                        modifier = Modifier
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary
                        )
                    )
                    Text(
                        text = "Saldo disponible:  N/A",
                        modifier = Modifier
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary
                        )
                    )
                }
            }

            viewModel.uiState.balanceCredit?.balanceAccountSmart?.let { balanceAccountSmart ->
                item {
                    Text(
                        text = "Balance Account Smart",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                balanceAccountSmart.account?.let { accountList ->
                    items(accountList) { account ->
                        Text(
                            text = "Saldo Unimoneda: N/A",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                        Text(
                            text = "Saldo total: ${account.totalBalance}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                        Text(
                            text = "Ganancia del ultimo mes: ${account.gainedInterest}",
                            modifier = Modifier
                                .padding(top = 24.dp),
                            style = Typography.subtitle1.copy(
                                color = MultimoneyTheme.colors.primary
                            )
                        )
                    }
                }
            }

            viewModel.uiState.balanceCredit?.balanceCryptoAccount?.let { balanceCryptoAccount ->
                item {
                    Text(
                        text = "Balance Crypto Account",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "Saldo Total: ${balanceCryptoAccount.globalBalance}",
                        modifier = Modifier
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary
                        )
                    )
                    Text(
                        text = "Ganancias y/o perdida del dia: N/A",
                        modifier = Modifier
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary
                        )
                    )
                    Text(
                        text = "Grafica de fluctuacion de saldos del ultimo mes: N/A",
                        modifier = Modifier
                            .padding(top = 24.dp),
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.primary
                        )
                    )
                }
            }
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}

fun getRandom(): Int {
    return Random.nextInt(2)
}

const val ZERO = 0