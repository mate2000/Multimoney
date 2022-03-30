package com.multimoney.multimoney.presentation.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {

    var isLoading by mutableStateOf(false)

    @Inject
    lateinit var connectivity: Connectivity

    /**
     * Use this val to store one time events defined in NavigationEvent Class
     **/
    private val _navigationEvent = Channel<NavEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

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
     * Use this function to trigger one time events defined in NavigationEvent Class
     **/
    fun sendNavigationEvent(event: NavEvent) {
        viewModelScope.launch {
            _navigationEvent.send(event)
        }
    }

    /**
     * Use this function to navigate to specified screen
     **/
    fun navigateTo(route: String) = sendNavigationEvent(NavEvent.Navigate(route = route))

    /**
     * Use this function to pop to specific screen and navigate to specified screen
     **/
    fun popAndNavigateTo(route: String, popTo: String) =
        sendNavigationEvent(NavEvent.PopAndNavigate(route = route, popTo = popTo))

    fun navigateBack() = sendNavigationEvent(NavEvent.PopBackStack)

    fun executeNavigation(
        onNavigate: (NavEvent.Navigate) -> Unit = {},
        onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
        popBackStack: () -> Unit = {}
    ) {
        viewModelScope.launch {
            navigationEvent.collect { event ->
                when (event) {
                    is NavEvent.Navigate -> onNavigate(event)
                    is NavEvent.PopAndNavigate -> onPopAndNavigate(event)
                    is NavEvent.PopBackStack -> popBackStack()
                    else -> Unit
                }
            }
        }
    }
}
