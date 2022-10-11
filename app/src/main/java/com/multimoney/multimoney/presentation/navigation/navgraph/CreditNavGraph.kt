package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_BUTTON_TEXT
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_DESCRIPTION
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_ICON
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_TITLE
import com.multimoney.multimoney.presentation.navigation.CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.alertresult.AlertResultScreen
import com.multimoney.multimoney.presentation.ui.credit.CreditScreen
import com.multimoney.multimoney.presentation.ui.credit.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.howmuchyouwantpay.HowMuchYouWantPayScreen
import com.multimoney.multimoney.presentation.ui.credit.signdocument.SignDocumentScreen

const val SIGN_DOCUMENT_LINK = "sign_document_link"
const val PK_USER = "pk_user"
const val IDENTIFICATION = "identification"
const val EMAIL = "email"
const val CREDIT_STEP = "credit_step"
const val ID_USER_REQUEST = "id_user_request"

fun NavGraphBuilder.creditNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.CreditScreen.route,
        route = CREDIT_ROUTE
    ) {
        composable(route = Screen.CreditScreen.route) { navBackStackEntry ->
            CreditScreen(
                navBackStackEntry = navBackStackEntry,
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
        composable(route = Screen.DocumentGenerationScreen.route) {
            DocumentGenerationScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignDocumentScreen.route) { navBackStackEntry ->
            SignDocumentScreen(
                navBackStackEntry = navBackStackEntry,
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.AlertResultScreen.route,
            arguments = listOf(
                navArgument(ALERT_RESULT_ICON) { type = NavType.IntType },
                navArgument(ALERT_RESULT_TITLE) { type = NavType.IntType },
                navArgument(ALERT_RESULT_DESCRIPTION) { type = NavType.IntType },
                navArgument(ALERT_RESULT_BUTTON_TEXT) { type = NavType.IntType })
        ) { navBackStackEntry ->
            AlertResultScreen(
                navBackStackEntry = navBackStackEntry,
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.HowMuchYouWantPayScreen.route) { navBackStackEntry ->
            HowMuchYouWantPayScreen(
                navBackStackEntry = navBackStackEntry,
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
}