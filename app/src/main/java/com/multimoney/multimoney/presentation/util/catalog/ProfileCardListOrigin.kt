package com.multimoney.multimoney.presentation.util.catalog

sealed class ProfileCardListOrigin(val value: String) {
    object Profile : ProfileCardListOrigin("profile")
    object Product : ProfileCardListOrigin("product")
}