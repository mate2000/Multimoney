package com.multimoney.multimoney.presentation.util.catalog

/**
 * cast the specific Economic Activity (source of income flow)
 * for each country SV and CR.
 * @param iconId to determine the iconType
 * @param id to determine the view that has to be open
 */
sealed class SourceIncomeIconType(val iconId: Int, val id: Int) {
    object Salaried : SourceIncomeIconType(1, 11)
    object FreeLancer : SourceIncomeIconType(2, 3)
    object OwnBusiness : SourceIncomeIconType(3, 4)
    object Retired : SourceIncomeIconType(4, 5)
    object FormalSalaried : SourceIncomeIconType(6, 7)
    object OwnBusinessOnPersonalBasis : SourceIncomeIconType(7, 9)
    object OwnBusinessInPartnership : SourceIncomeIconType(8, 8)
    object Other : SourceIncomeIconType(5 or 9, 6 or 10)
}
