package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.AMOUNT_ORIGINAL_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
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
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_GLOBAL_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_AUTOMATIC_PAYMENT_CHECKED
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_PAYMENT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_MULTI_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_SMART_EVICERTIA
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
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
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.POINT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.QUOTA_TOTAL
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.SELECTED_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_DISPLAY_EXCHANGE_RATE
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_GLOBAL_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ORIGIN
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.SMART_ACCOUNT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.SMART_PAYMENT_ACCOUNTS
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
const val CRYPTO_ROUTE = "crypto_route"
const val PROFILE_ROUTE = "profile_route"
const val TEST_ROUTE = "test_route"
const val SMART_PAYMENT_ROUTE = "smart_payment_route"
const val SMART_TRANSFER_ROUTE = "smart_transfer_route"
const val ID_BRAND = "id_brand"
const val PHONE_NUMBER = "phone_number"
const val NEW_PHONE_NUMBER = "new_phone_number"
const val PHONE_NUMBER_CODE = "phone_number_code"
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
const val TRANSFER_TYPE = "transfer_type"
const val ORIGIN_ACCOUNT = "origin_account"
const val DESTINY_ACCOUNT = "destiny_account"
const val SMART_ACCOUNT_LIST = "smart_id_list"
const val SMART_ACCOUNT = "smart_account"
const val SECOND_SMART_ACCOUNT = "second_smart_account"
const val IBAN_ACCOUNT = "iban_account"
const val GLOBAL_CRYPTO_BALANCE = "global_crypto_balance"
const val PASSWORD = "password"
const val DEVICE_ID = "device_id"
const val UNIQUE_ID = "unique_id"
const val IP_ADDRESS = "ip_address"
const val DEVICE_TYPE = "device_type"
const val DEVICE_NAME = "device_name"
const val APP_VERSION = "app_version"
const val DEVICE_BRAND = "device_brand"
const val DEVICE_MODEL = "device_model"
const val IS_EMULATOR = "is_emulator"
const val FORCE_CHANGE_DEVICE = "force_change_device"
const val ID_CLIENT = "id_client"
const val ID_LOAN_CLIENT = "id_loan_client"
const val STATUS_CREDIT = "status_credit"
const val STATUS_SMART = "status_smart"
const val STATUS_CRYPTO = "status_crypto"
const val CARD_STATUS = "card_status"
const val CRYPTO_ASSET = "asset"
const val DESCRIPTION_CURRENCY = "description_currency"
const val CURRENT_CRYPTO_PRICE = "current_crypto_price"
const val URL_IMAGE = "url_image"
const val USER_DATA = "user_data"
const val PROFILE_CARD_LIST_ORIGIN = "profile_card_list_origin"

// Previous
const val PREVIOUS_IS_RESTART = "previous_is_restart"
const val HOME_STATE = "home_state"
const val PREVIOUS_IS_RETURN_TO_HOME = "previous_is_return_to_home"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String, val baseRoute: String = "") {

    // LoginNavGraph Screens
    object SplashScreen : Screen("splash_screen")
    object OnBoardingScreen : Screen("onboarding_screen")
    object SignInScreen : Screen("sign_in_screen?$FORCE_CHANGE_DEVICE={$FORCE_CHANGE_DEVICE}", "sign_in_screen")

    object SignUpScreen : Screen("sign_up_screen/{$SIGN_UP_STEP}", "sign_up_screen")
    object RequestForgotPassword : Screen(
        "request_forgot_password_screen?$PREVIOUS_SCREEN={$PREVIOUS_SCREEN}",
        "request_forgot_password_screen"
    )

    object RegisteredUserEmailScreen : Screen(
        "registered_user_email_screen?$ID_BRAND={$ID_BRAND}?$USER_DATA={$USER_DATA}",
        "registered_user_email_screen"
    )

    object RegisteredUserOtpScreen : Screen(
        "registered_user_otp_screen?$PREVIOUS_SCREEN={$PREVIOUS_SCREEN}?$ID_BRAND={$ID_BRAND}?$USER_DATA={$USER_DATA}",
        "registered_user_otp_screen"
    )

    object RegisteredUserPassword : Screen(
        "registered_user_password?$USER_DATA={$USER_DATA}?$ID_BRAND={$ID_BRAND}",
        "registered_user_password"
    )

    object ProcessForgotPassword : Screen(
        "process_forgot_password_screen?$PREVIOUS_SCREEN={$PREVIOUS_SCREEN}?$EMAIL={$EMAIL}?$ID_BRAND={$ID_BRAND}?$PK_USER={$PK_USER}",
        "process_forgot_password_screen"
    )

    object SignUpSplashComeBackScreen : Screen(
        "sign_up_splash_come_back_screen/{$SIGN_UP_STEP}",
        "sign_up_splash_come_back_screen"
    )

    object SignInOTPScreen : Screen(
        "sign_in_otp_screen/{$EMAIL}/{$PASSWORD}/{$DEVICE_ID}/{$UNIQUE_ID}/{$IP_ADDRESS}/{$DEVICE_TYPE}/{$DEVICE_NAME}/{$APP_VERSION}/{$DEVICE_BRAND}/{$DEVICE_MODEL}/{$IS_EMULATOR}",
        "sign_in_otp_screen"
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
        "profile_verify_identity_phone_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$ID_BRAND}/{$PK_USER}/{$PHONE_NUMBER}/{$NEW_PHONE_NUMBER}/{$EMAIL}/{$IDENTIFICATION}/{$USER_NAME}/{$FIRST_NAME}/{$PHONE_NUMBER_CODE}",
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
        "profile_validate_otp_screen/{$ID_CLIENT}/{$CHANGING_FIELD}/{$NEW_VALUE}/{$SEND_METHOD}/{$IDENTIFICATION}/{$FIRST_NAME}/{$EMAIL}/{$PHONE_NUMBER}/{$PK_USER}/{$ID_BRAND}/{$USER}/{$PHONE_NUMBER_CODE}",
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
        "profile_change_password_screen/{$ID_BRAND}/{$PK_USER}/{$USER_NAME}/{$PREVIOUS_SCREEN}",
        "profile_change_password_screen"
    )

    object ProfileCardListScreen : Screen(
        "profile_card_list_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PROFILE_CARD_LIST_ORIGIN}",
        "profile_card_list_screen"
    )

    object ProfileMyCardsEditCardScreen : Screen(
        route = "profile_my_cards_edit_card_screen/{$IDENTIFICATION}/{$USER}/{$ID_BRAND}/{$CARD_SELECTED}",
        baseRoute = "profile_my_cards_edit_card_screen"
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

    object DisbursementVoucherScreen : Screen(
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
        "non_pre_approved_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}/{$EVICERTIA_STATUS}/{$SIGN_DOCUMENT_ID_PRINT}",
        "non_pre_approved_screen"
    )

    object CreditMovementsScreen : Screen(
        "credit_movements_screen/{$ID_BRAND}/{$ID_LOAN_CLIENT}/{$USER}/{$CREDIT_NUMBER}",
        "credit_movements_screen"
    )

    object CreditOnfidoScreen : Screen(
        "credit_onfido_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$EVICERTIA_STATUS}",
        "credit_onfido_screen"
    )

    object SignDocumentProcessScreen : Screen(
        "sign_document_process_screen/{$SIGN_DOCUMENT_STEP_ARG}/{$SIGN_DOCUMENT_ORIGIN}/{$SIGN_DOCUMENT_ID_PRINT}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}",
        "sign_document_process_screen"
    )

    object SmartSignScreen : Screen(
        "smart_sign_document_process_screen/{$SIGN_DOCUMENT_STEP_ARG}/{$SIGN_DOCUMENT_URL}/{$SIGN_DOCUMENT_ID_PRINT}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$ID_USER_REQUEST}/{$FIRST_NAME}/{$LAST_NAME}/{$IS_SMART_EVICERTIA}/{$SIGN_DOCUMENT_GLOBAL_ID}/{$USER}",
        "smart_sign_document_process_screen"
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
        "visa_issuance_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$BALANCE_CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "visa_issuance_screen"
    )

    object VisaCardScreen : Screen(
        "visa_card_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$BALANCE_CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "visa_card_screen"
    )

    object VisaTokenizationWaitingScreen : Screen(
        "visa_tokenization_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$PHONE_NUMBER}/{$BALANCE_CARD_INFORMATION}",
        "visa_tokenization_screen"
    )

    object VisaPreferencesScreen : Screen(
        "visa_preferences_screen/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$USER}/{$PHONE_NUMBER}/{$BALANCE_CARD_INFORMATION}/{$AVAILABLE_BALANCE_LABEL}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
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

    object PaymentVoucherScreen : Screen(
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
        "payment_location_details_screen?$POINT_NAME={$POINT_NAME}?$POINT_ADDRESS={$POINT_ADDRESS}?$POINT_ADDRESS_DESCRIPTION={$POINT_ADDRESS_DESCRIPTION}?$POINT_SCHEDULE={$POINT_SCHEDULE}?$PAYMENT_AMOUNT={$PAYMENT_AMOUNT}?$CREDIT_NUMBER={$CREDIT_NUMBER}?$ID_BRAND={$ID_BRAND}",
        "payment_location_details_screen"
    )

    object PaymentCardVoucherScreen : Screen(
        "payment_voucher_vd_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$CARD_SELECTED}/{$CURRENT_AMOUNT_VALUE}/{$IS_AUTOMATIC_PAYMENT_CHECKED}/{$REFERENCE_NUMBER}/{$PAYMENT_DATE}",
        "payment_voucher_vd_screen"
    )

    // Smart
    object SmartScreen : Screen(
        "smart_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$CREDIT_STEP}/{$FIRST_NAME}/{$LAST_NAME}/{$ONFIDO_STATUS}/{$COMING_FROM_CRYPTO}/{$ID_GLOBAL_REQUEST}",
        "smart_screen"
    )

    object SmartOnfidoScreen : Screen(
        "smart_onfido_screen/{$USER}/{$ID_BRAND}/{$PK_USER}/{$IDENTIFICATION}/{$EMAIL}/{$FIRST_NAME}/{$LAST_NAME}/{$SIGN_DOCUMENT_ID_PRINT}/{$SIGN_DOCUMENT_GLOBAL_ID}/{$SIGN_DOCUMENT_URL}",
        "smart_onfido_screen"
    )

    object SmartMovementsScreen : Screen(
        "smart_movements_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ACCOUNT_TOKEN}",
        "smart_movements_screen"
    )

    // Smart Payment

    object SmartPaymentOptionsScreenCR : Screen(
        "smart_payment_options_screen/{$SMART_ACCOUNT_LIST}/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}",
        "smart_payment_options_screen"
    )

    object SmartPaymentAccountScreenCR : Screen(
        "smart_payment_accounts_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$SMART_PAYMENT_ACCOUNTS}/{$SMART_ACCOUNT}",
        "smart_payment_accounts_screen"
    )

    object SmartPaymentMethodScreenSV : Screen(
        "smart_payment_method_screen/{$SMART_ACCOUNT}",
        "smart_payment_method_screen"
    )

    object SmartPaymentCardsScreenSV : Screen(
        "smart_payment_cards_screen/{$SMART_ACCOUNT}",
        "smart_payment_cards_screen"
    )

    object SavingMethodTransferScreen : Screen(
        "saving_method_transfer_screen/{$SMART_ACCOUNT_NUMBER}",
        "saving_method_transfer_screen"
    )

    /**
     * Params in order
     * @param PREVIOUS_SCREEN: Route to the previous screen
     * @param ORIGIN_ACCOUNT: Card visa direct
     * @param DESTINY_ACCOUNT: Smart account
     * @param TRANSFER_TYPE: Int id indicating transfer type (VisaToSmart)
     */
    object SmartPaymentSavingAmountSV : Screen(
        "smart_payment_saving_amount_screen_sv/{$PREVIOUS_SCREEN}/{$ORIGIN_ACCOUNT}/{$DESTINY_ACCOUNT}/{$TRANSFER_TYPE}",
        "smart_payment_saving_amount_screen_sv"
    )

    /**
     * Params in order
     * @param PREVIOUS_SCREEN: Route to the previous screen
     * @param ORIGIN_ACCOUNT: Iban account
     * @param DESTINY_ACCOUNT: Smart account
     * @param TRANSFER_TYPE: Int id indicating transfer type (IbanToSmart)
     */
    object SmartPaymentSavingAmountCR : Screen(
        "smart_payment_saving_amount_screen_cr/{$PREVIOUS_SCREEN}/{$ORIGIN_ACCOUNT}/{$DESTINY_ACCOUNT}/{$TRANSFER_TYPE}",
        "smart_payment_saving_amount_screen_cr"
    )

    // Smart Transfer Screens

    object SmartSelectSendingTypeScreen : Screen(
        "smart_select_sending_type_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$SMART_ACCOUNT}/{$SECOND_SMART_ACCOUNT}/{$ID_CLIENT}/{$PREVIOUS_SCREEN}",
        "smart_select_sending_type_screen"
    )

    object SmartSelectAccountScreen : Screen(
        "smart_select_account_screen/{$SMART_ACCOUNT_LIST}/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$ID_CLIENT}",
        "smart_select_account_screen"
    )

    object SmartTransferRegisterIbanScreen : Screen(
        "smart_transfer_register_iban_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}/{$SMART_ACCOUNT}",
        "smart_transfer_register_iban_screen"
    )

    object SmartTransferIbanAccountScreen : Screen(
        "transfer_iban_account_screen/{$SMART_ACCOUNT}/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$PREVIOUS_SCREEN}/{$ID_CLIENT}",
        "transfer_iban_account_screen"
    )

    /**
     * Params in order
     * @param ORIGIN_ACCOUNT: Smart account
     * @param DESTINY_ACCOUNT: Iban account
     * @param TRANSFER_TYPE: Int id indicating transfer type (SmartToIban)
     */
    object SmartTransferAmountScreen : Screen(
        "smart_transfer_amount_screen/{$ORIGIN_ACCOUNT}/{$DESTINY_ACCOUNT}/{$TRANSFER_TYPE}",
        "smart_transfer_amount_screen"
    )

    /**
     * Params in order
     * @param ORIGIN_ACCOUNT: Origin smart account
     * @param DESTINY_ACCOUNT: Destiny smart account
     * @param TRANSFER_TYPE: Int id indicating transfer type (SmartToSmart)
     */
    object OwnTransferAmountScreen : Screen(
        "own_transfer_amount_screen/{$ORIGIN_ACCOUNT}/{$DESTINY_ACCOUNT}/{$TRANSFER_TYPE}",
        "own_transfer_amount_screen"
    )

    object SmartAddSACAccountScreen : Screen(
        "smart_add_sac_account_screen/{$ID_BRAND}/{$USER}/{$SMART_ACCOUNT}",
        "smart_add_sac_account_screen"
    )

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen/{$}")

    // Crypto
    object CryptoWalletScreen : Screen(
        "crypto_wallet_screen/{$USER}/{$ID_BRAND}/{$IDENTIFICATION}/{$GLOBAL_CRYPTO_BALANCE}/{$ID_CLIENT}/{$ID_LOAN_CLIENT}/{$STATUS_CREDIT}/{$STATUS_SMART}/{$STATUS_CRYPTO}/{$CARD_STATUS}",
        "crypto_wallet_screen"
    )

    object CryptoCurrencyMovementsScreen : Screen(
        "crypto_currency_movements_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}/{$ITEM_CRYPTO_CURRENCY}",
        "crypto_currency_movements_screen"
    )

    object CryptoMarketScreen : Screen(
        "crypto_market_screen/{$USER}/{$ID_BRAND}",
        "crypto_market_screen"
    )

    object CryptoMovementsAllScreen : Screen(
        "crypto_movements_all_screen/{$ID_BRAND}/{$IDENTIFICATION}/{$USER}/{$PREVIOUS_SCREEN}?$CRYPTO_ASSET={$CRYPTO_ASSET}",
        "crypto_movements_all_screen"
    )

    object CryptoCurrencyDetailsScreen : Screen(
        "crypto_currency_details_screen/{$USER}/{$ID_BRAND}/{$CRYPTO_ASSET}/{$DESCRIPTION_CURRENCY}/{$CURRENT_CRYPTO_PRICE}/{$URL_IMAGE}",
        "crypto_currency_details_screen"
    )

    object CryptoPurchaseListScreen : Screen(
        "crypto_list_purchase/{$USER}/{$ID_BRAND}",
        "crypto_list_purchase"
    )
}
