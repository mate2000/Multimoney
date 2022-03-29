package com.multimoney.multimoney.presentation.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.multimoney.presentation.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {

    var isLoading by mutableStateOf(false)

    @Inject
    lateinit var connectivity: Connectivity

    /**
     * Use this val to store one time events defined in UiEvent Class
     **/
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    /**
     * Use this function to call use cases in a coroutine in the viewModel
     * action: is the use case you want to call
     **/
    inline fun executeUseCase(
        crossinline action: suspend () -> Unit,
        crossinline noInternetAction: suspend () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivity.hasNetworkAccess()) {
                action()
            } else {
                noInternetAction()
            }
        }
    }

    inline fun executeUseCase(
        checkConnection: Boolean = true,
        crossinline action: suspend () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (checkConnection) {
                if (connectivity.hasNetworkAccess()) {
                    action()
                }
            } else {
                action()
            }
        }
    }

    /**
     * Use this function to trigger one time events defined in UiEvent Class
     **/
    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    /**
     * Use this function to navigate to specified screen
     **/
    fun navigateToScreen(screen: String) {
        sendUiEvent(
            UiEvent.Navigate(
                route = screen
            )
        )
    }

    fun goBack() {
        sendUiEvent(UiEvent.PopBackStack)
    }
}
