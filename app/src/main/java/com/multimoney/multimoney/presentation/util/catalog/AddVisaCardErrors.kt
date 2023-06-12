package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

sealed class AddVisaCardErrors(val value: String, val title: Int, val description: Int) {
    object Default : AddVisaCardErrors(
        value = "Default",
        title = R.string.visa_add_card_error_default_title,
        description = R.string.visa_add_card_error_default_description
    )

    object SystemMalfunction : AddVisaCardErrors(
        value = "System Malfunction",
        title = R.string.visa_add_card_error_system_malfunction_title,
        description = R.string.visa_add_card_error_system_malfunction_description
    )

    object UnableToInclude : AddVisaCardErrors(
        value = "Unable to Include",
        title = R.string.visa_add_card_error_unable_to_include_title,
        description = R.string.visa_add_card_error_unable_to_include_description
    )

    object InvalidCardAccountValidation : AddVisaCardErrors(
        value = "Invalid Card Account Validation",
        title = R.string.visa_add_card_error_invalid_card_account_validation_title,
        description = R.string.visa_add_card_error_invalid_card_account_validation_description
    )

    object InvalidPaymentAccountValidation : AddVisaCardErrors(
        value = "Invalid payment account validation",
        title = R.string.visa_add_card_error_invalid_payment_account_validation_title,
        description = R.string.visa_add_card_error_invalid_payment_account_validation_description
    )

    object InvalidRequestPaymentAccountValidation : AddVisaCardErrors(
        value = "Invalid request payment account validation",
        title = R.string.visa_add_card_error_invalid_request_payment_account_validation_title,
        description = R.string.visa_add_card_error_invalid_request_payment_account_validation_description
    )

    object YouHaveReachedTheMaximum : AddVisaCardErrors(
        value = "You have reached the maximum",
        title = R.string.visa_add_card_error_you_have_reached_the_maximum_title,
        description = R.string.visa_add_card_error_you_have_reached_the_maximum_description
    )

    object ExpiredCard : AddVisaCardErrors(
        value = "Expired Card",
        title = R.string.visa_add_card_error_expired_card_title,
        description = R.string.visa_add_card_error_expired_card_description
    )

    object TooManyCardsPerUserMax : AddVisaCardErrors(
        value = "Too Many Cards Per User Max",
        title = R.string.visa_add_card_error_too_many_cards_per_user_max_title,
        description = R.string.visa_add_card_error_too_many_cards_per_user_max_description
    )

    object InvalidCardVerification : AddVisaCardErrors(
        value = "Invalid Card Verification",
        title = R.string.visa_add_card_error_invalid_card_verification_title,
        description = R.string.visa_add_card_error_invalid_card_verification_description
    )

    object InvalidVerificationValue : AddVisaCardErrors(
        value = "Invalid Verification Value",
        title = R.string.visa_add_card_error_invalid_verification_value_title,
        description = R.string.visa_add_card_error_invalid_verification_value_description
    )

    object InvalidCard : AddVisaCardErrors(
        value = "Invalid Card",
        title = R.string.visa_add_card_error_invalid_card_title,
        description = R.string.visa_add_card_error_invalid_card_description
    )

    object MaxUsersSameCard : AddVisaCardErrors(
        value = "Max Users Same Card",
        title = R.string.visa_add_card_error_max_users_same_card_title,
        description = R.string.visa_add_card_error_max_users_same_card_description
    )

    object AlreadyExist : AddVisaCardErrors(
        value = "Already Exist",
        title = R.string.visa_add_card_error_already_exist_title,
        description = R.string.visa_add_card_error_already_exist_description
    )

    object UserBlocked : AddVisaCardErrors(
        value = "User Blocked",
        title = R.string.visa_add_card_error_user_blocked_title,
        description = R.string.visa_add_card_error_user_blocked_description
    )

    object InvalidUsernameOrPassword : AddVisaCardErrors(
        value = "Invalid UserName or Password",
        title = R.string.visa_add_card_error_invalid_username_or_password_title,
        description = R.string.visa_add_card_error_invalid_username_or_password_description
    )

    object InvalidApplication : AddVisaCardErrors(
        value = "InvalidApplication",
        title = R.string.visa_add_card_error_invalid_application_title,
        description = R.string.visa_add_card_error_invalid_application_description
    )

    object Null : AddVisaCardErrors(
        value = "null",
        title = R.string.visa_add_card_error_null_title,
        description = R.string.visa_add_card_error_null_description
    )

    object EditFailed : AddVisaCardErrors(
        value = "editFailed",
        title = R.string.visa_add_card_error_edit_failed_title,
        description = R.string.visa_add_card_error_edit_failed_description
    )
}