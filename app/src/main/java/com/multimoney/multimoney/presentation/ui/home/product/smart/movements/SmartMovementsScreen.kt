package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsViewModel.UIEvent.OnGetMovement
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.API_COLONES
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
        viewModel.onUIEvent(OnGetMovement(viewModel.uiState.currentPage, PAGE_SIZE))
    }

    TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = { onNavigate }
    )

    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .verticalScroll(scrollState)
    ) {
        Log.d("AAASTATE", "STATE = ${scrollState.value} - MAX = ${scrollState.maxValue}")

        if (
            scrollState.value == scrollState.maxValue &&
            scrollState.maxValue != 0 &&
            !viewModel.uiState.isLoading &&
            viewModel.uiState.moreRecordsAvailable
        ) {
            Log.d("AAACALL", "STATE = ${scrollState.value} - MAX = ${scrollState.maxValue} - PAGE = ${viewModel.uiState.currentPage}")
            viewModel.onUIEvent(OnGetMovement(viewModel.uiState.currentPage, PAGE_SIZE))
        }

        Text(
            text = stringResource(string.home_product_movement_title),
            style = Typography.h4.copy(
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
        )
        viewModel.uiState.smartMovementsList.forEach {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currencySymbol = if (it.currencyDescription == API_COLONES) {
                    CostaRica.id.getCurrencySymbol()
                } else {
                    ElSalvador.id.getCurrencySymbol()
                }

                val symbol = if (it.amount > 0) {
                    drawable.ic_plus
                } else {
                    drawable.ic_close
                }

                Column(Modifier.weight(2f)) {
                    Text(
                        text = it.transactionCatalogueDescription,
                        style = Typography.subtitle2.copy(
                            color = MultimoneyTheme.colors.labelText
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = parseApiDateToCardDate(it.creationDate),
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
                        text = stringResource(currencySymbol) + it.amount.toString(),
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
const val PAGE_SIZE = 10
