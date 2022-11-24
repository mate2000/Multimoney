package com.multimoney.multimoney.presentation.ui.home.quickaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.NfcHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class QuickActionsBottomSheetViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val nfcHelper: NfcHelper,
    private val queryGetQuickActionsUseCase : QueryGetQuickActionsUseCase,
    ) : BaseViewModel(true) {

    var quickActionUiState by mutableStateOf(QuickActionUiState())

    // Stateless
    var validateUserStatus: ValidateUserStatus? = null
    var balance: Balance? = null
    var idBrand: String = ""
    var pkUser: String = ""
    var identification: String = ""

    init {
        getIdBrand()
    }

    private fun getIdBrand() = executeUseCase {
        quickActionUiState =
            quickActionUiState.copy(idBrand = dataStorePreferences.getIdBrand().first())
    }

    data class QuickActionUiState(
        //quick actions fields
        var idBrand: String = "0"
    )

    fun isNfcAvailable() = nfcHelper.isNfcSupported()

    fun getSmartQuickAction(label: String, iconId: String): QuickActionDummy {
        return QuickActionDummy(getQuickActionIconByType(iconId), label)
    }

    private fun getQuickActionIconByType(iconId : String) : Int? {
        return when (iconId){
            "1" -> R.drawable.ic_quick_action_calendar
            "2" -> R.drawable.ic_quick_action_money
            "3" -> R.drawable.ic_quick_action_visa_logo
            "4" -> R.drawable.ic_quick_action_view
            "5" -> R.drawable.ic_quick_action_pay
            "6" -> R.drawable.ic_saving_smart
            "7" -> R.drawable.ic_send_money
            "8" -> R.drawable.ic_buy_crypto
            "9" -> R.drawable.ic_quick_actionsell_crypto
            "10" -> R.drawable.ic_quick_action_receive_crypto
            "11" -> R.drawable.ic_quick_action_send_crypto
            else -> null
        }
    }
}

data class QuickActionDummy(val icon: Int?, val label: String)