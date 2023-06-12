package com.multimoney.multimoney.presentation.util.catalog

/**
 * cast the specific Economic Activity (source of income flow)
 * for each country SV and CR.
 * @param iconId to determine the iconType
 * @param id to determine the view that has to be open
 */
sealed class SourceIncomeType(val iconId: Int, val id: Int) {
    object Salaried : SourceIncomeType(1, 11)
    object FreeLancer : SourceIncomeType(2, 3)
    object OwnBusiness : SourceIncomeType(3, 4)
    object Retired : SourceIncomeType(4, 5)
    object FormalSalaried : SourceIncomeType(6, 7)
    object OwnBusinessOnPersonalBasis : SourceIncomeType(7, 9)
    object OwnBusinessInPartnership : SourceIncomeType(8, 8)
    object Other : SourceIncomeType(5 or 9, 6 or 10)
}
