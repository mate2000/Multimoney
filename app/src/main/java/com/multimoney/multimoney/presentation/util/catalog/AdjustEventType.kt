package com.multimoney.multimoney.presentation.util.catalog

enum class AdjustEventType(val stgToken: String, val prodToken: String, val eventId: String) {
    SPLASH_1001("6t3wnx", "162spm", "Splash"),

    // ON BOARDING
    ON_BOARDING_1_1002("dn9tlm", "q4pfbo", "onboarding_1"),
    ON_BOARDING_2_1003("buvcxq", "6aato7", "onboarding_2"),
    ON_BOARDING_3_1004("mt8aki", "y43ho8", "onboarding_3"),

    // SIGNUP
    SIGNUP_FIRST_BUTTON_CLICKED_2000("1ppswb", "677x4e", "signup"),
    SIGNUP_1_2001("e70xxi", "mwgqqq", "signup_1"),
    SIGNUP_2_2002("juh5da", "841kw5", "signup_2"),
    SIGNUP_3_2003("80haw1", "3rdgkn", "signup_3"),
    SIGNUP_4_2004("c03f37", "acdu94", "signup_4"),
    SIGNUP_5_2007("lo8d23", "gg2i4j", "signup_5"),
    SIGNUP_RESEND_OTP_2005("", "", "signup_reenviar_otp"),
    SIGNUP_OTP_BY_CALL_2006("", "", "signup_llamada"),
    SIGNUP_SUCCESS_2008("", "", "signup_exito"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_EMAIL_2009("f5no3g", "3a448q", "signup_ce_ingresa_email"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_OTP_EMAIL_2010("", "", "signup_ce_otp_email"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_OTP_SMS_2011("", "", "signup_ce_otp_sms"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_OTP_SUCCESS_CONFIRMATION_2012("tc8kf4", "czalev", "signup_ce_verfica_otp"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_RESEND_OTP_2013("", "", "signup_ce_reenvia_otp"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_OTP_BY_CALL_2014("", "", "signup_ce_llamada"),
    SIGNUP_ALREADY_BEEN_CUSTOMERS_CREATE_PASSWORD_2015("8q7lm7", "auq95w", "signup_ce_contraseña"),

    // LOGIN
    FIRST_LOGIN_3000("44ivkd", "4sty20", "login_first_time"),
    LOGIN_3001("", "", "login"),

    // FORGOT
    FORGOT_CONFIRM_EMAIL_4000("", "", "olvide_contraseña"),
    FORGOT_CONFIRM_CORRECT_PASSWORD_4001("", "", "cambiar_contraseña"),
    FORGOT_RESEND_OTP_4002("", "", "cambiar_contraseña_reenviar_otp"),
    FORGOT_OTP_BY_CALL_4003("", "", "cambiar_contraseña_llamada"),
    FORGOT_SUCCESS_4004("", "", "cambiar_contraseña_exito"),

    // ORIGINATION
    ORIGINATION_OFFER_FIRST_TIME("", "", ""),
    ORIGINATION_OFFER("", "", ""),
    ORIGINATION_FIRST_CHECK_TERMS("", "", ""),
    ORIGINATION_FIRST_ENTER_AMOUNT("", "", ""),
    ORIGINATION_FIRST_FILL_ACCOUNT("", "", ""),
    ORIGINATION_FIRST_FILL_MONTHLY_AMOUNT("", "", ""),
    ORIGINATION_FIRST_FILL_COMPANY_INFORMATION("", "", ""),
    ORIGINATION_FIRST_FILL_OWN_ADDRESS("", "", ""),
    ORIGINATION_FIRST_FILL_PEP("", "", ""),
    ORIGINATION_FIRST_START_ON_FIDO("", "", ""),
    ORIGINATION_FIRST_ADD_DOCUMENT_ON_FIDO("", "", ""),
    ORIGINATION_FIRST_FINNISH_ON_FIDO("", "", ""),
    ORIGINATION_FIRST_FINNISH_EVICERTIA("", "", ""),
    ORIGINATION_WAIT_SCREEN_EVICERTIA("", "", ""),
    ORIGINATION_FIRST_CUSTOMER_REJECTED("", "", ""),
    ORIGINATION_FIRST_CUSTOMER_COMPLETE_REQUEST("", "", ""),
    ORIGINATION_FIRST_NON_PRE_APPROVED_INFORMATION("", "", ""),
    ORIGINATION_FIRST_NON_PRE_APPROVED_IS_APPROVED("", "", ""),
    ORIGINATION_FIRST_NON_PRE_APPROVED_IS_REJECTED("", "", ""),

    // DISBURSEMENT
    DISBURSEMENT_FIRST_INIT_PROCESS("", "", ""),
    DISBURSEMENT_FIRST_FINISH_PROCESS_SUCCESS("", "", ""),

    // CROSSELLING
    CROSSELLING_OFFER_FIRST_TIME("", "", ""),
    CROSSELLING_OFFER("", "", ""),
    CROSSELLING_FIRST_CHECK_TERMS("", "", ""),
    CROSSELLING_FIRST_ENTER_AMOUNT("", "", ""),
    CROSSELLING_FIRST_FILL_ACCOUNT("", "", ""),
    CROSSELLING_FIRST_EXTRA_INFORMATION("", "", ""),
    CROSSELLING_FIRST_FINNISH_EVICERTIA("", "", ""),
    CROSSELLING_FIRST_CUSTOMER_COMPLETE_REQUEST("", "", ""),

    // HOME CREDIT
    HOME_CTA_ENABLED_FIRST_AUTOMATIC_PAYMENT("", "", ""),
    HOME_CTA_FIRST_START_PAYMENT("", "", ""),
    HOME_CTA_FIRST_REQUEST_ACCOUNT_STATUS("", "", ""),

    // HOME
    HOME_CTA_FIRST_ACTIVATE_MM_VISA("", "", ""),

    // MM VISA
    MM_VISA_CTA_FIRST_LINK_MM_VISA("", "", ""),
    MM_VISA_CTA_FIRST_MM_VISA_ACTIVATED("", "", ""),

    // SETTINGS
    SETTINGS_CHANGE_EMAIL_SUCCESS("", "", ""),
    SETTINGS_CHANGE_PHONE_SUCCESS("", "", ""),
    SETTINGS_CHANGE_PASSWORD_SUCCESS("", "", ""),
    SETTINGS_FIRST_ADD_ACCOUNT("", "", ""),
    SETTINGS_USER_WITHOUT_ACCOUNT("", "", ""),
    SETTINGS_FIRST_ADD_CARD("", "", ""),
    SETTINGS_USER_WITHOUT_CARD("", "", ""),
    SETTINGS_CTA_FIRST_START_FLOW_CARD("", "", ""),
    SETTINGS_CTA_FIRST_FINISH_FLOW_CARD("", "", ""),
    SETTINGS_CTA_FIRST_VALIDATED_FLOW_CARD("", "", "")
}
