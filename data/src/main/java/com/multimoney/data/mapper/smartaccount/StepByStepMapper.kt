package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.StepByStepQuery
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.StepByStep

private fun StepByStepQuery.StepbyStep.mapToDomainModel() = StepByStep(
    knownFor = knownFor,
    birthdate = birthdate?.toString(),
    idGenre = idGenre?.toString()?.toInt(),
    strGenre = strGenre,
    idMaritalStatus = idMaritalStatus?.toString()?.toInt(),
    strMaritalStatus = strMaritalStatus,
    idProfessionType = idProfessionType?.toString()?.toInt(),
    stringProfessionType = stridProfessionType,
    idNationality = idNationalidad?.toString()?.toInt(),
    strNationality = strNationality,
    expirationDate = expirationDate?.toString(),
    dateOfIssue = dateOfIssue?.toString(),
    placeOfIssue = placeOfIssue,
    strPlaceOfIssue = strplaceOfIssue,
    idAddressLevel1 = idAddressLevel1?.toString()?.toInt(),
    strAddressLevel1 = strAddressLevel1,
    idAddressLevel2 = idAddressLevel2?.toString()?.toInt(),
    strAddressLevel2 = strAddressLevel2,
    idAddressLevel3 = idAddressLevel3?.toString()?.toInt(),
    strAddressLevel3 = strAddressLevel3,
    addressDetail = addressDetail,
    idEconomicActivity = idEconomicActivity.toString().toInt(),
    strIdEconomicActivity = strIdEconomicActivity,
    legalID = legalID,
    nameCompany = nameCompany,
    positionJob = positionJob,
    specifiesIncomeSource = specifiesIncomeSource,
    institutionalPesion = institucionPesion,
    idJobLevel1 = idJobLevel1?.toString()?.toLong(),
    idJobLevel2 = idJobLevel2?.toString()?.toLong(),
    idJobLevel3 = idJobLevel3?.toString()?.toLong(),
    aboutCompany = aboutCompany,
    income = income?.toString()?.toFloat(),
    fullJobAddress = fullJobAddress,
    entrepreneurship = entrepreneurship,
    beneficiary = beneficiary?.map { it.mapToDomainModel() },
    isActivityOfArt15 = isActivityOfArt15,
    isUSCitizen = isUSCitizen,
    isPEP = isPEP,
    isUSTaxPayer = isUSTaxPayer,
    isTaxPayer = isTaxPayer,
    idOrigenCountry = idOrigenCountry?.toString()?.toInt(),
    strOrigenCountry = strOrigenCountry,
    currentStep = currentStep,
    idRequest = idRequest?.toString()?.toInt(),
    identification = identification,
    dateCreation = dateCreation?.toString(),
    statusRequest = statusRequest,
    statusFirm = statusFirm,
    statusOnfido = statusOnfido,
    idRequestSys = idRequestSysde?.toString()?.toInt()
)

private fun StepByStepQuery.Beneficiary.mapToDomainModel() =
    Beneficiary(fullName, relationship.toString().toInt(), strRelationship, allocationPercentage)

fun StepByStepQuery.Data.mapToDomainModel() = stepbyStep.mapToDomainModel()
