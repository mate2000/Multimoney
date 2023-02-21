package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.virtualcard.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit
import com.multimoney.domain.model.virtualcard.CardBlocking
import com.multimoney.domain.model.virtualcard.CardUnblocking
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.model.virtualcard.DeleteCard
import com.multimoney.domain.model.virtualcard.MicroDepositVD
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import com.multimoney.domain.model.virtualcard.ResendMicroDepositVD
import com.multimoney.domain.model.virtualcard.UpdateCard
import com.multimoney.domain.model.virtualcard.CreateUser
import com.multimoney.domain.model.virtualcard.CreateCard
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VirtualCardRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), VirtualCardRepository {

    override suspend fun queryListCardVD(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>> = fetchData(
        apolloCall = graphqlApi.queryListCardsVD(
            user,
            idBrand,
            identification
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationPayCreditVD(
        identification: String,
        currency: String,
        paymentAmount: Double,
        operationNumber: String,
        reference: String,
        comment: String,
        cardMasked: String,
        idCard: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<PayCreditVisaDirect?>> = fetchData(
        apolloCall = graphqlApi.mutationPayCreditVD(
            identification = identification,
            currency = currency,
            paymentAmount = paymentAmount,
            operationNumber = operationNumber,
            reference = reference,
            comment = comment,
            cardMasked = cardMasked,
            idCard = idCard,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            if (data.payCreditVD.status == null || data.payCreditVD.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationUpdateCardVD(
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
    ): Flow<MultimoneyResult<UpdateCard?>> = fetchData(
        apolloCall = graphqlApi.mutationUpdateCardVD(
            idCard = idCard,
            identification = identification,
            cardDescription = cardDescription,
            cardMasked = cardMasked,
            expirationMonth = expirationMonth,
            expirationYear = expirationYear,
            verificationValue = verificationValue,
            default = default,
            user = user,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationCreateCardVD(
        identification: String,
        cardTokenID: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreateCard?>> = fetchData(
        apolloCall = graphqlApi.mutationCreateCardVD(
            identification = identification,
            cardTokenID = cardTokenID,
            default = default,
            user = user,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationCreateUserVD(
        identification: String,
        firstName: String,
        secondName: String,
        lastName: String,
        secondLastName: String,
        email: String,
        callerId: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreateUser?>> = fetchData(
        apolloCall = graphqlApi.mutationCreateUserVD(
            identification = identification,
            firstName = firstName,
            secondName = secondName,
            lastName = lastName,
            secondLastName = secondLastName,
            email = email,
            callerId = callerId,
            user = user,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationDeleteCardVD(
        identification: String,
        user: String,
        idBrand: Int,
        idCard: Long
    ): Flow<MultimoneyResult<DeleteCard?>> = fetchData(
        apolloCall = graphqlApi.mutationDeleteCardVD(
            identification = identification,
            user = user,
            idBrand = idBrand,
            idCard = idCard
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationActivatedCardAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idCard: Long,
        cardMasked: String
    ): Flow<MultimoneyResult<AutomaticCardDebit?>> =
        fetchData(
            apolloCall = graphqlApi.mutationActivatedCardAutomaticDebit(
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoanClient = idLoanClient,
                idCard = idCard,
                cardMasked = cardMasked
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun mutationCardBlocking(
        blockType: String,
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardBlocking?>> = fetchData(
        apolloCall = graphqlApi.mutationCardBlocking(
            blockType, observations, clientId, userApp, cardToken, source, idLoan, user, idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationCardUnblocking(
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardUnblocking?>> = fetchData(
        apolloCall = graphqlApi.mutationCardUnblocking(
            observations,
            clientId,
            userApp,
            cardToken,
            source,
            idLoan,
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationMicroDepositVD(
        identification: String,
        idCard: String,
        code: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MicroDepositVD?>> = fetchData(
        apolloCall = graphqlApi.mutationMicroDepositVD(
            identification,
            idCard,
            code,
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            if (data.microDepositVD.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationResendMicroDepositVD(
        identification: String,
        idCard: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ResendMicroDepositVD?>> = fetchData(
        apolloCall = graphqlApi.mutationResendMicroDepositVD(
            identification,
            idCard,
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            if (data.resendMicroDepositVD.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )
}
