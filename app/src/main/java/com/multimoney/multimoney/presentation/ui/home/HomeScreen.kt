package com.multimoney.multimoney.presentation.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.multimoney.multimoney.presentation.navigation.BottomNavItem
import com.multimoney.multimoney.presentation.navigation.navgraph.HomeInsideNavGraph
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun HomeScreen(
    onInnerNavigate: (innerNavController: NavHostController, NavEvent.InnerNavigate) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    /*
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
    }*/

    val navController = rememberNavController()

    LaunchedEffect(true) {
        viewModel.executeNavigation(onInnerNavigate = onInnerNavigate)
    }

    Scaffold(bottomBar = { MMBottomNavigation(navController = navController, viewModel) }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            HomeInsideNavGraph(navController = navController)
        }
    }
}

@Composable
fun MMBottomNavigation(navController: NavHostController, viewModel: HomeViewModel) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.QuickAction,
        BottomNavItem.Products
    )

    Column {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        BottomNavigation(
            modifier = Modifier.height(76.dp),
            backgroundColor = MultimoneyTheme.colors.background,
            contentColor = MultimoneyTheme.colors.bottomNavigationIconSelectedColor
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = item.icon), contentDescription = "") },
                    selectedContentColor = MultimoneyTheme.colors.bottomNavigationIconSelectedColor,
                    unselectedContentColor = MultimoneyTheme.colors.bottomNavigationIconUnselectedColor,
                    alwaysShowLabel = false,
                    selected = currentRoute == item.route,
                    onClick = {
                        viewModel.onUIEvent(
                            HomeViewModel.UIEvent.OnBottomNavigationItemClick(
                                navController,
                                item.route
                            )
                        )
                    }
                )
            }
        }
    }
}