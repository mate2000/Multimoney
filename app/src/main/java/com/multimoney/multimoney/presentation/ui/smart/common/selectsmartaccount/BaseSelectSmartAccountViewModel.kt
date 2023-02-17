package com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNTS_ID_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.BaseSelectSmartAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.BaseSelectSmartAccountViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

open class BaseSelectSmartAccountViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        protected set

    // Stateless
    protected var user: String = ""
    protected var idBrand: Int = 0
    protected var idClient: String = ""
    protected var idLoanClient: String = ""
    protected var identification: String? = ""
    protected var smartAccountIDs: List<SmartAccountID>? = listOf()
    protected var selectedSmartAccount: SmartAccountID? = null
    protected var secondSmartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccountIDs = savedStateHandle.get<Array<SmartAccountID>>(SMART_ACCOUNTS_ID_LIST)?.toList()
    }

    protected fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    protected open fun onSelectSmartAccount(currencyType: CurrencyType) {
        if (currencyType == CurrencyType.Colon) {
                selectedSmartAccount = smartAccountIDs?.find { it.currencyID == CurrencyType.Colon.id }
                secondSmartAccount = smartAccountIDs?.find { it.currencyID == CurrencyType.Dollar.id }
        } else {
                selectedSmartAccount = smartAccountIDs?.find { it.currencyID == CurrencyType.Dollar.id }
                secondSmartAccount = smartAccountIDs?.find { it.currencyID == CurrencyType.Colon.id }
        }
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        val screenTitle: Int = R.string.payment_options_title_cr
    )

    open fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnSmartAccountSelected -> onSelectSmartAccount(uiEvent.currencyType)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnSmartAccountSelected(var currencyType: CurrencyType) : UIEvent()
    }
}
