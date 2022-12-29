package com.multimoney.multimoney.presentation.ui.crypto.buycrypto

import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val cognitoHelper: CognitoHelper,
    private val countDownTimer: MMCountDownTimer
) : BaseViewModel(true) {
}