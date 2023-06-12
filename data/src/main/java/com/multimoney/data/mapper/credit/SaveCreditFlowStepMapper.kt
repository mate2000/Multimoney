package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.graphql.apollomodel.type.QuestionsDataInput
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.SaveCreditFlowStep

fun CreditInfoQuestion.mapToApolloModel() = QuestionsDataInput(
    idQuestionRequestCredit = idQuestionRequestCredit ?: 0,
    idOptionQuestionRequestCredit = idOptionQuestionRequestCredit ?: 0,
    createUser = createUser ?: "",
    updateUser = updateUser ?: "",
    identificator = identificator ?: "",
    value = value,
    active = active,
    controlType = controlType ?: "",
    isCoreCatalogue = isCoreCatalogue ?: false,
    isBranchOfficeCatalogue = isBranchOfficeCatalogue ?: false,
    useValue = useValue ?: false,
    maximumAmount = maximumAmount ?: "",
    description = description ?: "",
    valueCatalogue = valueCatalogue ?: "",
    idIdentificatorCatalogue = idIdentificatorCatalogue ?: ""
)

private fun SaveCreditFlowInputMutation.SaveCreditFlowStep.mapToDomainModel() = SaveCreditFlowStep(
    messageInfo = messageInfo,
    status = status,
    result = result
)

fun SaveCreditFlowInputMutation.Data.mapToDomainModel() = saveCreditFlowStep.mapToDomainModel()
