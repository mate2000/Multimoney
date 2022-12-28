package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.AMOUNT_ORIGINAL_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.BANK_DETAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_CARD_VISA_DIRECT
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENT_AMOUNT_VALUE
import com.multimoney.multimoney.presentation.navigation.navgraph.DISBURSEMENT_LABEL
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
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_SMART_EVICERTIA
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.MASKED_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NEXT_PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_AND_EVICERTIA_ERROR
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT_LABEL
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SMART_PAYMENT_ACCOUNTS
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT

// Route
const val LOGIN_ROUTE = "log_in_route"
const val HOME_ROUTE = "home_route"
const val CREDIT_ROUTE = "credit_route"
const val DISBURSEMENT_ROUTE = "disbursement_route"
const val VISA_ROUTE = "visa_route"
const val PAYMENT_CREDIT_ROUTE = "payment_route"
const val SMART_ROUTE = "smart_route"
const val CRYPTO_ROUTE = "crypto_route"
const val PROFILE_ROUTE = "profile_route"
const val TEST_ROUTE = "test_route"
const val SMART_PAYMENT_ROUTE = "smart_payment_route"
const val ID_BRAND = "id_brand"
const val PHONE_NUMBER = "phone_number"
const val NEW_PHONE_NUMBER = "new_phone_number"
const val EMAIL = "email"
const val USER_NAME = "user_name"
const val HTML = "html"
const val UTF8 = "UTF-8"
const val TITLE = "title"
const val SEND_METHOD = "send_method"
const val NEW_EMAIL = "new_email"
const val CHANGING_FIELD = "changing_field"
const val NEW_VALUE = "new_value"
const val DATE_SIGNED = "date_signed"
const val VERSION = "version"
const val ID_VISA_CARD = "id_visa_card"
const val SMART_IDS_LIST = "smart_id_list"
const val SMART_IDS = "smart_ids"
const val IBAN_ACCOUNT = "iban_account"
const val GLOBAL_CRYPTO_BALANCE = "global_crypto_balance"

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

    object ProfileScreen : Screen(
        "profile_screen/{$ID_CLIENT}/{$ID_BRAND}/{$FIRST_NAME}/{$EMAIL}/{$PHONE_NUMBER}/{$IDENTIFICATION}/{$PK_USER}/{$USER_NAME}",
        "profile_screen"
    )

    object ProfileTermsAndConditionsScreen : Screen(
        "profile_terms_and_conditions_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$PK_USER}/{$USER_NAME}",
        "profile_terms_and_conditions_screen"
    )

    object ProfileTermsAndConditionsDetailScreen : Screen(
        "profile_terms_and_conditions_detail_screen/{$TITLE}/{$HTML}/{$VERSION}/{$DATE_SIGNED}",
        "profile_terms_and_conditions_detail_screen"
    )

    object ProfilePersonalInfoScreen : Screen(
        "profile_personal_info_screen/{$ID_CLIENT}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}",
        "profile_personal_info_screen"
    )

    object ProfileVerifyIdentityPhoneScreen : Screen(
        "profile_verify_identity_phone_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$NEW_PHONE_NUMBER}/{$EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}",
        "profile_verify_identity_phone_screen"
    )

    object ProfileVerifyIdentityEmailScreen : Screen(
        "profile_verify_identity_email_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$EMAIL}/{$NEW_EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}",
        "profile_verify_identity_email_screen"
    )

    object ProfileChangePhoneScreen : Screen(
        "profile_change_phone_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}",
        "profile_change_phone_screen"
    )

    object ProfileChangeEmailScreen : Screen(
        "profile_change_email_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}",
        "profile_change_email_screen"
    )

    object ProfileValidateOTPScreen : Screen(
        "profile_validate_otp_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$NEW_VALUE}/{$SEND_METHOD}/{$IDENTIFICATION}/{$FIRST_NAME}/{$EMAIL}/{$PHONE_NUMBER}/{$PK_USER}/{$ID_BRAND}/{$USER}",
        "profile_validate_otp_screen"
    )

    // Profile Sub-Screens
    object ProfileSettingsScreen : Screen(
        "profile_settings_screen/{$ID_BRAND}/{$PK_USER}/{$USER_NAME}",
        "profile_settings_screen"
    )

    object ProfileHelpScreen : Screen(
        "profile_help_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$PK_USER}/{$USER_NAME}",
        "profile_help_screen"
    )

    object ProfileChangePasswordScreen : Screen(
        "profile_change_password_screen/{$ID_BRAND}/{$PK_USER}/{$USER_NAME}",
        "profile_change_password_screen"
    )

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

    object DisbursementVoucherScreen :
        Screen(
            route = "disbursement_voucher_screen/{$CLIENT_BANK_ACCOUNT}/{$DISBURSEMENT_LABEL}/{$EXCHANGE_RATE_LABEL}/{$SHOULD_DISPLAY_EXCHANGE_RATE}/{$REFERENCE_NUMBER}/{$AMOUNT_ORIGINAL_LABEL}",
            "disbursement_voucher_screen"
        )

    // CreditNavGraph Screens
    object CreditScreen : Screen(
        "credit_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}/{$EVICERTIA_STATUS}/{$SIGN_DOCUMENT_ID_PRINT}",
        "credit_screen"
    )

    // Non Pre-Approved Screen
    object NonPreApprovedScreen : Screen(
        "non_pre_approved_screen/{$PK_USER}/{$EMAIL}/{$ID_BRAND}/{$ID_USER_REQUEST}",
        "non_pre_approved_screen"
    )

    object CreditMovementsScreen : Screen(
        "credit_movements_screen/{$ID_BRAND}/{$ID_LOAN_CLIENT}/{$USER}/{$CREDIT_NUMBER}",
        "credit_movements_screen"
    )

    object CreditOnfidoScreen : Screen(
        "credit_onfido_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$SIGN_DOCUMENT_URL}/{$EVICERTIA_STATUS}",
        "credit_onfido_screen"
    )

    object SignDocumentProcessScreen : Screen(
        "sign_document_process_screen/{$SIGN_DOCUMENT_STEP_ARG}/{$SIGN_DOCUMENT_URL}/{$SIGN_DOCUMENT_ID_PRINT}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$IS_SMART_EVICERTIA}",
        "sign_document_process_screen"
    )

    object SmartSignScreen : Screen(
        "sign_document_process_screen/{$SIGN_DOCUMENT_STEP_ARG}/{$SIGN_DOCUMENT_URL}/{$SIGN_DOCUMENT_ID_PRINT}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$IS_SMART_EVICERTIA}",
        "sign_document_process_screen"
    )

    object SmartOnfidoAndEvicertiaErrorsScreen : Screen(
        "smart_onfido_and_evicertia_errors_screen/{$ONFIDO_AND_EVICERTIA_ERROR}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}",
        "smart_onfido_and_evicertia_errors_screen"
    )

    object ContinueValidatingOnfidoScreen : Screen("continue_validating_onfido_screen")

    object SmartContinueValidatingOnfidoScreen : Screen("smart_continue_validating_onfido_screen")

    object ProcessingTransactionScreen : Screen(
        "processing_transaction_screen/{$ID_BRAND}/{$SIGN_DOCUMENT_ID_PRINT}/{$USER}",
        "processing_transaction_screen"
    )

    object ApprovedByOnfidoScreen : Screen(
        "approved_by_onfido_screen/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_BRAND}",
        "approved_by_onfido_screen"
    )

    object OnfidoAndEvicertiaErrorsScreen : Screen(
        "onfido_and_evicertia_errors_screen/{$ONFIDO_AND_EVICERTIA_ERROR}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}",
        "onfido_and_evicertia_errors_screen"
    )

    // VisaNavGraph
    object VisaIssuanceScreen : Screen(
        "visa_issuance_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}",
        "visa_issuance_screen"
    )

    object VisaCardScreen : Screen(
        "visa_card_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}",
        "visa_card_screen"
    )

    object VisaTokenizationWaitingScreen : Screen(
        "visa_tokenization_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}",
        "visa_tokenization_screen"
    )

    object VisaPreferencesScreen : Screen(
        "visa_preferences_screen",
        "visa_preferences_screen"
    )

    // Bottom Navigation
    object HomeBNScreen : Screen("home_bn_screen")
    object QuickActionBNScreen : Screen("quick_action_bt_screen")
    object ProductsBNScreen : Screen("products_bt_screen")

    // Add Iban Account
    object AddIbanAccountScreen : Screen(
        "add_iban_account_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "add_iban_account_screen"
    )

    // Transfer Iban Account
    object TransferIbanAccountScreen : Screen(
        "transfer_iban_account_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "transfer_iban_account_screen"
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

    object PaymentScheduleCardScreen : Screen(
        "payment_schedule_card_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$CLIENT_CARD_VISA_DIRECT}/{$PAYMENT_DATE}/{$IS_EDIT_BANK_ACCOUNT}/{$PREVIOUS_SCREEN}/{$IS_EDIT_PAYMENT_SCHEDULE}/{$IDENTIFICATION}",
        "payment_schedule_card_screen"
    )

    object PaymentScheduleCardListScreen : Screen(
        "payment_schedule_account_screen/{$USER}/{$ID_BRAND}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$PAYMENT_DATE}/{$PREVIOUS_SCREEN}/{$IDENTIFICATION}",
        "payment_schedule_account_screen"
    )

    object PaymentOptionsScreen : Screen(
        "payment_options_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$PAYMENT_METHOD}/{$TRANSFER_ACCOUNT}/{$MINIMUM_PAYMENT}/{$MINIMUM_PAYMENT_LABEL}/{$MAXIMUM_PAYMENT}/{$MAXIMUM_PAYMENT_LABEL}/{$IDENTIFICATION}/{$USER}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$ID_CURRENCY}/{$PAYMENT_DATE}",
        "payment_options_screen"
    )

    object PaymentOptionsTransferScreen : Screen(
        "payment_options_transfer_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$TRANSFER_ACCOUNT}",
        "payment_options_transfer_screen"
    )

    object PaymentPointsScreen : Screen(
        "payment_points_screen/{$ID_BRAND}/{$CREDIT_NUMBER}/{$PAYMENT_AMOUNT_LABEL}",
        "payment_points_screen"
    )

    object PaymentCardsListScreen : Screen(
        "payment_cards_list_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}/{$CREDIT_NUMBER}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$MINIMUM_PAYMENT}/{$MINIMUM_PAYMENT_LABEL}/{$MAXIMUM_PAYMENT}/{$MAXIMUM_PAYMENT_LABEL}/{$ID_CURRENCY}/{$PAYMENT_DATE}",
        "payment_cards_list_screen"
    )

    object PaymentAmountCardsScreen : Screen(
        "payment_amount_card_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}/{$CARD_SELECTED}/{$CREDIT_NUMBER}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$MINIMUM_PAYMENT}/{$MINIMUM_PAYMENT_LABEL}/{$MAXIMUM_PAYMENT}/{$MAXIMUM_PAYMENT_LABEL}/{$ID_CURRENCY}/{$PAYMENT_DATE}",
        "payment_amount_card_screen"
    )

    object PaymentLocationDetailsScreen : Screen(
        "payment_location_details_screen?$POINT_NAME={$POINT_NAME}?$POINT_ADDRESS={$POINT_ADDRESS}?$POINT_ADDRESS_DESCRIPTION={$POINT_ADDRESS_DESCRIPTION}?$POINT_SCHEDULE={$POINT_SCHEDULE}?$POINT_LATITUDE={$POINT_LATITUDE}?$POINT_LONGITUDE={$POINT_LONGITUDE}?$PAYMENT_AMOUNT={$PAYMENT_AMOUNT}?$CREDIT_NUMBER={$CREDIT_NUMBER}?$ID_BRAND={$ID_BRAND}",
        "payment_location_details_screen"
    )

    object PaymentCardVoucherScreen : Screen(
        "payment_voucher_vd_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$CARD_SELECTED}/{$CURRENT_AMOUNT_VALUE}/{$IS_AUTOMATIC_PAYMENT_CHECKED}/{$REFERENCE_NUMBER}/{$PAYMENT_DATE}",
        "payment_voucher_vd_screen"
    )

    // Smart
    object SmartScreen : Screen(
        "smart_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}/{$COMING_FROM_CRYPTO}",
        "smart_screen"
    )

    object SmartOnfidoScreen : Screen(
        "smart_onfido_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$SIGN_DOCUMENT_URL}/{$COMING_FROM_CRYPTO}",
        "smart_onfido_screen"
    )

    object SmartMovementsScreen : Screen(
        "smart_movements_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ACCOUNT_TOKEN}",
        "smart_movements_screen"
    )

    // Smart Payment

    object SmartPaymentOptionsScreenCR : Screen(
        "smart_payment_options_screen/{$SMART_IDS_LIST}/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "smart_payment_options_screen"
    )

    object SmartPaymentAccountScreenCR : Screen(
        "smart_payment_accounts_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SMART_PAYMENT_ACCOUNTS}/{$SMART_IDS}",
        "smart_payment_accounts_screen"
    )

    object SmartPaymentMethodScreenSV : Screen(
        "smart_payment_method_screen/{$SMART_IDS}",
        "smart_payment_method_screen"
    )

    object SmartPaymentCardsScreenSV : Screen(
        "smart_payment_cards_screen/{$SMART_IDS}",
        "smart_payment_cards_screen"
    )

    object SavingMethodTransferScreen : Screen(
        route = "saving_method_transfer_screen/{$USER_SMART_ACCOUNT}",
        baseRoute = "saving_method_transfer_screen"
    )

    object SmartPaymentSavingAmount : Screen(
        "smart_payment_saving_amount_screen/{$SMART_IDS}?$IBAN_ACCOUNT={$IBAN_ACCOUNT}/{$ID_VISA_CARD}/{$PREVIOUS_SCREEN}/{$MASKED_CARD}/{$BANK_DETAIL}",
        "smart_payment_saving_amount_screen"
    )

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen/{$}")
    object SubscriptionScreen : Screen("subscription_screen")

    // Crypto
    object CryptoWalletScreen : Screen(
        "crypto_wallet_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$GLOBAL_CRYPTO_BALANCE}",
        "crypto_wallet_screen"
    )
}
