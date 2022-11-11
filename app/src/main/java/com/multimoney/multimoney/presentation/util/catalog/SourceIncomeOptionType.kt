package com.multimoney.multimoney.presentation.util.catalog

/**
 * mapping the id of each option returned from the backend, whenever a new option
 * gets added on the backend, a new object class needs to be represented as well.
 * This will work to identify the type of each option, a use case of this would be
 * to navigate to the selected option screen.
 * Note: This mapper has the ids of the options for the following countries: SV and CR.
 */
sealed class SourceIncomeOptionType(val id: Int, val name: String) {
    object MainSourceIncomeScreenType : SourceIncomeOptionType(0, "main_source_of_income_screen")
    object FreeLancer : SourceIncomeOptionType(3, "freelancer_option")
    object OwnBusiness : SourceIncomeOptionType(4, "own_business_option")
    object Retired : SourceIncomeOptionType(5, "retired_option")
    object OtherSV : SourceIncomeOptionType(6, "other_sv_option")
    object FormalSalariedCr : SourceIncomeOptionType(7, "formal_salaried_cr_option")
    object OwnBusinessInPartnership : SourceIncomeOptionType(8, "own_business_in_partnership_option")
    object OwnBusinessOnPersonalBasis : SourceIncomeOptionType(9, "own_business_on_personal_basis_option")
    object OtherCR : SourceIncomeOptionType(10, "other_cr_option")
    object FormalSalariedSv : SourceIncomeOptionType(11, "formal_salaried_sv_option")
}
