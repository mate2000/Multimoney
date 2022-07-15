package com.multimoney.multimoney.presentation.ui.home.product

import com.multimoney.domain.model.credit.CreditOfferAndTip
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor() : BaseViewModel() {

    val hasCredit = true

    fun getCreditOfferAndTips(): List<CreditOfferAndTip> {
        return listOf(
            CreditOfferAndTip(
                "1",
                "Ahorra Smart",
                "La mejor tasa del 3.5% anual",
                "Solicitar",
                "",
                ""
            ),
            CreditOfferAndTip(
                "1",
                "Ahorra Smart",
                "La mejor tasa del 3.5% anual",
                "Solicitar",
                "",
                ""
            )
        )
    }
}