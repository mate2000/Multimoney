package com.multimoney.multimoney.presentation.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.util.firebase.FireBaseEventHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    val shouldObserveToken: Boolean,
) : ViewModel() {

    var isLoading by mutableStateOf(false)

    var openDialog by mutableStateOf(DialogParameters())

    var baseEvent = MutableSharedFlow<Any>()

    @Inject
    lateinit var connectivity: Connectivity

    @Inject
    lateinit var preferences: DataStorePreferences

    @Inject
    lateinit var provideFireBaseEventHelper: FireBaseEventHelper

    /**
     * Use this val to store one time events defined in NavigationEvent Class
     **/
    private val navigationEvent = MutableSharedFlow<NavEvent>()

    /**
     * Use this function to call use cases in a coroutine in the viewModel
     * action: is the use case you want to call
     **/
    inline fun executeUseCase(
        crossinline action: suspend () -> Unit,
        crossinline noInternetAction: suspend () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (shouldObserveToken && preferences.getAuthToken().first().isEmpty()) {
                popAndNavigateTo(Screen.SignInScreen.route, Screen.SignInScreen.route)
            } else if (connectivity.hasNetworkAccess()) {
                action()
            } else {
                noInternetAction()
            }
        }
    }

    inline fun executeUseCase(
        checkConnection: Boolean = true,
        crossinline action: suspend () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (shouldObserveToken && preferences.getAuthToken().first().isEmpty()) {
                popAndNavigateTo(Screen.SignInScreen.route, Screen.SignInScreen.route)
            } else if (checkConnection) {
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
    private fun sendNavigationEvent(event: NavEvent) {
        viewModelScope.launch {
            navigationEvent.emit(event)
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
        onPopBackStack: () -> Unit = {},
    ) {
        viewModelScope.launch {
            navigationEvent.collectLatest { event ->
                when (event) {
                    is NavEvent.Navigate -> onNavigate(event)
                    is NavEvent.PopAndNavigate -> onPopAndNavigate(event)
                    is NavEvent.PopBackStack -> onPopBackStack()
                    else -> Unit
                }
            }
        }
    }

    fun emitBaseEvent(data: Any) {
        viewModelScope.launch {
            baseEvent.emit(data)
        }
    }
}
