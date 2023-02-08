package com.multimoney.multimoney.presentation.ui.crypto

enum class CryptoProcessErrorCodes(val status: Int) {
    // purchase
    WeeklyLimitExceeded(2804),
    InsufficientFundsBuy(2807),
    ExpiredPriceBuy(2808),
    // sell
    InsufficientFundsSell(2829),
    ExpiredPriceSell(2830),
    // transfer
    MonthlyLimitExceededLocal(2853),
    MonthlyLimitExceededExternal(2878),
    InsufficientFundsToApplyTransfer(2858),
}