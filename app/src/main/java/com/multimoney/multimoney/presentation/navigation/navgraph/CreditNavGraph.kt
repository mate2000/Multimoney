package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido.ContinueValidatingOnfidoScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess.ProcessingTransactionScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessScreen

const val PK_USER = "pk_user"
const val IDENTIFICATION = "identification"
const val EMAIL = "email"
const val CREDIT_STEP = "credit_step"
const val ID_USER_REQUEST = "id_user_request"
const val FIRST_NAME = "name"
const val LAST_NAME = "last_name"
const val ONFIDO_STATUS = "onfido_status"
const val EVICERTIA_STATUS = "evicertia_status"
const val SIGN_DOCUMENT_STEP_ARG = "sign_document_step_arg"
const val SIGN_DOCUMENT_URL = "sign_document_url"
const val SIGN_DOCUMENT_ID_PRINT = "sign_document_id_print"
const val ONFIDO_AND_EVICERTIA_ERROR = "onfifo_and_evicertia_error"
const val ID_CURRENCY = "currency"

fun NavGraphBuilder.creditNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.CreditScreen.route,
        route = CREDIT_ROUTE
    ) {
        composable(
            route = Screen.CreditScreen.route,
            arguments = listOf(
                navArgument(CREDIT_STEP) { type = NavType.IntType },
                navArgument(ID_USER_REQUEST) { type = NavType.IntType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType }
            )
        ) { navBackStackEntry ->
            CreditScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                })
        }

        composable(
            Screen.CreditOnfidoScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(ID_USER_REQUEST) { type = NavType.LongType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType },
            )
        ) {
            CreditOnfidoScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(
            route = Screen.SignDocumentProcessScreen.route,
            arguments = listOf(
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_USER_REQUEST) { type = NavType.LongType },
                navArgument(PK_USER) { type = NavType.LongType },
            )
        ) {
            SignDocumentProcessScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(
            route = Screen.ContinueValidatingOnfidoScreen.route
        ) {
            ContinueValidatingOnfidoScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(
            route = Screen.ProcessingTransactionScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType }
            )
        ) {
            ProcessingTransactionScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(
            route = Screen.OnfidoAndEvicertiaErrorsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(ID_USER_REQUEST) { type = NavType.LongType },
            )
        ) {
            OnfidoAndEvicertiaErrorsScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
}
