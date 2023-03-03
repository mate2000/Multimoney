package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.ReleaseCryptoTransferMutation
import com.multimoney.domain.model.crypto.ReleaseTransactionResponse

fun ReleaseCryptoTransferMutation.Data.mapToDomainModel() = ReleaseTransactionResponse(
    withHeld = this.releaseTransaction.withheld,
    userResponse = this.releaseTransaction.userResponse,
    hasError = this.releaseTransaction.haveError
)