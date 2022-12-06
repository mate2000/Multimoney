package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENT_AMOUNT_VALUE
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.EXCHANGE_RATE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.FK_FLOW_CONTROL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_AUTOMATIC_PAYMENT_CHECKED
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_PAYMENT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_MULTI_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NEXT_PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_AND_EVICERTIA_ERROR
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_METHOD
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_ADDRESS
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_ADDRESS_DESCRIPTION
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_LATITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_LONGITUDE
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.QUOTA_TOTAL
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.SELECTED_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_DISPLAY_EXCHANGE_RATE
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER

// Route
const val LOGIN_ROUTE = "log_in_route"
const val HOME_ROUTE = "home_route"
const val CREDIT_ROUTE = "credit_route"
const val DISBURSEMENT_ROUTE = "disbursement_route"
const val VISA_ROUTE = "visa_route"
const val PAYMENT_CREDIT_ROUTE = "payment_route"
const val SMART_ROUTE = "smart_route"
const val PROFILE_ROUTE = "profile_route"
const val TEST_ROUTE = "test_route"
const val PAYMENT_SMART_ROUTE = "payment_smart_route"

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

    object ProfileScreen : Screen("profile_screen/{$ID_BRAND}", "profile_screen")

    // DisbursementNavGraph Screens
    object DisbursementAmountScreen : Screen(
        "disbursement_amount_screen/{$ID_BRAND}/{$USER}/{$ID_CLIENT}/{$SUMMARY_LIST}/{$PK_USER}/{$CREDIT_NUMBER}/{$ID_USER_REQUEST}/{$IDENTIFICATION}",
        "disbursement_amount_screen"
    )

    object DisbursementAccountScreen : Screen(
        "disbursement_account_screen/{$ID_BRAND}/{$USER}/{$ID_CLIENT}/{$NEXT_PAYMENT_DATE}/{$QUOTA_TOTAL}/{$SELECTED_AMOUNT}/{$PK_USER}/{$ID_USER_REQUEST}/{$IDENTIFICATION}/{$CREDIT_NUMBER}/{$FK_FLOW_CONTROL}/{$ID_CURRENCY}/{$ID_LOAN_CLIENT}",
        "disbursement_account_screen"
    )

    object DisbursementAddAccountScreen : Screen(
        "disbursement_add_account_screen/{$ID_BRAND}/{$PK_USER}/{$EMAIL}/{$ID_USER_REQUEST}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$ID_CURRENCY}",
        "disbursement_add_account_screen"
    )

    // CreditNavGraph Screens
    object CreditScreen : Screen(
        "credit_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}/{$EVICERTIA_STATUS}/{$SIGN_DOCUMENT_ID_PRINT}",
        "credit_screen"
    )

    object CreditOnfidoScreen : Screen(
        "credit_onfido_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$SIGN_DOCUMENT_URL}/{$EVICERTIA_STATUS}",
        "credit_onfido_screen"
    )

    object SignDocumentProcessScreen : Screen(
        "sign_document_process_screen/{$SIGN_DOCUMENT_STEP_ARG}/{$SIGN_DOCUMENT_URL}/{$SIGN_DOCUMENT_ID_PRINT}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}",
        "sign_document_process_screen"
    )

    object ContinueValidatingOnfidoScreen : Screen("continue_validating_onfido_screen")

    object ProcessingTransactionScreen : Screen(
        "processing_transaction_screen/{$ID_BRAND}/{$SIGN_DOCUMENT_ID_PRINT}/{$USER}",
        "processing_transaction_screen"
    )

    object OnfidoAndEvicertiaErrorsScreen : Screen(
        "onfido_and_evicertia_errors_screen/{$ONFIDO_AND_EVICERTIA_ERROR}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}",
        "onfido_and_evicertia_errors_screen"
    )

    // VisaNavGraph
    object VisaIssuanceScreen : Screen("visa_issuance_screen/{$ID_BRAND}", "visa_issuance_screen")
    object VisaCardScreen : Screen("visa_card_screen/{$ID_BRAND}", "visa_card_screen")

    // Bottom Navigation
    object HomeBNScreen : Screen("home_bn_screen")
    object QuickActionBNScreen : Screen("quick_action_bt_screen")
    object ProductsBNScreen : Screen("products_bt_screen")

    // Add Iban Account
    object AddIbanAccountScreen : Screen(
        "add_iban_account_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "add_iban_account_screen"
    )

    // Payment Credit
    object PaymentFeeScreen : Screen(
        "payment_fee_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$IDENTIFICATION}/{$NAME_CLIENT}/{$PAYMENT_DATE}",
        "payment_fee_screen"
    )

    object PaymentAccountScreen : Screen(
        "payment_account_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$IDENTIFICATION}/{$NAME_CLIENT}/{$PAYMENT_DATE}/{$PREVIOUS_SCREEN}",
        "payment_account_screen"
    )

    object PaymentAmountScreen : Screen(
        "payment_amount_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SUMMARY_LIST}/{$CLIENT_BANK_ACCOUNT}/{$IDENTIFICATION}/{$NAME_CLIENT}/{$PAYMENT_DATE}",
        "payment_amount_screen"
    )

    object PaymentVoucherScreen :
        Screen(
            route = "payment_voucher_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$CLIENT_BANK_ACCOUNT}/{$PAYMENT_DATE}/{$CURRENT_AMOUNT_VALUE}/{$PAYMENT_LABEL}/{$EXCHANGE_RATE_LABEL}/{$SHOULD_DISPLAY_EXCHANGE_RATE}/{$IS_MULTI_CURRENCY}/{$IS_AUTOMATIC_PAYMENT_CHECKED}/{$REFERENCE_NUMBER}",
            "payment_voucher_screen"
        )

    object PaymentScheduleScreen : Screen(
        "payment_schedule_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$CLIENT_BANK_ACCOUNT}/{$PAYMENT_DATE}/{$IS_EDIT_BANK_ACCOUNT}/{$PREVIOUS_SCREEN}/{$IS_EDIT_PAYMENT_SCHEDULE}",
        "payment_schedule_screen"
    )

    object PaymentScheduleAccountScreen : Screen(
        "payment_schedule_account_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$PAYMENT_DATE}/{$PREVIOUS_SCREEN}",
        "payment_schedule_account_screen"
    )

    object PaymentOptionsScreen : Screen(
        "payment_options_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$PAYMENT_METHOD}/{$TRANSFER_ACCOUNT}/{$PAYMENT_AMOUNT}/{$IDENTIFICATION}/{$USER}",
        "payment_options_screen"
    )

    object PaymentOptionsTransferScreen : Screen(
        "payment_options_transfer_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$TRANSFER_ACCOUNT}",
        "payment_options_transfer_screen"
    )

    object PaymentPointsScreen : Screen(
        "payment_points_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$PAYMENT_AMOUNT}",
        "payment_points_screen"
    )

    object PaymentCardsListScreen : Screen(
        "payment_cards_list_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}",
        "payment_cards_list_screen"
    )

    object PaymentAmountCardsScreen : Screen(
        "payment_amount_cards_list_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}/{$CARD_SELECTED}",
        "payment_amount_cards_list_screen"
    )

    object PaymentLocationDetailsScreen : Screen(
        "payment_location_details_screen/{$POINT_NAME}/{$POINT_ADDRESS}/{$POINT_ADDRESS_DESCRIPTION}/{$POINT_SCHEDULE}/{$POINT_LATITUDE}/{$POINT_LONGITUDE}/{$PAYMENT_AMOUNT}/{$CREDIT_NUMBER}/{$ID_BRAND}",
        "payment_location_details_screen"
    )

    // Smart
    object SmartScreen : Screen(
        "smart_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}",
        "smart_screen"
    )

    object SmartOnfidoScreen : Screen(
        "smart_onfido_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$SIGN_DOCUMENT_URL}",
        "smart_onfido_screen"
    )

    object SmartMovementsScreen : Screen(
        "smart_movements_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ACCOUNT_TOKEN}",
        "smart_movements_screen"
    )

    // Payment Smart
    object PaymentSmartCardsScreen : Screen(
        "payment_smart_cards_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}",
        "payment_smart_cards_screen"
    )

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen/{$}")
    object SubscriptionScreen : Screen("subscription_screen")
}
