package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.PROFILE_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.CardVDNavType
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileScreen
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardsListScreen
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreen
import com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.TermsAndConditionsScreen
import com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.detail.TermsAndConditionsDetailsScreen
import com.multimoney.multimoney.presentation.ui.home.profile.cards.editcard.EditCardScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.PersonalInfoScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.email.ChangeEmailScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone.ChangePhoneScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp.ValidateOTPScreen
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity.VerifyIdentityScreen
import com.multimoney.multimoney.presentation.ui.home.profile.settings.SettingsScreen
import com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword.ChangePasswordScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.ProfileScreen.route,
        route = PROFILE_ROUTE
    ) {
        composable(
            Screen.ProfileScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            ProfileScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            PersonalInfoScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            ChangeEmailScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            ChangePhoneScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            VerifyIdentityScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            VerifyIdentityScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            ValidateOTPScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileTermsAndConditionsScreen.route,
            arguments = listOf(navArgument(ID_BRAND) {
                type = NavType.IntType
            })
        ) {
            TermsAndConditionsScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileTermsAndConditionsDetailScreen.route
        ) {
            TermsAndConditionsDetailsScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileSettingsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            SettingsScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileChangePasswordScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            ChangePasswordScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileHelpScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            HelpScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
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
            Screen.ProfileCardListScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            val viewModel = hiltViewModel<ProfileCardListViewModel>()
            ProfileCardsListScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                viewModel = viewModel
            )
        }

        composable(
            route =  Screen.ProfileMyCardsEditCardScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(CARD_SELECTED) {
                    type = CardVDNavType()
                }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ProfileCardListScreen.route)
            }
            val viewModel = hiltViewModel<ProfileCardListViewModel>(parentEntry)
            EditCardScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                sharedViewModel = viewModel
            )
        }
    }
}
