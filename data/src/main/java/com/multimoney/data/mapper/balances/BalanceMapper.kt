package com.multimoney.data.mapper.balances

import com.multimoney.data.networking.graphql.apollomodel.BalanceQuery
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.domain.model.balance.Summary

private fun BalanceQuery.BalanceCredit.mapToDomainModel() = BalanceCredit(
    summary = resumen.map {
        Summary(
            idCurrency = it.id_Moneda,
            currency = it.moneda,
            currentBalance = it.saldo_Actual.toString().toDouble(),
            currentBalanceLabel = it.saldo_Actual_Label,
            availableBalance = it.saldo_Disponible.toString().toDouble(),
            availableBalanceLabel = it.saldo_Disponible_Label,
            paymentDate = it.fec_Pago.toString(),
            paymentDateLabel = it.fecha_Pago_Label,
            monthlyQuotaLabel = it.cuota_Mensual_Label,
            minPayment = it.pago_Minimo.toString().toDouble(),
            minPaymentLabel = it.pago_Minimo_Label,
            expiredPayment = it.pagos_Vencidos,
            expiredDays = it.dias_Vencidos,
            ibanAccount = it.cuenta_Iban,
            monthlyQuota = it.cuota_Mensual.toString(),
            balanceAmountCancel = it.saldo_Monto_Cancelar.toString(),
            daysExpired = it.dias_Vencidos
        )
    },
    creditLimit = limite_credito,
    creditLimitLabel = limite_credito_label,
    creditNumber = this.pagare,
    term = this.plazo
)

private fun BalanceQuery.Account.mapToDomainModel() =
    Account(
        totalBalance = this.totalBalance.toString().toDouble(),
        gainedInterest = this.gainedInterest.toString().toDouble()
    )

private fun BalanceQuery.BalanceCryptoAccount.mapToDomainModel() =
    BalanceCryptoAccount(globalBalance = globalBalance.toString().toDouble())

private fun BalanceQuery.BalanceCardInformation.mapToDomainModel() =
    BalanceCardInformation(cardInformation = CardInformation(cardInfo.cardNumber))

fun BalanceQuery.Data.mapToDomainModel() =
    Balance(
        balanceCredit = balanceCredit?.map { it.mapToDomainModel() },
        balanceAccountSmart = balanceAccountSmart?.accounts?.map { it.mapToDomainModel() },
        balanceCryptoAccount = balanceCryptoAccount?.mapToDomainModel(),
        balanceCardInformation = balanceCardInformation?.mapToDomainModel()
    )
