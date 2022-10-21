package com.multimoney.multimoney.presentation.ui.test.smart.data

import com.multimoney.domain.model.smart.origin.sourceofincome.SourceOfIncome

/**
 * Mock class to store temporary list to be shown when creating UI elements
 */
object MockDataSource {
    val sourceOfIncomeList = listOf(
        SourceOfIncome(1, "Asalariado"),
        SourceOfIncome(2, "Negocio propio a titulo personal"),
        SourceOfIncome(3, "Comerciante o propietario"),
        SourceOfIncome(4, "Jubilado"),
        SourceOfIncome(5, "Otro")
    )
}
