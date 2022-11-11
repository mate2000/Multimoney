package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.SignDocumentProcessScreen

const val PK_USER = "pk_user"
const val IDENTIFICATION = "identification"
const val EMAIL = "email"
const val CREDIT_STEP = "credit_step"
const val ID_USER_REQUEST = "id_user_request"
const val FIRST_NAME = "name"
const val LAST_NAME = "last_name"
const val SIGN_DOCUMENT_STEP = "sign_document_step"
const val SIGN_DOCUMENT_URL = "sign_document_url"

fun NavGraphBuilder.creditNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.CreditScreen.route,
        route = CREDIT_ROUTE
    ) {
        composable(
            route = Screen.CreditScreen.route,
            arguments = listOf(
                navArgument(CREDIT_STEP) { type = NavType.IntType },
                navArgument(ID_USER_REQUEST) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            CreditScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.SignDocumentProcess.route
        ) {
            SignDocumentProcessScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
}
