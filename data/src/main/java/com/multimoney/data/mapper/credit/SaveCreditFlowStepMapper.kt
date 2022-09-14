package com.multimoney.data.mapper.credit

import com.apollographql.apollo3.api.Optional
import com.multimoney.data.networking.credit.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.credit.apollomodel.type.QuestionsDataListInput
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.SaveCreditFlowStep

fun CreditInfoQuestion.mapToApolloModel() = QuestionsDataListInput(
    idQuestionRequestCredit = idQuestionRequestCredit ?: 0,
    idOptionQuestionRequestCredit = Optional.Present(idOptionQuestionRequestCredit),
    createUser = Optional.Present(createUser),
    updateUser = Optional.Present(updateUser),
    identificator = Optional.Present(identificator),
    value = Optional.Present(value),
    active = Optional.Present(active),
    controlType = Optional.Present(controlType),
    isCoreCatalogue = Optional.Present(isCoreCatalogue),
    isBranchOfficeCatalogue = Optional.Present(isBranchOfficeCatalogue),
    useValue = Optional.Present(useValue),
    maximumAmount = Optional.Present(maximumAmount),
    description = Optional.Present(description),
    valueCatalogue = Optional.Present(valueCatalogue),
    idIdentificatorCatalogue = Optional.Present(idIdentificatorCatalogue),
)

private fun SaveCreditFlowInputMutation.SaveCreditFlowStep.mapToDomainModel() = SaveCreditFlowStep(
    messageInfo = messageInfo,
    status = status,
    result = result
)

fun SaveCreditFlowInputMutation.Data.mapToDomainModel() = saveCreditFlowStep?.mapToDomainModel()