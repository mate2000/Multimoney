package com.multimoney.domain.model.security

data class UserPhoneMobileSave(
    val pkUserMobile: Long,
    val platform: String,
    val uuid: String,
    val deviceVersion: String,
    val manufacture: String,
    val deviceName: String,
    val serialNumber: String,
    val ipAddress: String,
    val latitude: String,
    val longitude: String,
    val tokenNotificationsPush: String,
    val deviceCard: String,
    val walletId: String,
    val status: Int?,
    val message: String?,
    val detail: String?
)
