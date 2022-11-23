package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addnewaccount.AddNewAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addnewaccountsuccess.AddNewAccountSuccessScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentScreen

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
        composable(route = Screen.DocumentGenerationScreen.route) {
            DocumentGenerationScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignDocumentScreen.route) {
            SignDocumentScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        // Disburse Add New Account
        composable(route = Screen.AddNewAccountScreen.route) {
            AddNewAccountScreen(
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
    }
}
