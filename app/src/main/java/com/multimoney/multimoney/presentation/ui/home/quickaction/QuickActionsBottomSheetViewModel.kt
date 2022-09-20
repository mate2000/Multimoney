package com.multimoney.multimoney.presentation.ui.home.quickaction

import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuickActionsBottomSheetViewModel @Inject constructor() : BaseViewModel() {
    fun getQuickActions(): List<QuickActionDummy> {
        return listOf(
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action"),
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action"),
            QuickActionDummy(R.drawable.ic_quickaction_item_icon, "Quick Action")
        )
    }
}

data class QuickActionDummy(val icon: Int, val label: String)