package com.multimoney.multimoney.presentation.ui.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val mutationUserValidationUseCase: MutationUserValidationUseCase,
    val onFidoHelper: OnFidoHelper
) : BaseViewModel() {

    var data by mutableStateOf<UserData?>(null)
    fun mutationUserValidationUseCase() = executeUseCase {
        isLoading = true
        mutationUserValidationUseCase(
            email = "popics93@gmail.com",
            currentStep = "Register_Step1",
            idBrand = 5
        ).collectLatest { result ->
            result.onSuccess {
                data = it
                isLoading = false
            }
            result.onFailure {
                isLoading = false
                val error2 = it.getError()
            }
            result.onLoading {
                isLoading = true
            }
        }
    }

    fun navigateToChart() {
        navigateTo(Screen.ChartScreen.route)
    }

}