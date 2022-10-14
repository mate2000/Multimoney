package com.multimoney.domain.model.balance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Summary(
    val idCurrency: Int?,
    val currency: String?,
    val currentBalance: Double?,
    val currentBalanceLabel: String?,
    val availableBalance: Double?,
    val availableBalanceLabel: String?,
    val paymentDateLabel: String?,
    val monthlyQuota: String?,
    val monthlyQuotaLabel: String?,
    val minPayment: Double?,
    val minPaymentLabel: String?,
    val expiredPayment: Int?,
    val expiredDays: Int?,
    val ibanAccount: String?,
    val balanceAmountCancel: String
) : Parcelable
