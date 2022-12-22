package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

}