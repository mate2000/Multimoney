package com.multimoney.multimoney.presentation.ui.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.GetLaunchListUseCase
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val getLaunchListUseCase: GetLaunchListUseCase
) : BaseViewModel() {

    var data by mutableStateOf<LaunchConnection?>(null)
    fun getLaunchList() = executeUseCase {
        isLoading = true
        getLaunchListUseCase().collect { result ->
            result.onSuccess {
                data = it
                isLoading = false
            }
            result.onFailure {
                isLoading = false
            }
            result.onLoading {
                isLoading = true
            }
        }
    }

}