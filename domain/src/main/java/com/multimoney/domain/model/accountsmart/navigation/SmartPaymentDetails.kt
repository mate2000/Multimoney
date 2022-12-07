package com.multimoney.domain.model.accountsmart.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartPaymentDetails(
    var currency: String,
    var exchangeCurrency: String,
    var amount: String,
    var exchangeAmount: String,
    var isMultiCurrency: Boolean,
    var exchangeRate: String,
    var cardNumberMasked: String,
    var referenceNumber: String
) : Parcelable
