package com.multimoney.multimoney.presentation.util.catalog

sealed class CognitoErrorCode(val code: String) {
    object SessionActive : CognitoErrorCode("2885")
    object SessionBlocked : CognitoErrorCode("2889")
    object BlacklistedDevice : CognitoErrorCode("2888")
}
