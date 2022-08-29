package com.multimoney.multimoney.presentation.ui.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val mutationUserValidationUseCase: MutationUserValidationUseCase,
    val onFidoHelper: OnFidoHelper
) : BaseViewModel() {

    var data by mutableStateOf<UserData?>(null)

    fun navigateToChart() {
        navigateTo(Screen.ChartScreen.route)
    }

}