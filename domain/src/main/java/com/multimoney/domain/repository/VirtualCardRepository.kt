package com.multimoney.domain.repository

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit
import com.multimoney.domain.model.virtualcard.CardBlocking
import com.multimoney.domain.model.virtualcard.CardUnblocking
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.model.virtualcard.DeleteCard
import com.multimoney.domain.model.virtualcard.MicroDepositVD
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import com.multimoney.domain.model.virtualcard.ResendMicroDepositVD
import com.multimoney.domain.model.virtualcard.UpdateCard
import kotlinx.coroutines.flow.Flow

interface VirtualCardRepository {
    suspend fun queryListCardVD(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>>

    suspend fun mutationPayCreditVD(
        identification: String,
        currency: String,
        paymentAmount: Double,
        operationNumber: String,
        reference: String,
        comment: String,
        cardMasked: String,
        idCard: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<PayCreditVisaDirect?>>

    suspend fun mutationUpdateCardVD(
        idCard: Long,
        identification: String,
        cardDescription: String,
        cardMasked: String,
        expirationMonth: String,
        expirationYear: String,
        verificationValue: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<UpdateCard?>>

    suspend fun mutationDeleteCardVD(
        identification: String,
        user: String,
        idBrand: Int,
        idCard: Long
    ): Flow<MultimoneyResult<DeleteCard?>>

    suspend fun mutationActivatedCardAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idCard: Long,
        cardMasked: String
    ): Flow<MultimoneyResult<AutomaticCardDebit?>>

    suspend fun mutationCardBlocking(
        blockType: String,
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardBlocking?>>

    suspend fun mutationCardUnblocking(
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardUnblocking?>>

    suspend fun mutationMicroDepositVD(
        identification: String,
        idCard: String,
        code: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MicroDepositVD?>>

    suspend fun mutationResendMicroDepositVD(
        identification: String,
        idCard: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ResendMicroDepositVD?>>
}
