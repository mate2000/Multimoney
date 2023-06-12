package com.multimoney.multimoney.presentation.util.catalog

/**
 * mapping the 'iconCode' id of each option returned from the backend, whenever a new option
 * gets added on the backend, a new object class needs to be represented as well.
 * This will work to identify the type of each option, a use case of this would be
 * to navigate to the selected option screen.
 * Note: This mapper has the ids of the options for the following countries: SV and CR.
 */
sealed class SourceIncomeOptionType(val iconId: Int, val name: String) {
    object MainSourceIncomeScreenType : SourceIncomeOptionType(0, "main_source_of_income_screen")

    /** SV options */
    object FormalSalariedSv : SourceIncomeOptionType(1, "formal_salaried_sv_option")
    object FreeLancer : SourceIncomeOptionType(2, "freelancer_option")
    object OwnBusiness : SourceIncomeOptionType(3, "own_business_option")
    object Retired : SourceIncomeOptionType(4, "retired_option")
    object OtherSV : SourceIncomeOptionType(5, "other_sv_option")

    /** CR options */
    object FormalSalariedCr : SourceIncomeOptionType(6, "formal_salaried_cr_option")
    object OwnBusinessOnPersonalBasis : SourceIncomeOptionType(7, "own_business_on_personal_basis_option")
    object OwnBusinessInPartnership : SourceIncomeOptionType(8, "own_business_in_partnership_option")
    object OtherCR : SourceIncomeOptionType(9, "other_cr_option")
}
