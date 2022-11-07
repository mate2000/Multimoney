package com.multimoney.multimoney.presentation.ui.test.smart.data

import com.multimoney.domain.model.accountsmart.GeneralEconomicActivity

/**
 * Mock class to store temporary list to be shown when creating UI elements
 */
object MockDataSource {
    val generalEconomicActivityLists = listOf(
        GeneralEconomicActivity(1, 1, "Asalariado"),
        GeneralEconomicActivity(2, 2, "Negocio propio a titulo personal"),
        GeneralEconomicActivity(3, 3, "Comerciante o propietario"),
        GeneralEconomicActivity(4, 4, "Jubilado"),
        GeneralEconomicActivity(5, 5, "Otro")
    )
}
