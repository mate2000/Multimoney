package com.multimoney.multimoney.presentation.ui.credit.util

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.CreditInfoQuestion
import javax.inject.Inject

class SaveCreditStepsHelper @Inject constructor() {

    private var inputTextInfoList: List<CreditCatalog?>? = listOf<CreditCatalog>()
    var creditFlowData: MutableList<CreditInfoQuestion> = mutableListOf()

    fun start(screenConfig: List<CreditCatalog?>?) {
        inputTextInfoList = screenConfig
    }

    private fun saveScreenQuestionData(creditInfoQuestion: CreditInfoQuestion) {
        creditFlowData.add(creditInfoQuestion)
    }

    fun saveStepOne(
        user: String?,
        monthlyIncomeValue: String,
        occupation: CreditCatalog?,
        occupationSelected: CreditCatalogOption?
    ) {
        val monthlyIncomeQuestion = inputTextInfoList?.find { it?.description == SALARY }
        saveScreenQuestionData(textQuestion(user, monthlyIncomeValue, monthlyIncomeQuestion))

        saveScreenQuestionData(selectionQuestion(user, occupation, occupationSelected))
    }

    fun saveStepTwo(user: String?, companyName: String, startedJobDate: String, companyPhone: String) {
        val companyNameQuestion = inputTextInfoList?.find { it?.description == COMPANY_NAME }
        val companyStartedJobDateQuestion = inputTextInfoList?.find { it?.description == STARTED_JOB_DATE }
        val companyPhoneQuestion = inputTextInfoList?.find { it?.description == COMPANY_PHONE }

        val creditInfoQuestionCompanyName = textQuestion(user, companyName, companyNameQuestion)
        saveScreenQuestionData(creditInfoQuestionCompanyName)

        val creditInfoQuestionStartedJobDate = textQuestion(user, startedJobDate, companyStartedJobDateQuestion)
        saveScreenQuestionData(creditInfoQuestionStartedJobDate)

        val creditInfoQuestionCompanyPhone = textQuestion(user, companyPhone, companyPhoneQuestion)
        saveScreenQuestionData(creditInfoQuestionCompanyPhone)
    }

    fun saveStepThree(
        user: String?,
        companyProvince: CreditCatalog,
        companyProvinceSelected: CreditCatalogOption,
        companyCanton: CreditCatalog,
        companyCantonSelected: CreditCatalogOption,
        companyDistrict: CreditCatalog,
        companyDistrictSelected: CreditCatalogOption,
        companyAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, companyProvince, companyProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, companyCanton, companyCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, companyDistrict, companyDistrictSelected))

        val creditInfoQuestionCompanyAddress = inputTextInfoList?.find { it?.description == COMPANY_ADDRESS }
        saveScreenQuestionData(textQuestion(user, companyAddressValue, creditInfoQuestionCompanyAddress))
    }

    fun saveStepThreeSV(
        user: String?,
        companyProvince: CreditCatalog,
        companyProvinceSelected: CreditCatalogOption,
        companyCanton: CreditCatalog,
        companyCantonSelected: CreditCatalogOption,
        companyAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, companyProvince, companyProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, companyCanton, companyCantonSelected))

        val creditInfoQuestionCompanyAddress = inputTextInfoList?.find { it?.description == COMPANY_ADDRESS }
        saveScreenQuestionData(textQuestion(user, companyAddressValue, creditInfoQuestionCompanyAddress))
    }

    fun saveStepFourGT(
        user: String?,
        homeProvince: CreditCatalog,
        homeProvinceSelected: CreditCatalogOption,
        homeCanton: CreditCatalog,
        homeCantonSelected: CreditCatalogOption,
        homeDistrict: CreditCatalog,
        homeDistrictSelected: CreditCatalogOption,
        homeAddressValue: String,
        homePhoneValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, homeDistrict, homeDistrictSelected))

        val creditInfoQuestionHomeAddress = inputTextInfoList?.find { it?.description == HOME_ADDRESS }
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))

        // todo uncomment this logic when the backend change the configuration and this question
//        val creditInfoQuestionHomePhone = inputTextInfoList.find { it.description == HOME_PHONE }
//        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomePhone))
    }

    fun saveStepFourSV(
        user: String?,
        homeProvince: CreditCatalog,
        homeProvinceSelected: CreditCatalogOption,
        homeCanton: CreditCatalog,
        homeCantonSelected: CreditCatalogOption,
        homeAddressValue: String,
        homePhoneValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))

        val creditInfoQuestionHomeAddress = inputTextInfoList?.find { it?.description == HOME_ADDRESS }
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))

        // todo uncomment this logic when the backend change the configuration and this question
//        val creditInfoQuestionHomePhone = inputTextInfoList.find { it.description == HOME_PHONE }
//        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomePhone))
    }

    fun saveStepFourCR(
        user: String?,
        homeProvince: CreditCatalog,
        homeProvinceSelected: CreditCatalogOption,
        homeCanton: CreditCatalog,
        homeCantonSelected: CreditCatalogOption,
        homeDistrict: CreditCatalog,
        homeDistrictSelected: CreditCatalogOption,
        homeAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, homeDistrict, homeDistrictSelected))

        val creditInfoQuestionHomeAddress = inputTextInfoList?.find { it?.description == HOME_ADDRESS }
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))
    }

    private fun textQuestion(user: String?, value: String, textQuestionData: CreditCatalog?): CreditInfoQuestion {
        return CreditInfoQuestion(
            idQuestionRequestCredit = textQuestionData?.fkQuestion,
            idOptionQuestionRequestCredit = textQuestionData?.pkQuestionOption,
            createUser = user,
            updateUser = user,
            identificator = textQuestionData?.pkCatalog,
            value = value,
            controlType = textQuestionData?.controlType,
            isCoreCatalogue = textQuestionData?.isCoreCatalog,
            isBranchOfficeCatalogue = textQuestionData?.isCatalogBrandOffice,
            useValue = textQuestionData?.useValue,
            maximumAmount = textQuestionData?.maximumAmount,
            description = "",
            valueCatalogue = "",
            idIdentificatorCatalogue = ""
        )
    }

    private fun selectionQuestion(
        user: String?,
        selectionQuestionData: CreditCatalog?,
        selectionQuestionOption: CreditCatalogOption?
    ): CreditInfoQuestion {
        return CreditInfoQuestion(
            idQuestionRequestCredit = selectionQuestionData?.fkQuestion,
            idOptionQuestionRequestCredit = selectionQuestionData?.pkQuestionOption,
            createUser = user,
            updateUser = user,
            identificator = selectionQuestionData?.pkQuestionOption.toString(),
            value = "",
            controlType = selectionQuestionData?.controlType,
            isCoreCatalogue = selectionQuestionData?.isCoreCatalog,
            isBranchOfficeCatalogue = selectionQuestionData?.isCatalogBrandOffice,
            useValue = selectionQuestionData?.useValue,
            maximumAmount = selectionQuestionData?.maximumAmount,
            description = selectionQuestionOption?.description,
            valueCatalogue = selectionQuestionData?.valueCatalog,
            idIdentificatorCatalogue = selectionQuestionOption?.id
        )
    }

    companion object {
        const val SALARY = "Ingreso Mensual"
        const val COMPANY_NAME = "Nombre Empresa"
        const val STARTED_JOB_DATE = "Fecha Ingreso Laboral Actual"
        const val COMPANY_PHONE = "Teléfono del trabajo"
        const val COMPANY_ADDRESS = "Detalle Dirección Empresa"
        const val HOME_ADDRESS = "Detalle Dirección Casa"
        const val HOME_PHONE = "HOME_PHONE"
    }
}