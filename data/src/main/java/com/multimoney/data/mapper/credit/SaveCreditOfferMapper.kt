package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOfferMutation
import com.multimoney.domain.model.credit.Product
import com.multimoney.domain.model.credit.SaveCreditOffer

private fun SaveCreditOfferMutation.Product.mapToDomainModel() = Product(
    id = iD_PRODUCTO ?: "",
    creditLimit = lIMITE_CREDITO ?: "",
    progressFactor = tRACTO?.toDoubleOrNull() ?: 0.0,
    minimumDisbursement = mINIMO_DESEMBOLSO ?: "",
    minimumDisbursementLabel = sTR_MINIMO_DESEMBOLSO ?: "",
    maximumDisbursement = mAXIMO_DESEMBOLSO ?: "",
    maximumDisbursementLabel = sTR_MAXIMO_DESEMBOLSO ?: "",
    term = pLAZO_NUMERICO ?: 0,
    termLabel = pLAZO ?: "",
    currency = mONEDA ?: "",
    regularInterestRate = tASA_INTERES_NORMAL ?: "",
    regularInterestRateLabel = sTR_TASA_INTERES_NORMAL ?: "",
    fee = cuota_Real.toString().toDoubleOrNull() ?: 0.0,
    feeLabel = cUOTA ?: "",
    commissionDisbursement = cOMISION_DESEMBOLSO ?: "",
    commissionDisbursementLabel = sTR_COMISION_DESEMBOLSO ?: "",
    paymentDate = fECHA_PAGO ?: "",
    currencyName = nOMBRE_MONEDA ?: "",
    messageConditions = mensaje_Condiciones ?: "",
    isFormalizationRequired = bIT_REQUIERE_FORMALIZACION ?: ""
)

private fun SaveCreditOfferMutation.SaveCreditOffer.mapToDomainModel() = SaveCreditOffer(
    idUserRequest = idUserRequest.toString().toInt(),
    rejectedBlaze = rejectedBlaze,
    products = products?.map { it.mapToDomainModel() } ?: listOf()
)

fun SaveCreditOfferMutation.Data.mapToDomainModel() = saveCreditOffer.mapToDomainModel()
