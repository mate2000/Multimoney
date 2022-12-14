package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.PROFILE_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.PersonalInfoScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.email.ChangeEmailScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone.ChangePhoneScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp.ValidateOTPScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity.VerifyIdentityScreen
import com.multimoney.multimoney.presentation.ui.home.profile.settings.SettingsScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.ProfileScreen.route,
        route = PROFILE_ROUTE
    ) {
        composable(
            Screen.ProfileScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            ProfileScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
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
            Screen.ProfilePersonalInfoScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            PersonalInfoScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }

        composable(
            Screen.ProfileChangeEmailScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            ChangeEmailScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }

        composable(
            Screen.ProfileChangePhoneScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            ChangePhoneScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }

        composable(
            Screen.ProfileVerifyIdentityPhoneScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            VerifyIdentityScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(
            Screen.ProfileVerifyIdentityEmailScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            VerifyIdentityScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(
            Screen.ProfileValidateOTPScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            }, navArgument(ID_CLIENT) {
                type = NavType.IntType
            })
        ) {
            ValidateOTPScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
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
            Screen.ProfileSettingsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            SettingsScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                },
            )
        }
    }
}
