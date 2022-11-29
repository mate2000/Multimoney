package com.multimoney.multimoney.presentation.util

import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

/**
 * PagingLoadStateView is used to handle the states of a pagination source (When using Paging 3)
 *
 * Parameters:
 * @param loadState: Takes the LoadState of the collected lazy paging items we want to handle
 * @param onLoad: Function to handle loading state. Returning boolean indicates (true = Loading, false = Not Loading)
 * @param onError: Function to handle error while loading items. Returns string with the error message
 */

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

const val INDEX_ONE = 1
const val PAGE_SIZE = 10
const val LAST_THREE = 3
