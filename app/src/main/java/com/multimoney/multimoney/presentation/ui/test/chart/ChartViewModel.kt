package com.multimoney.multimoney.presentation.ui.test.chart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
) : BaseViewModel(false) {

    var data by mutableStateOf<UserData?>(null)
}