package com.multimoney.multimoney.presentation.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.domain.interaction.security.MutationSaveLogTrackingUseCase
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.AdjustHelper
import com.multimoney.multimoney.util.firebase.FireBaseEventHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    val shouldObserveToken: Boolean
) : ViewModel() {

    var isOnRestart by mutableStateOf(true)

    var openDialog by mutableStateOf(DialogParameters())

    var baseEvent = MutableSharedFlow<Any>()

    @Inject
    lateinit var connectivity: Connectivity

    @Inject
    lateinit var preferences: DataStorePreferences

    @Inject
    lateinit var provideFireBaseEventHelper: FireBaseEventHelper

    @Inject
    lateinit var adjustHelper: AdjustHelper

    @Inject
    lateinit var mutationSaveLogTrackingUseCase: MutationSaveLogTrackingUseCase

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
        crossinline noInternetAction: suspend () -> Unit
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
        crossinline action: suspend () -> Unit
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
     * Thi function is only use for handle the BottomNavigation navigation
     */
    fun innerNavigateTo(innerNavigate: NavHostController, route: String) =
        sendNavigationEvent(NavEvent.InnerNavigate(innerNavigate = innerNavigate, route = route))

    /**
     * Use this function to pop to specific screen and navigate to specified screen
     **/
    fun popAndNavigateTo(route: String, popTo: String) =
        sendNavigationEvent(NavEvent.PopAndNavigate(route = route, popTo = popTo))

    fun navigateBack(popTo: String, isRestart: Boolean, homeState: HomeState = HomeState.OLD_STATE) =
        sendNavigationEvent(NavEvent.PopBackStack(popTo = popTo, isRestart = isRestart, homeState = homeState))

    fun executeNavigation(
        onNavigate: (NavEvent.Navigate) -> Unit = {},
        onInnerNavigate: (innerNavigate: NavHostController, NavEvent.InnerNavigate) -> Unit = { _, _ -> },
        onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
        onPopBackStack: (NavEvent.PopBackStack) -> Unit = {}
    ) {
        viewModelScope.launch {
            navigationEvent.collectLatest { event ->
                when (event) {
                    is NavEvent.Navigate -> onNavigate(event)
                    is NavEvent.InnerNavigate -> onInnerNavigate(event.innerNavigate, event)
                    is NavEvent.PopAndNavigate -> onPopAndNavigate(event)
                    is NavEvent.PopBackStack -> onPopBackStack(event)
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

    fun registerAdjustEvent(
        adjustEventType: AdjustEventType,
        listParameters: List<Pair<String, String>> = listOf(),
        isLoggedIn: Boolean = true,
        data: String = ""
    ) {
        viewModelScope.launch {
            val mutableList = mutableListOf<Pair<String, String>>()
            if (isLoggedIn) {
                val idBrand = preferences.getIdBrand().firstOrNull() ?: ""
                val email = preferences.getUserEmail().firstOrNull() ?: ""
                val pkUser = preferences.getPkUser().firstOrNull() ?: ""
                val identification = preferences.getIdentification().firstOrNull() ?: ""
                mutableList.add(Pair(ID_BRAND_ADJUST_KEY, idBrand))
                mutableList.add(Pair(EMAIL_ADJUST_KEY, email))
                mutableList.add(Pair(PK_USER_ADJUST_KEY, pkUser))
                mutableList.add(Pair(IDENTIFICATION_ADJUST_KEY, identification))
                if (data.isNotEmpty()) {
                    callSaveLogTracking(identification, pkUser, data, idBrand)
                }
            }
            mutableList.addAll(listParameters)
            adjustHelper.registerEvent(adjustEventType, mutableList)
        }
    }

    private fun callSaveLogTracking(
        identification: String,
        pkUser: String,
        data: String,
        idBrand: String
    ) = executeUseCase {
        mutationSaveLogTrackingUseCase.invoke(
            identification = identification,
            pkUser = pkUser.toInt(),
            keySearch = DEFAULT_ADJUST_KEY,
            data = data,
            idBrand = idBrand.toInt()
        ).collectLatest { result ->
            result.onSuccess {
            }
        }
    }

    companion object {
        private const val ID_BRAND_ADJUST_KEY = "idbrand"
        private const val EMAIL_ADJUST_KEY = "email"
        private const val PK_USER_ADJUST_KEY = "pkUser"
        private const val IDENTIFICATION_ADJUST_KEY = "identification"
        private const val DEFAULT_ADJUST_KEY = "default"
    }
}
