package com.multimoney.domain.model.security

data class ValidateUserStatus(
    val infoUser: InfoUser?,

    val infoCredit: InfoCredit?,

    val infoVirtualCard: InfoVirtualCard?,

    val infoBankAccount: InfoBankAccount?,

    val infoCrypto: InfoCrypto?
)
