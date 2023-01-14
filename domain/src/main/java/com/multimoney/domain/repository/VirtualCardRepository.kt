package com.multimoney.domain.repository

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit
import com.multimoney.domain.model.virtualcard.CardBlocking
import com.multimoney.domain.model.virtualcard.CardUnblocking
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.model.virtualcard.DeleteCard
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
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
}
