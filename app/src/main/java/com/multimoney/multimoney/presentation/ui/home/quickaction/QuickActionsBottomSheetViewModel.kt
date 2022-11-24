package com.multimoney.multimoney.presentation.ui.home.quickaction

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.QuickActionIconByType
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
            QuickActionIconByType.DISBURSE_AVAILABLE.iconId -> QuickActionIconByType.DISBURSE_AVAILABLE.iconResource
            QuickActionIconByType.PAY_FEE.iconId -> QuickActionIconByType.PAY_FEE.iconResource
            QuickActionIconByType.ACTIVATE_MM_VISA.iconId -> QuickActionIconByType.ACTIVATE_MM_VISA.iconResource
            QuickActionIconByType.SEE_MM_VISA.iconId -> QuickActionIconByType.SEE_MM_VISA.iconResource
            QuickActionIconByType.PAY_AT_BUSINESS.iconId -> QuickActionIconByType.PAY_AT_BUSINESS.iconResource
            QuickActionIconByType.SAVE_SMART.iconId -> QuickActionIconByType.SAVE_SMART.iconResource
            QuickActionIconByType.SEND_MONEY.iconId -> QuickActionIconByType.SEND_MONEY.iconResource
            QuickActionIconByType.BUY_CRYPTO.iconId -> QuickActionIconByType.BUY_CRYPTO.iconResource
            QuickActionIconByType.SELL_CRYPTO.iconId -> QuickActionIconByType.SELL_CRYPTO.iconResource
            QuickActionIconByType.RECEIVE_CRYPTO.iconId -> QuickActionIconByType.RECEIVE_CRYPTO.iconResource
            QuickActionIconByType.SEND_CRYPTO.iconId -> QuickActionIconByType.SEND_CRYPTO.iconResource
            else -> null
        }
    }
}

data class QuickActionDummy(val icon: Int?, val label: String)