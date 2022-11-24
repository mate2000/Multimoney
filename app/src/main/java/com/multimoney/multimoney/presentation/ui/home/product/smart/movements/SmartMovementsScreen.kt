package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnNavigateBackToHome
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.API_COLONES
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate

@Composable
fun SmartMovementsScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SmartMovementsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnGetMovement)
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description.ifBlank {
                stringResource(viewModel.uiState.openDialog.descriptionResource)
            },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    val smartMoves = viewModel.uiState.movementsPage.collectAsLazyPagingItems()

    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isLeftButtonVisible = true,
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBackToHome) }
        )

        Text(
            text = stringResource(string.home_product_movement_title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
        )
        MovementsList(smartMoves)
    }
}

@Composable
fun MovementsList(smartMoves: LazyPagingItems<SmartMovement>) {
    LazyColumn {
        items(items = smartMoves) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currencySymbol = if (it?.currencyDescription == API_COLONES) {
                    CostaRica.id.getCurrencySymbol()
                } else {
                    ElSalvador.id.getCurrencySymbol()
                }

                val symbol = if ((it?.amount ?: 0.0) > 0) {
                    drawable.ic_plus
                } else {
                    drawable.ic_close
                }

                Column(Modifier.weight(2f)) {
                    Text(
                        text = it?.transactionCatalogueDescription ?: "",
                        style = Typography.subtitle2.copy(
                            color = MultimoneyTheme.colors.labelText
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = parseApiDateToCardDate(it?.creationDate),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.textSubhead
                        ),
                        maxLines = 1
                    )
                }
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(symbol),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(currencySymbol) + it?.amount.toString(),
                        style = Typography.subtitle1.copy(
                            textAlign = TextAlign.End,
                            color = MultimoneyTheme.colors.labelText,
                            fontWeight = FontWeight.W600
                        ),
                        maxLines = 1
                    )
                }
            }
            Divider(color = MultimoneyTheme.colors.dividerWhite30)
        }
    }
}
