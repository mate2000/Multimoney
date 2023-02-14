package com.multimoney.data.mapper.balances

import com.multimoney.data.networking.graphql.apollomodel.BalanceQuery
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.balance.BalanceCredit
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.balance.BlockType
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
            daysExpired = it.dias_Vencidos,
            canExpandState = it.estado_Ampli,
            isProductActive = it.producto_Activo,
            applyCommerce = it.aplica_Ampli_Comercio,
            applyCreateCard = it.aplica_Crear_Tarjeta
        )
    },
    creditLimit = limite_credito,
    creditLimitLabel = limite_credito_label,
    creditNumber = pagare,
    term = plazo,
    applyAutomaticDebit = aplica_Debito_Aut,
    automaticDebitEnabled = debito_Aut_Activo,
    expiredAutomaticDebitCard = expired_Debito_Aut_Card
)

private fun BalanceQuery.Account.mapToDomainModel() =
    Account(
        totalBalance = totalBalance.toString().toDouble(),
        currencyCode = currencyCode,
        gainedInterest = gainedInterest.toString().toDouble(),
        accountNumber = accountNumber,
        ibanAccountNumber = ibanAccountNumber,
        totalInterest = totalInterest.toString(),
        tokenNumber = tokenNumber,
        idCurrencyAccount = idCurrencyAccount.toString().toIntOrNull(),
        month = month,
        customerId = customerId.toString().toLongOrNull(),
        interest = rate.toString().toDoubleOrNull()
    )

private fun BalanceQuery.BalanceCryptoAccount.mapToDomainModel() =
    BalanceCryptoAccount(
        status = status.toString().toInt(),
        message = message,
        outOfService = outOfService ?: false,
        globalBalance = globalBalance.toString().toDouble(),
        investedBalance = investedBalance.toString(),
        percentageInvested = percentageInvested.toString(),
        items = items?.map { it.mapToDomainModel() } ?: emptyList()
    )

private fun BalanceQuery.Item.mapToDomainModel() = BalanceCryptoAccountItems(
    asset = asset.toString(),
    available = available.toString().toDouble(),
    trading = trading.toString(),
    descriptionCurrency = descriptionCurrency.toString(),
    balanceDollars = balanceDollars.toString().toDouble(),
    investedBalanceCurrency = investedBalanceCurrency.toString(),
    percentageInvestedCurrency = percentageInvestedCurrency.toString(),
    priceOfTheDay = priceOfTheDay.toString().toDouble(),
    url_image = url_image.toString(),
    cryptoNetwork = crypto_network.toString()
)

private fun BalanceQuery.BalanceCardInformation.mapToDomainModel() = BalanceCardInformation(
    cardInformation = CardInformation(
        cardToken = cardInfo?.cardToken,
        cardNumber = cardInfo?.cardNumber,
        expDate = cardInfo?.expDate,
        holderName = cardInfo?.holderName,
        status = cardInfo?.status,
        blockType = BlockType(
            cardInfo?.blockType?.code,
            cardInfo?.blockType?.msg
        ),
        cValidation = cardInfo?.cvalidation,
        type = cardInfo?.type
    ),
    floatingBalance = sALDO_FLOTANTE,
    allowUnLock = pERMITE_DESBLOQUEO,
    disbursementCommission = cOMISION_DESEMBOLSO,
    interestRate = tASA_INTERES,
    term = pLAZO,
    fullName = nOMBRE_COMPLETO,
    remission = rEMISION,
    status = eSTADO
)

fun BalanceQuery.Data.mapToDomainModel() =
    Balance(
        balanceCredit = balanceCredit?.map { it.mapToDomainModel() },
        balanceAccountSmart = balanceAccountSmart?.accounts?.map { it.mapToDomainModel() },
        balanceCryptoAccount = balanceCryptoAccount?.mapToDomainModel(),
        balanceCardInformation = balanceCardInformation?.mapToDomainModel()
    )
