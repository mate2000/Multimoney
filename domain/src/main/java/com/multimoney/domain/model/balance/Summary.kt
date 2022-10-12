package com.multimoney.domain.model.balance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Summary(
    val idCurrency: Int?,
    val currency: String?,
    val currentBalance: Double?,
    val currentBalanceLabel: String?,
    val availableBalance: String?,
    val availableBalanceLabel: String?,
    val paymentDateLabel: String?,
    val monthlyQuota: String?,
    val monthlyQuotaLabel: String?,
    val balanceAmountCancel: String,
    val daysExpired: Int
) : Parcelable