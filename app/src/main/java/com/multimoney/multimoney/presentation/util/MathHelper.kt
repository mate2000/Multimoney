package com.multimoney.multimoney.presentation.util

import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreenViewModel
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.DEFAULT_AMOUNT
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.EMPTY_CURRENCY
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import java.math.RoundingMode
import java.text.DecimalFormat

fun calculateGainLosesMarketDetails(
    currentBalance: Double,
    listOfBalance: List<CurrencyHistoricPrice>
): Double {

    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val firstBalance = listOfBalance.first().average_price.toDouble()
    return currentBalance - firstBalance
}

fun calculatePercentageMarketDetails(
    currentBalance: Double,
    listOfBalance: List<CurrencyHistoricPrice>
): Double {
    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val firstBalance = listOfBalance.first().average_price.toDouble()
    return ((currentBalance - firstBalance) / currentBalance) * 100
}

fun calculateDollarEstimated(
    baseAmount: String,
    currencyPrice: Double
): String {
    return (currencyPrice * baseAmount.ifEmpty {
        EMPTY_CURRENCY
    }.toDouble()).toCurrencyFormat()
}

fun calculateDollarEstimatedWithoutFormat(
    baseAmount: String,
    currencyPrice: Double
): String {
    val estimated = (currencyPrice * baseAmount.ifEmpty {
        EMPTY_CURRENCY
    }.toDouble()).roundToTwoDecimalPlaces()
    val decimalCount = estimated.takeLast(3)
    val takeAfterDot = estimated.substringAfterLast('.').length
    return when {
        decimalCount == ".00" -> estimated.dropLast(3)
        takeAfterDot >= 2 -> estimated.dropLast(3)
        else -> estimated
    }
}

fun calculateAssetEstimated(
    quoteAmount: String,
    currencyPrice: Double
): String {
    return (quoteAmount.ifEmpty {
        EMPTY_CURRENCY
    }.toDouble() / currencyPrice).roundToEightDecimalPlaces()
}

fun calculateAmountToReceive(
    amountInUsd: Double,
    totalFee: Double?
) = amountInUsd.minus(totalFee ?: 0.0).toCurrencyFormat()

fun calculateConvertedAmount(
    amountInUsd: Double,
    exchangeRate: Double
) = amountInUsd.times(exchangeRate).toCurrencyFormat(CurrencyType.Colon.symbol)

fun calculateSellApproximate(
    amount: Double,
    exchangeRate: Double,
    idCurrency: Int
) = if (idCurrency == CurrencyType.Colon.id) {
    amount.times(exchangeRate).toCurrencyFormat(CurrencyType.Colon.symbol)
} else {
    amount.toCurrencyFormat()
}

fun calculateConfirmationBaseAmount(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?
): String {
    return baseAmount.ifEmpty {
        quoteAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().div(currencyPrice ?: 0.0)
    }.toString().toDouble().roundToEightDecimalPlaces()
}

fun calculateQuote(
    isTransformationCurrency: Boolean,
    amount: String,
    price: Double
)= if (isTransformationCurrency.not()) {
    amount.ifEmpty { BuyCurrencyScreenViewModel.DEFAULT_BASE_AMOUNT_STRING }.toDouble()
} else {
    amount.ifEmpty {
        BuyCurrencyScreenViewModel.DEFAULT_AMOUNT
    }.toDouble().times(price)
}

fun calculateBase(
    isTransformationCurrency: Boolean,
    amount: String,
    price: Double
) = if (isTransformationCurrency.not()) {
    amount.ifEmpty {
        BuyCurrencyScreenViewModel.DEFAULT_AMOUNT
    }.toDouble().div(price)
} else {
    amount.ifEmpty { BuyCurrencyScreenViewModel.DEFAULT_BASE_AMOUNT_STRING }.toDouble()
}

fun calculateAmountPlusFee(
    amount: String,
    fee: Double?
): Double {
    return amount.ifEmpty {
        BuyCurrencyScreenViewModel.DEFAULT_BASE_AMOUNT_STRING
    }.toDouble().plus(fee ?: 0.0)
}

fun calculateAvailableInDollars(
    baseAmount: Double,
    currencyPrice: Double
): Double = baseAmount.times(currencyPrice)

fun Double.roundToEightDecimals(): Double {
    val df = DecimalFormat("#.########")
    df.roundingMode = RoundingMode.UP
    return df.format(this).toDouble()
}