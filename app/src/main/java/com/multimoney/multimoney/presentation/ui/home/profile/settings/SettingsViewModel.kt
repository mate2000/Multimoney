package com.multimoney.multimoney.presentation.ui.home.profile.settings

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
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val cognitoHelper: CognitoHelper,
    private val countDownTimer: MMCountDownTimer
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(SettingsViewModel.UIState())
        private set

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
        )
        viewModelScope.launch {
            val isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            uiState = uiState.copy(areBiometricsEnabled = isBiometricActive)
        }
    }

    private fun onShowConfirmationDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = if (uiState.idBrand == Brand.Guatemala.id) R.string.profile_sure_to_deactivate_biometrics_gt else R.string.profile_sure_to_deactivate_biometrics,
                descriptionResource = if (uiState.idBrand == Brand.Guatemala.id) R.string.profile_you_can_try_later_biometrics_gt else R.string.profile_you_can_try_later_biometrics,
                positiveResource = R.string.button_continue,
                negativeResource = R.string.cancel,
                positiveAction = { deleteBiometrics() },
                isActive = mutableStateOf(true),
                negativeAction = {}
            )
        )
    }

    private fun deleteBiometrics() {
        viewModelScope.launch {
            dataStorePreferences.isBiometricsEnabled(false)
        }
        uiState = uiState.copy(areBiometricsEnabled = false)
    }

    data class UIState(
        // Fields
        val openDialog: DialogParameters = DialogParameters(),
        val idBrand: Int? = null,
        val areBiometricsEnabled: Boolean? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnShowConfirmationDialog -> onShowConfirmationDialog()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnShowConfirmationDialog : UIEvent()
    }
}