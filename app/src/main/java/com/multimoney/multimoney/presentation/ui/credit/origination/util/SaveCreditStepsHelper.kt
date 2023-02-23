package com.multimoney.multimoney.presentation.ui.credit.origination.util

import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.CreditInfoQuestion
import javax.inject.Inject

class SaveCreditStepsHelper @Inject constructor() {

    var inputTextInfoList: List<CreditCatalog?>? = listOf<CreditCatalog>()
    var creditFlowData: MutableList<CreditInfoQuestion> = mutableListOf()

    fun start(screenConfig: List<CreditCatalog?>?) {
        inputTextInfoList = screenConfig
    }

    private fun saveScreenQuestionData(creditInfoQuestion: CreditInfoQuestion) {
        creditFlowData.add(creditInfoQuestion)
    }

    private fun getScreenConfigQuestion(description: String, screenValue: String): CreditCatalog? {
        return inputTextInfoList?.find { it?.description == description }?.apply { value = screenValue }
    }

    fun saveStepOne(
        user: String?,
        bank: CreditCatalog?,
        bankAccountSelected: CreditCatalogOption?,
        accountType: String,
        accountNumber: String,
        idIdentificatorCatalogue: String
    ) {
        val accountNumberQuestion = getScreenConfigQuestion(ACCOUNT_NUMBER, accountNumber)
        val accountTypeQuestion = getScreenConfigQuestion(ACCOUNT_TYPE, accountType)

        saveScreenQuestionData(selectionQuestion(user, bank, bankAccountSelected))

        val creditInfoQuestionAccountType =
            textByDropdownQuestion(user, accountType, accountTypeQuestion, idIdentificatorCatalogue)
        saveScreenQuestionData(creditInfoQuestionAccountType)

        val creditInfoQuestionAccountNumber = textQuestion(user, accountNumber, accountNumberQuestion)
        saveScreenQuestionData(creditInfoQuestionAccountNumber)
    }

    fun saveStepOneCR(user: String?, ibanNumber: String) {
        val ibanNumberQuestion = getScreenConfigQuestion(ACCOUNT_NUMBER, ibanNumber)

        val creditInfoQuestionAccountNumber = textQuestion(user, ibanNumber, ibanNumberQuestion)
        saveScreenQuestionData(creditInfoQuestionAccountNumber)
    }

    fun saveStepTwo(
        user: String?,
        idBrand: Int,
        monthlyIncomeValue: String,
        profession: CreditCatalog?,
        professionSelected: CreditCatalogOption?,
        occupation: CreditCatalog?,
        occupationSelected: CreditCatalogOption?,
        duiEmissionPlace: CreditCatalog?,
        duiEmissionPlaceSelected: CreditCatalogOption?,
        duiEmissionDateValue: String,
        duiExpirationDateValue: String,
        companyName: String,
        companyStartDate: String,
        companyPhoneNumber: String,
        isCrosseling: Boolean
    ) {
        val monthlyIncomeQuestion = getScreenConfigQuestion(SALARY, monthlyIncomeValue)
        saveScreenQuestionData(textQuestion(user, monthlyIncomeValue, monthlyIncomeQuestion))

        saveScreenQuestionData(selectionQuestion(user, profession, professionSelected))
        if (idBrand == Brand.CostaRica.id) {
            saveScreenQuestionData(selectionQuestion(user, occupation, occupationSelected))
        }

        if (idBrand == Brand.ElSalvador.id) {
            val duiEmissionDateQuestion = getScreenConfigQuestion(DUI_EMISSION_DATE, duiEmissionDateValue)
            saveScreenQuestionData(textQuestion(user, duiEmissionDateValue, duiEmissionDateQuestion))
            val duiExpirationDateQuestion = getScreenConfigQuestion(DUI_EXPIRATION_DATE, duiExpirationDateValue)
            saveScreenQuestionData(textQuestion(user, duiExpirationDateValue, duiExpirationDateQuestion))
            saveScreenQuestionData(selectionQuestion(user, duiEmissionPlace, duiEmissionPlaceSelected))

            if (isCrosseling) {
                val companyNameQuestion = getScreenConfigQuestion(COMPANY_NAME, companyName)
                saveScreenQuestionData(textQuestion(user, companyName, companyNameQuestion))

                val companyStartedJobDateQuestion = getScreenConfigQuestion(STARTED_JOB_DATE, companyStartDate)
                saveScreenQuestionData(textQuestion(user, companyStartDate, companyStartedJobDateQuestion))

                val companyPhoneQuestion = getScreenConfigQuestion(COMPANY_PHONE, companyPhoneNumber)
                saveScreenQuestionData(textQuestion(user, companyPhoneNumber, companyPhoneQuestion))
            }
        }
    }

    fun saveStepThree(
        idBrand: Int,
        user: String?,
        companyName: String,
        startedJobDate: String,
        companyPhone: String,
        dateFirstJob: String,
        isCrosseling: Boolean,
        profession: CreditCatalog?,
        professionSelected: CreditCatalogOption?
    ) {
        val companyNameQuestion = getScreenConfigQuestion(COMPANY_NAME, companyName)
        val companyStartedJobDateQuestion = getScreenConfigQuestion(STARTED_JOB_DATE, startedJobDate)
        val companyStartedFirstJobDateQuestion = getScreenConfigQuestion(STARTED_FIRST_JOB_DATE, startedJobDate)
        val companyPhoneQuestion = getScreenConfigQuestion(COMPANY_PHONE, companyPhone)

        val creditInfoQuestionCompanyName = textQuestion(user, companyName, companyNameQuestion)
        saveScreenQuestionData(creditInfoQuestionCompanyName)

        val creditInfoQuestionStartedJobDate = textQuestion(user, startedJobDate, companyStartedJobDateQuestion)
        saveScreenQuestionData(creditInfoQuestionStartedJobDate)

        val creditInfoQuestionCompanyPhone = textQuestion(user, companyPhone, companyPhoneQuestion)
        saveScreenQuestionData(creditInfoQuestionCompanyPhone)

        if (isCrosseling) {
            saveScreenQuestionData(selectionQuestion(user, profession, professionSelected))
        }

        if (idBrand == Brand.CostaRica.id) {
            val creditInfoQuestionStartedFirstJobDate =
                textQuestion(user, dateFirstJob, companyStartedFirstJobDateQuestion)
            saveScreenQuestionData(creditInfoQuestionStartedFirstJobDate)
        }
    }

    fun saveStepFour(
        user: String?,
        companyProvince: CreditCatalog?,
        companyProvinceSelected: CreditCatalogOption?,
        companyCanton: CreditCatalog?,
        companyCantonSelected: CreditCatalogOption?,
        companyDistrict: CreditCatalog?,
        companyDistrictSelected: CreditCatalogOption?,
        companyAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, companyProvince, companyProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, companyCanton, companyCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, companyDistrict, companyDistrictSelected))

        val creditInfoQuestionCompanyAddress = getScreenConfigQuestion(COMPANY_ADDRESS, companyAddressValue)
        saveScreenQuestionData(textQuestion(user, companyAddressValue, creditInfoQuestionCompanyAddress))
    }

    fun saveStepFourSV(
        user: String?,
        companyProvince: CreditCatalog?,
        companyProvinceSelected: CreditCatalogOption?,
        companyCanton: CreditCatalog?,
        companyCantonSelected: CreditCatalogOption?,
        companyAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, companyProvince, companyProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, companyCanton, companyCantonSelected))

        val creditInfoQuestionCompanyAddress = getScreenConfigQuestion(COMPANY_ADDRESS, companyAddressValue)
        saveScreenQuestionData(textQuestion(user, companyAddressValue, creditInfoQuestionCompanyAddress))
    }

    fun saveStepFiveGT(
        user: String?,
        homeProvince: CreditCatalog?,
        homeProvinceSelected: CreditCatalogOption?,
        homeCanton: CreditCatalog?,
        homeCantonSelected: CreditCatalogOption?,
        homeDistrict: CreditCatalog?,
        homeDistrictSelected: CreditCatalogOption?,
        homeAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, homeDistrict, homeDistrictSelected))

        val creditInfoQuestionHomeAddress = getScreenConfigQuestion(HOME_ADDRESS, homeAddressValue)
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))
    }

    fun saveStepFiveSV(
        user: String?,
        homeProvince: CreditCatalog?,
        homeProvinceSelected: CreditCatalogOption?,
        homeCanton: CreditCatalog?,
        homeCantonSelected: CreditCatalogOption?,
        homeAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))

        val creditInfoQuestionHomeAddress = getScreenConfigQuestion(HOME_ADDRESS, homeAddressValue)
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))
    }

    fun saveStepFiveCR(
        user: String?,
        homeProvince: CreditCatalog?,
        homeProvinceSelected: CreditCatalogOption?,
        homeCanton: CreditCatalog?,
        homeCantonSelected: CreditCatalogOption?,
        homeDistrict: CreditCatalog?,
        homeDistrictSelected: CreditCatalogOption?,
        homeAddressValue: String
    ) {
        saveScreenQuestionData(selectionQuestion(user, homeProvince, homeProvinceSelected))
        saveScreenQuestionData(selectionQuestion(user, homeCanton, homeCantonSelected))
        saveScreenQuestionData(selectionQuestion(user, homeDistrict, homeDistrictSelected))

        val creditInfoQuestionHomeAddress = getScreenConfigQuestion(HOME_ADDRESS, homeAddressValue)
        saveScreenQuestionData(textQuestion(user, homeAddressValue, creditInfoQuestionHomeAddress))
    }

    fun saveStepSix(
        user: String?,
        pep: String
    ) {
        val pepQuestion = getScreenConfigQuestion(POLITICALLY_EXPOSED_PERSON, pep)
        val creditInfoQuestionPEP = textQuestion(user, pep, pepQuestion)
        saveScreenQuestionData(creditInfoQuestionPEP)
    }

    fun saveStepSixCR(
        user: String?,
        article15: String,
        pep: String,
        taxPayerUSA: String,
        taxPayerExternal: String
    ) {
        val article15Question = getScreenConfigQuestion(ARTICLE_15, article15)
        val pepQuestion = getScreenConfigQuestion(POLITICALLY_EXPOSED_PERSON, pep)
        val taxPayerUSAQuestion = getScreenConfigQuestion(TAX_PAYER_USA, taxPayerUSA)
        val taxPayerExternalQuestion = getScreenConfigQuestion(TAX_PAYER_EXTERNAL, taxPayerExternal)

        val creditInfoQuestionArticle15 = textQuestion(user, article15, article15Question)
        saveScreenQuestionData(creditInfoQuestionArticle15)

        val creditInfoQuestionPEP = textQuestion(user, pep, pepQuestion)
        saveScreenQuestionData(creditInfoQuestionPEP)

        val creditInfoQuestionTaxPayerUSA = textQuestion(user, taxPayerUSA, taxPayerUSAQuestion)
        saveScreenQuestionData(creditInfoQuestionTaxPayerUSA)

        val creditInfoQuestionTaxPayerExternal = textQuestion(user, taxPayerExternal, taxPayerExternalQuestion)
        saveScreenQuestionData(creditInfoQuestionTaxPayerExternal)
    }

    fun saveAdditionalQuestionsForNonPreApprovedFlow(
        user: String?,
        birthDate: String,
        monthlyIncomeValue: String,
        employmentSituation: CreditCatalog?,
        employmentSituationSelected: CreditCatalogOption?
    ) {
        val monthlyIncomeQuestion = getScreenConfigQuestion(SALARY, monthlyIncomeValue)
        saveScreenQuestionData(
            textQuestion(
                user = user,
                value = monthlyIncomeValue,
                textQuestionData = monthlyIncomeQuestion
            )
        )

        val birthDateQuestion = getScreenConfigQuestion(BIRTH_DATE, birthDate)
        saveScreenQuestionData(
            textQuestion(
                user = user,
                value = birthDate,
                textQuestionData = birthDateQuestion
            )
        )

        saveScreenQuestionData(
            selectionQuestion(
                user = user,
                selectionQuestionData = employmentSituation,
                selectionQuestionOption = employmentSituationSelected
            )
        )
    }

    /**
     * This function is use to save all the questions that the value will be got from a textField
     */
    private fun textQuestion(user: String?, value: String, textQuestionData: CreditCatalog?): CreditInfoQuestion {
        return CreditInfoQuestion(
            idQuestionRequestCredit = textQuestionData?.fkQuestion,
            idOptionQuestionRequestCredit = textQuestionData?.pkQuestionOption,
            createUser = user,
            updateUser = user,
            identificator = textQuestionData?.pkCatalog ?: "",
            value = value,
            controlType = textQuestionData?.controlType,
            isCoreCatalogue = textQuestionData?.isCoreCatalog,
            isBranchOfficeCatalogue = textQuestionData?.isCatalogBrandOffice,
            useValue = textQuestionData?.useValue,
            maximumAmount = textQuestionData?.maximumAmount ?: "",
            description = textQuestionData?.description ?: "",
            valueCatalogue = "",
            idIdentificatorCatalogue = ""
        )
    }

    /**
     * This function is use to save a question that the value comes from a dropdown but will be save in the screen config.
     * when the type of question in screenConfig is ComboBoxEvent uses this function to save the value.
     */
    private fun textByDropdownQuestion(
        user: String?,
        value: String,
        textQuestionData: CreditCatalog?,
        idIdentificatorCatalogue: String
    ): CreditInfoQuestion {
        return CreditInfoQuestion(
            idQuestionRequestCredit = textQuestionData?.fkQuestion,
            idOptionQuestionRequestCredit = textQuestionData?.pkQuestionOption,
            createUser = user,
            updateUser = user,
            identificator = textQuestionData?.pkCatalog ?: "",
            value = value,
            controlType = textQuestionData?.controlType,
            isCoreCatalogue = textQuestionData?.isCoreCatalog,
            isBranchOfficeCatalogue = textQuestionData?.isCatalogBrandOffice,
            useValue = textQuestionData?.useValue,
            maximumAmount = textQuestionData?.maximumAmount ?: "",
            description = value,
            valueCatalogue = "",
            idIdentificatorCatalogue = idIdentificatorCatalogue
        )
    }

    /**
     *This function is use to save all the questions that the value will be got from a DropDowns,
     * powered from a list of Credit CatalogOption.
     */
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
            maximumAmount = selectionQuestionData?.maximumAmount ?: "",
            description = selectionQuestionOption?.description ?: "",
            valueCatalogue = if (selectionQuestionData?.isCatalogBrandOffice ?: false.and(
                    selectionQuestionData?.useValue == true
                )
            ) {
                selectionQuestionOption?.valueCatalog ?: ""
            } else {
                ""
            },
            idIdentificatorCatalogue = if (selectionQuestionData?.isCatalogBrandOffice?.not()
                ?: false.or(
                        selectionQuestionData?.useValue?.not() == true
                    )
            ) {
                selectionQuestionOption?.pkCatalog ?: "0"
            } else {
                "0"
            }
        )
    }

    companion object {
        const val ACCOUNT_NUMBER = "Número de cuenta"
        const val ACCOUNT_TYPE = "Tipo Cuenta"
        const val SALARY = "Ingreso Mensual"
        const val COMPANY_NAME = "Nombre Empresa"
        const val STARTED_JOB_DATE = "Fecha Ingreso Laboral Actual"
        const val STARTED_FIRST_JOB_DATE = "Fecha Inicio Actividad Laboral"
        const val COMPANY_PHONE = "Teléfono del trabajo"
        const val COMPANY_ADDRESS = "Detalle Dirección Empresa"
        const val HOME_ADDRESS = "Detalle Dirección Casa"
        const val HOME_PHONE = "HOME_PHONE"
        const val ARTICLE_15 = "ART15"
        const val POLITICALLY_EXPOSED_PERSON = "PEP"
        const val TAX_PAYER_USA = "TAX_PAYER_USA"
        const val TAX_PAYER_EXTERNAL = "TAX_PAYER_EXTERNAL"
        const val BIRTH_DATE = "Fecha Nacimiento"
        const val DUI_EMISSION_DATE = "Fecha Emision DUI"
        const val DUI_EXPIRATION_DATE = "Fecha Expiración DUI"
    }
}
