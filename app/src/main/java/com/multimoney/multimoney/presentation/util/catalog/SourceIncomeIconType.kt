package com.multimoney.multimoney.presentation.util.catalog

/**
 * cast the specific Economic Activity (source of income flow)
 * for each country SV and CR.
 * @param iconId to determine the iconType
 */
sealed class SourceIncomeIconType(val iconId: Int) {
    object Salaried : SourceIncomeIconType(1)
    object FreeLancer : SourceIncomeIconType(2)
    object OwnBusiness : SourceIncomeIconType(3)
    object Retired : SourceIncomeIconType(4)
    object FormalSalaried : SourceIncomeIconType(6)
    object OwnBusinessOnPersonalBasis : SourceIncomeIconType(7)
    object OwnBusinessInPartnership : SourceIncomeIconType(8)
    object Other : SourceIncomeIconType(5 or 9)
}
