package com.multimoney.multimoney.presentation.ui.home.quickaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickActionsBottomSheetViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    var quickActionUiState by mutableStateOf(QuickActionUiState())

    init {
        getIdBrand()
    }

    private fun getIdBrand() = executeUseCase {
        viewModelScope.launch {
            quickActionUiState = quickActionUiState.copy(idBrand = dataStorePreferences.getIdBrand().first())
        }
    }

    data class QuickActionUiState(
        //quick actions fields
        var idBrand: String = "0"
    )

    fun getQuickActions(): List<QuickActionDummy> {
        return listOf(
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action"),
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action"),
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action")
        )
    }

    fun getSmartQuickAction(label: String, iconId: Int): QuickActionDummy {
        return QuickActionDummy(iconId, label)
    }
}

data class QuickActionDummy(val icon: Int, val label: String)