package com.multimoney.multimoney.presentation.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.multimoney.multimoney.R

data class DialogParameters(
    val title: Int = R.string.error,
    val description: String = "",
    val isActive: MutableState<Boolean> = mutableStateOf(false),
    val positiveText: Int = R.string.accept,
    val negativeText: Int = R.string.error,
    val positiveAction: () -> Unit = {},
    val negativeAction: () -> Unit = {}
)
