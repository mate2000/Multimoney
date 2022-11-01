package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_METHOD
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_LINK
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER

// Route
const val LOGIN_ROUTE = "log_in_route"
const val HOME_ROUTE = "home_route"
const val CREDIT_ROUTE = "credit_route"
const val VISA_ROUTE = "visa_route"
const val PAYMENT_CREDIT_ROUTE = "payment_route"
const val SMART_ROUTE = "smart_route"
const val TEST_ROUTE = "test_route"

const val ID_BRAND = "id_brand"

// Previous
const val PREVIOUS_IS_RESTART = "previous_is_restart"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String, val baseRoute: String = "") {

    // LoginNavGraph Screens
    object SplashScreen : Screen("splash_screen")
    object OnBoardingScreen : Screen("onboarding_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen/{$SIGN_UP_STEP}", "sign_up_screen")

    object SignUpSplashComeBackScreen : Screen(
        "sign_up_splash_come_back_screen/{$SIGN_UP_STEP}",
        "sign_up_splash_come_back_screen"
    )

    object SignUpCompleted : Screen("sign_up_completed")

    // HomeNavGraph Screens
    object HomeScreen : Screen("home_screen")

    // CreditNavGraph Screens
    object CreditScreen : Screen(
        "credit_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$ID_USER_REQUEST}",
        "credit_screen"
    )

    // VisaNavGraph
    object VisaIssuanceScreen : Screen("visa_issuance_screen/{$ID_BRAND}", "visa_issuance_screen")
    object VisaCardScreen : Screen("visa_card_screen/{$ID_BRAND}", "visa_card_screen")

    // HomeNavGraph Screens
    object DocumentGenerationScreen : Screen("document_generation_screen")
    object SignDocumentScreen :
        Screen("sign_document_screen/{$SIGN_DOCUMENT_LINK}", "sign_document_screen")

    // Bottom Navigation
    object HomeBNScreen : Screen("home_bn_screen")
    object QuickActionBNScreen : Screen("quick_action_bt_screen")
    object ProductsBNScreen : Screen("products_bt_screen")

    // Payment Credit
    object PaymentFeeScreen : Screen(
        "payment_fee_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$IDENTIFICATION}/{$NAME_CLIENT}",
        "payment_fee_screen"
    )

    object PaymentAccountScreen :
        Screen(
            "payment_account_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$IDENTIFICATION}/{$NAME_CLIENT}",
            "payment_account_screen"
        )

    object PaymentAmountScreen : Screen(
        "payment_amount_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$CLIENT_BANK_ACCOUNT}/{$IDENTIFICATION}/{$NAME_CLIENT}",
        "payment_amount_screen"
    )

    // Smart
    object SmartScreen :
        Screen(
            "smart_screen/{$USER}/{$ID_BRAND}/{$PK_USER}",
            "smart_screen"
        )
    object PaymentVoucherScreen : Screen(route = "payment_voucher_screen")

    object PaymentOptionsScreen : Screen(
        "payment_options_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$PAYMENT_METHOD}/{$TRANSFER_ACCOUNT}",
        "payment_options_screen"
    )

    object PaymentOptionsTransferScreen : Screen(
        "payment_options_transfer_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$TRANSFER_ACCOUNT}",
        "payment_options_transfer_screen"
    )

    object PaymentPointsScreen : Screen(
        "payment_points_screen/{$ID_BRAND}",
        "payment_points_screen"
    )

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen/{$}")
}
