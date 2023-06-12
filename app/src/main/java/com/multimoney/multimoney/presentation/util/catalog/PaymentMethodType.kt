package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

sealed class PaymentMethodType(
    val value: String,
    val icon: Int
) {

    object VisaDirect : PaymentMethodType(
        "VISA_DIRECT",
        R.drawable.ic_payment_visa
    )

    object TransferBank : PaymentMethodType(
        "TRANSFERENCIA_BANCARIA",
        R.drawable.ic_payment_transfer
    )

    object CashPaymentPoint : PaymentMethodType(
        "PUNTOS_PAGO_EFECTIVO",
        R.drawable.ic_payment_cash
    )
}
