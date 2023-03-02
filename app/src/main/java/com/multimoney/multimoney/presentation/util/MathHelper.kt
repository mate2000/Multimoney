package com.multimoney.multimoney.presentation.util

import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.DEFAULT_AMOUNT
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreenViewModel
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.EMPTY_CURRENCY
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

// fun to calculate gain loses based on the list of historical balance
fun calculateGainLoses(
    currentBalance: Double,
    listOfBalance: List<HistoricalBalanceClient>
): Double {

    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val lastBalance = listOfBalance.last().convertedBalance
    return lastBalance - currentBalance
}

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

fun calculateConvertedCurrencyBalance(
    quoteAmount: String,
    baseAmount: String,
    exchangeRate: Double,
    price: Double?
): String {
    return (quoteAmount.ifEmpty {
        (baseAmount.toDoubleOrNull() ?: 0.0).times(price ?: 0.0).toString()
    }.toDouble() * exchangeRate).toCurrencyFormat(
        symbol = CurrencyType.Colon.symbol
    )
}

fun calculateConvertedCurrencyBalance(
    quoteAmount: String,
    baseAmount: String,
    exchangeRate: Double,
    price: Double?,
    totalFee: Double
): String {
    val convertedAmount = (quoteAmount.ifEmpty {
        (baseAmount.toDoubleOrNull() ?: 0.0).times(price ?: 0.0).toString()
    }.toDouble() * exchangeRate)
    val convertedFee = (totalFee * exchangeRate)
    return convertedAmount.minus(convertedFee).toCurrencyFormat(
        symbol = CurrencyType.Colon.symbol
    )
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
    return (currencyPrice * baseAmount.ifEmpty {
        EMPTY_CURRENCY
    }.toDouble()).roundToTwoDecimalPlaces()
}

fun calculateAssetEstimated(
    quoteAmount: String,
    currencyPrice: Double
): String {
    return (quoteAmount.ifEmpty {
        EMPTY_CURRENCY
    }.toDouble() / currencyPrice).roundToEightDecimalPlaces()
}

fun calculateConfirmationQuoteAmount(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?
): String {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().times(currencyPrice ?: 0.0)
    }.toString().toDouble().toCurrencyFormat()
}

fun calculateConfirmationQuoteAmountForVoucher(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?
): String {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().times(currencyPrice ?: 0.0)
    }.toString()
}

fun calculateConfirmationQuoteAmount(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?,
    symbol: String,
    totalFee: Double
): String {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().times(currencyPrice ?: 0.0)
    }.toString().toDouble().minus(totalFee).toCurrencyFormat(
        symbol = symbol
    )
}

fun calculateConfirmationQuoteAmount(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?,
    symbol: String
): String {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().times(currencyPrice ?: 0.0)
    }.toString().toDouble().toCurrencyFormat(
        symbol = symbol
    )
}

fun calculateConfirmationQuoteAmount(
    quoteAmount: String,
    baseAmount: String,
    currencyPrice: Double?,
    exchangeRate: Double,
    symbol: String
): String {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            DEFAULT_AMOUNT
        }.toDouble().times(currencyPrice ?: 0.0)
    }.toString().toDouble().times(exchangeRate).toCurrencyFormat(
        symbol = symbol
    )
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
): Double {
    return if (isTransformationCurrency.not()) {
        amount.ifEmpty { BuyCurrencyScreenViewModel.DEFAULT_BASE_AMOUNT_STRING }.toDouble()
    } else {
        amount.ifEmpty {
            BuyCurrencyScreenViewModel.DEFAULT_AMOUNT
        }.toDouble().times(price)
    }
}

fun calculateQuote(
    quoteAmount: String,
    baseAmount: String,
    price: Double
): Double {
    return quoteAmount.ifEmpty {
        baseAmount.ifEmpty {
            BuyCurrencyScreenViewModel.DEFAULT_BASE_AMOUNT_STRING
        }.toDouble().times(price)
    }.toString().toDouble().roundToTwoDecimalPlaces().toDouble()
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