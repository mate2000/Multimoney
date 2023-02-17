package com.multimoney.multimoney.presentation.ui.crypto.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencyMovementItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import kotlinx.coroutines.flow.Flow

@Composable
fun CryptoMovementsScreenContent(
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    onBackPressed: () -> Unit
) {

    val movements = cryptoMovements.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isLeftButtonVisible = false,
            isRightButtonVisible = true,
            onRightButtonClick = onBackPressed
        )
        HeaderSection()
        when (movements.loadState.refresh) {
            is LoadState.Loading -> {
                MovementsSkeleton()
            }
            is LoadState.NotLoading -> {
                MovementsListSection(cryptoMovements = movements)
            }
            else -> {
                MovementsListSection(cryptoMovements = movements)
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.crypto_movements),
            style = Typography.h5.copy(color = MultimoneyTheme.colors.text, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun MovementsListSection(
    cryptoMovements: LazyPagingItems<CryptoCurrencyMovement>
) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        items(cryptoMovements) { movement ->
            CryptoCurrencyMovementItem(cryptoCurrencyMovement = movement)
        }
    }
}