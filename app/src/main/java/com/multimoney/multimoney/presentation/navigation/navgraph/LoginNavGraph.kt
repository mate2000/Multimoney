package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.FORCE_CHANGE_DEVICE
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navtype.login.UserDataNavType
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailScreen
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpScreen
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsScreen
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInOTPScreen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInScreen
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.splash.DEFAULT_STEP
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBack
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreen

const val SIGN_UP_STEP = "sign_up_step"

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SplashScreen.route,
        route = LOGIN_ROUTE
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.OnBoardingScreen.route) {
            OnBoardingScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.SignInScreen.route,
            arguments = listOf(
                navArgument(FORCE_CHANGE_DEVICE) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val forceChangeDevice = it.arguments?.getBoolean(FORCE_CHANGE_DEVICE) ?: false

            SignInScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                onNavigate = {
                    navController.navigate(it.route)
                },
                forceChangeDevice = forceChangeDevice
            )
        }
        composable(
            route = Screen.SignUpScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            SignUpScreen(
                // Workaround to solve compose issue when launchSingleTop is combine with arguments
                // (Use: navController.currentBackStackEntry ?: navBackStackEntry)

                isRestart = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                    PREVIOUS_IS_RESTART
                )?.observeAsState()?.value ?: true,
                step = navController.currentBackStackEntry?.arguments?.getString(
                    SIGN_UP_STEP,
                    DEFAULT_STEP
                )
                    ?: DEFAULT_STEP,
                idBrand = navController.currentBackStackEntry?.arguments?.getInt(ID_BRAND,0),
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.SignUpSplashComeBackScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            SignUpSplashComeBack(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignUpCompleted.route) {
            SignUpCompleted(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignInOTPScreen.route) {
            SignInOTPScreen(
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
        composable(route = Screen.RequestForgotPassword.route) {
            RequestForgotPasswordScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
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
                }
            )
        }
        composable(
            route = Screen.ProcessForgotPassword.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            ProcessForgotPasswordScreen(
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
                }
            )
        }

        composable(
            route = Screen.RegisteredUserEmailScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(USER_DATA) { type = UserDataNavType() }
            )
        ) {
            RegisteredUserEmailScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                        HOME_STATE,
                        it.homeState
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
        composable(
            Screen.RegisteredUserOtpOptionsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(USER_DATA) { type = UserDataNavType() }
            )
        ) {
            RegisteredUserOtpOptionsScreen(
                onNavigate = { navController.navigate(it.route) },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
        composable(
            Screen.RegisteredUserOtpScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(USER_DATA) { type = UserDataNavType() }
            )
        ) {
            RegisteredUserOtpScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                        HOME_STATE,
                        it.homeState
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
        composable(
            route = Screen.RegisteredUserPassword.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(USER_DATA) { type = UserDataNavType() }
            )
        ) {
            RegisteredUserPasswordScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    launchSingleTop = true
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
    }
}
