package com.multimoney.data.mapper.balances

import com.multimoney.data.networking.graphql.apollomodel.BalanceCardInformationQuery
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.balance.CardInformation

private fun BalanceCardInformationQuery.BalanceCardInformation.mapToDomainModel() =
    BalanceCardInformation(
        cardInformation = CardInformation(
            cardToken = cardInfo.cardToken,
            cardNumber = cardInfo.cardNumber,
            expDate = cardInfo.expDate,
            holderName = cardInfo.holderName,
            status = cardInfo.status,
            blockType = cardInfo.blockType,
            cValidation = cardInfo.cvalidation,
            type = cardInfo.type
        ),
        floatingBalance = sALDO_FLOTANTE,
        allowUnLock = pERMITE_DESBLOQUEO,
        disbursementCommission = cOMISION_DESEMBOLSO,
        interestRate = tASA_INTERES,
        term = pLAZO,
        fullName = nOMBRE_COMPLETO,
        remission = rEMISION
    )

fun BalanceCardInformationQuery.Data.mapToDomainModel() = balanceCardInformation?.mapToDomainModel()
