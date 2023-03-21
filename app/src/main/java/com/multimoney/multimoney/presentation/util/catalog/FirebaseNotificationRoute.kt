package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.presentation.ui.home.HomeState

enum class FirebaseNotificationRoute(val route: String, val homeState: HomeState) {
    LOAN_MOVEMENTS("LoanMovements", HomeState.EXPANDED),
    HOME_CREDIT("HomeCredit", HomeState.EXPANDED),
    HOME("Home", HomeState.COLLAPSED)
}

fun getFirebaseNotificationRouteByRoute(route: String) = when (route) {
    FirebaseNotificationRoute.LOAN_MOVEMENTS.route -> FirebaseNotificationRoute.LOAN_MOVEMENTS.homeState
    FirebaseNotificationRoute.HOME_CREDIT.route -> FirebaseNotificationRoute.HOME_CREDIT.homeState
    FirebaseNotificationRoute.HOME.route -> FirebaseNotificationRoute.HOME.homeState
    else -> HomeState.OLD_STATE
}

fun shouldRestartFirebaseNotificationRoutePreferenceInHome(route: String) = when (route) {
    FirebaseNotificationRoute.LOAN_MOVEMENTS.route -> false
    else -> true
}
