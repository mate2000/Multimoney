package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ManageSinpeAccountSaveMutation
import com.multimoney.domain.model.accountsmart.SaveSinpeAccount

private fun ManageSinpeAccountSaveMutation.Result.mapToDomainModel() =
    SaveSinpeAccount(id_Account_Sinpe.toString().toLong())

private fun ManageSinpeAccountSaveMutation.ManageSinpeAccountSave.mapToDomainModel() =
    this.result.mapToDomainModel()

fun ManageSinpeAccountSaveMutation.Data.mapToDomainModel() =
    this.manageSinpeAccountSave?.mapToDomainModel()
