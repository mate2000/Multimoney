package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.StepByStepQuery
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.StepByStep

private fun StepByStepQuery.StepbyStep.mapToDomainModel() = StepByStep(
    knownFor,
    birthdate.toString(),
    idGenre.toString().toInt(),
    strGenre,
    idMaritalStatus.toString().toInt(),
    strMaritalStatus,
    idProfessionType.toString().toInt(),
    stridProfessionType,
    idNationality = idNationalidad.toString().toInt(),
    strNationality,
    expirationDate.toString(),
    dateOfIssue.toString(),
    placeOfIssue,
    strPlaceOfIssue = strplaceOfIssue,
    idAddressLevel1.toString().toInt(),
    strAddressLevel1,
    idAddressLevel2.toString().toInt(),
    strAddressLevel2,
    idAddressLevel3.toString().toInt(),
    strAddressLevel3,
    addressDetail,
    idEconomicActivity.toString().toInt(),
    strIdEconomicActivity,
    legalID,
    nameCompany,
    specifiesIncomeSource,
    institutionalPesion = institucionPesion,
    aboutCompany,
    income.toString().toFloat(),
    fullJobAddress,
    entrepreneurship,
    beneficiary?.map { it.mapToDomainModel() },
    isActivityOfArt15,
    isUSCitizen,
    isPEP,
    isUSTaxPayer,
    isTaxPayer,
    idOrigenCountry = idOrigenCountry.toString().toInt(),
    strOrigenCountry,
    currentStep,
    idRequest.toString().toInt(),
    identification,
    dateCreation.toString(),
    statusRequest,
    statusFirm,
    statusOnfido,
    idRequestSys = idRequestSysde.toString().toInt()
)

private fun StepByStepQuery.Beneficiary.mapToDomainModel() =
    Beneficiary(fullName, relationship.toString().toInt(), strRelationship, allocationPercentage)

fun StepByStepQuery.Data.mapToDomainModel() = stepbyStep.mapToDomainModel()
