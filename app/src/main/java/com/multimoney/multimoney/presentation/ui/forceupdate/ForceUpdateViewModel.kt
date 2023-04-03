package com.multimoney.multimoney.presentation.ui.forceupdate

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.ui.forceupdate.ForceUpdateViewModel.UIEvent.OnUpdateClick
import com.multimoney.multimoney.presentation.util.openGooglePlayStoreDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForceUpdateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int? = null

    init {
        idBrand = savedStateHandle[ID_BRAND]
        getTextResources()
    }

    private fun getTextResources() {
        viewModelScope.launch {
            uiState = when (idBrand) {
                Brand.CostaRica.id -> uiState.copy(
                    titleResource = R.string.force_update_title_cr,
                    messageResource = R.string.force_update_message_cr
                )
                else -> uiState.copy(
                    titleResource = R.string.force_update_title_gt_sv,
                    messageResource = R.string.force_update_message_gt_sv
                )
            }
        }
    }

    data class UIState(
        val titleResource: Int = R.string.empty,
        val messageResource: Int = R.string.empty
    )

    private fun openGooglePlayStore(context: Context) {
        context.openGooglePlayStoreDeepLink(GOOGLE_PLAY_STORE_URL)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnUpdateClick -> openGooglePlayStore(event.context)
        }
    }

    sealed class UIEvent {
        data class OnUpdateClick(val context: Context) : UIEvent()
    }

    companion object {
        const val GOOGLE_PLAY_STORE_URL = "market://details?id=com.multimoney.multimoney.cr"
    }
}