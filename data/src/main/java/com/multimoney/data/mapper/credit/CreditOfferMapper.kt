package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.CreditOfferQuery
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.Product

private fun CreditOfferQuery.Product.mapToDomainModel() = Product(
    id = iD_PRODUCTO ?: "",
    creditLimit = lIMITE_CREDITO ?: "",
    progressFactor = tRACTO?.toDouble() ?: 0.0,
    minimumDisbursement = mINIMO_DESEMBOLSO ?: "",
    minimumDisbursementLabel = sTR_MINIMO_DESEMBOLSO ?: "",
    maximumDisbursement = mAXIMO_DESEMBOLSO ?: "",
    maximumDisbursementLabel = sTR_MAXIMO_DESEMBOLSO ?: "",
    term = pLAZO_NUMERICO ?: 0,
    termLabel = pLAZO ?: "",
    currency = mONEDA ?: "",
    regularInterestRate = tASA_INTERES_NORMAL ?: "",
    regularInterestRateLabel = sTR_TASA_INTERES_NORMAL ?: "",
    fee = cuota_Real.toString().toDouble() ?: 0.0,
    feeLabel = cUOTA ?: "",
    commissionDisbursement = cOMISION_DESEMBOLSO ?: "",
    commissionDisbursementLabel = sTR_COMISION_DESEMBOLSO ?: "",
    paymentDate = fECHA_PAGO ?: "",
    currencyName = nOMBRE_MONEDA ?: "",
    messageConditions = mensaje_Condiciones ?: "",
    isFormalizationRequired = bIT_REQUIERE_FORMALIZACION ?: ""
)

private fun CreditOfferQuery.CreditOffer.mapToDomainModel() =
    CreditOffer(idUserRequest = idUserRequest as Int, products = products?.map { it.mapToDomainModel() } ?: listOf())

fun CreditOfferQuery.Data.mapToDomainModel() = creditOffer.mapToDomainModel()
