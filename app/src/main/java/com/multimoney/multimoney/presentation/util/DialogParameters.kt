package com.multimoney.multimoney.presentation.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.multimoney.multimoney.R

data class DialogParameters(
    val titleResource: Int = R.string.error,
    val description: String = "",
    val descriptionResource: Int = R.string.empty,
    val isActive: MutableState<Boolean> = mutableStateOf(false),
    val positiveResource: Int = R.string.accept,
    val negativeResource: Int = R.string.empty,
    var positiveAction: () -> Unit = {},
    var negativeAction: () -> Unit = {},
    var dismissAction: () -> Unit = {}
)
