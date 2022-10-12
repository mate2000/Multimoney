package com.multimoney.data.mapper.balances

import com.multimoney.data.networking.balances.apollomodel.BalanceQuery
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceAccountSmart
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.domain.model.balance.Summary

private fun BalanceQuery.BalanceCredit.mapToDomainModel() = BalanceCredit(
    summary = resumen?.map {
        Summary(
            idCurrency = it?.id_Moneda,
            currency = it?.moneda,
            currentBalance = it?.saldo_Actual.toString().toDouble(),
            currentBalanceLabel = it?.saldo_Actual_Label,
            availableBalance = it?.saldo_Disponible.toString(),
            availableBalanceLabel = it?.saldo_Disponible_Label,
            paymentDateLabel = it?.fecha_Pago_Label,
            monthlyQuotaLabel = it?.cuota_Mensual_Label,
            minPaymentLabel = it?.pago_Minimo_Label,
            expiredPayment = it?.pagos_Vencidos ?: 0,
            expiredDays = it?.dias_Vencidos ?: 0,
            ibanAccount = it?.cuenta_Iban ?: "",
            monthlyQuota = it?.cuota_Mensual.toString(),
            balanceAmountCancel = it?.saldo_Monto_Cancelar.toString()
        )
    },
    creditNumber = this.pagare,
    creditLimitLabel = this.limite_credito_label,
    term = this.plazo
)

private fun BalanceQuery.BalanceAccountSmart.mapToDomainModel() =
    BalanceAccountSmart(
        account = accounts?.map {
            Account(
                totalBalance = it?.totalBalance.toString().toDouble(),
                gainedInterest = it?.gainedInterest.toString().toDouble()
            )
        }
    )

private fun BalanceQuery.BalanceCryptoAccount.mapToDomainModel() =
    BalanceCryptoAccount(globalBalance = globalBalance.toString().toDouble())

private fun BalanceQuery.BalanceCardInformation.mapToDomainModel() =
    BalanceCardInformation(cardInformation = CardInformation(cardInfo?.cardNumber))

fun BalanceQuery.Data.mapToDomainModel() =
    Balance(
        balanceCredit = balanceCredit?.map { it?.mapToDomainModel() },
        balanceAccountSmart = balanceAccountSmart?.mapToDomainModel(),
        balanceCryptoAccount = balanceCryptoAccount?.mapToDomainModel(),
        balanceCardInformation = balanceCardInformation?.mapToDomainModel()
    )
