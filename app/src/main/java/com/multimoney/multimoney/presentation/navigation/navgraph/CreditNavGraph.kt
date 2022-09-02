package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.CreditScreen
import com.multimoney.multimoney.presentation.ui.credit.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.signdocument.SignDocumentScreen

const val SIGN_DOCUMENT_LINK = "sign_document_link"
const val ID_BRAND = "id_brand"
const val PK_USER = "pk_user"
const val IDENTIFICATION = "identification"
const val EMAIL = "email"

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
    }
}