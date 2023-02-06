package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ManageSinpeAccountSaveMutation
import com.multimoney.data.networking.graphql.apollomodel.ManageSinpeAccountUpdateMutation
import com.multimoney.domain.model.accountsmart.SaveSinpeAccount

//save

private fun ManageSinpeAccountSaveMutation.Result.mapToDomainModel() =
    SaveSinpeAccount(id_Account_Sinpe.toString().toLong())

private fun ManageSinpeAccountSaveMutation.ManageSinpeAccountSave.mapToDomainModel() =
    this.result.mapToDomainModel()

fun ManageSinpeAccountSaveMutation.Data.mapToDomainModel() =
    this.manageSinpeAccountSave?.mapToDomainModel()

// update

private fun ManageSinpeAccountUpdateMutation.Result.mapToDomainModel() =
    SaveSinpeAccount(id_Account_Sinpe.toString().toLong())

private fun ManageSinpeAccountUpdateMutation.ManageSinpeAccountUpdate.mapToDomainModel() =
    this.result.mapToDomainModel()

fun ManageSinpeAccountUpdateMutation.Data.mapToDomainModel() =
    this.manageSinpeAccountUpdate?.mapToDomainModel()