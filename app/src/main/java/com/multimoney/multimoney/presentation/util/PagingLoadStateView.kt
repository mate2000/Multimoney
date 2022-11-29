package com.multimoney.multimoney.presentation.util

import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

@Composable
fun PagingLoadStateView(
    loadState: CombinedLoadStates?,
    onLoad: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    loadState?.apply {
        when {
            // start loading
            refresh is LoadState.Loading || append is LoadState.Loading -> onLoad(true)

            // stop loading
            refresh is LoadState.NotLoading && append is LoadState.NotLoading -> onLoad(false)

            // errors
            refresh is LoadState.Error || append is LoadState.Error -> {
                val loadError = loadState.append as LoadState.Error
                onError(loadError.error.message ?: "")
            }
        }
    }
}
