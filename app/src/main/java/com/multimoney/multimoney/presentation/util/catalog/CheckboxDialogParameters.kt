package com.multimoney.multimoney.presentation.util.catalog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.multimoney.multimoney.R

data class CheckboxDialogParameters(
    val titleResource: Int = R.string.error,
    val description: String = "",
    val checkboxResource: Int = R.string.common_not_show_again,
    val descriptionResource: Int = R.string.empty,
    val isActive: MutableState<Boolean> = mutableStateOf(false),
    val positiveResource: Int = R.string.accept,
    val negativeResource: Int = R.string.empty,
    var positiveAction: (Boolean) -> Unit = {},
    var negativeAction: () -> Unit = {},
    var dismissAction: () -> Unit = {},
    var isCancelable: Boolean = true,
    var additionalText: String = ""
)
