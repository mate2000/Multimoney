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
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
            pkUser = savedStateHandle[PK_USER],
            userName = savedStateHandle[USER_NAME]
        )
        viewModelScope.launch {
            val isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            uiState = uiState.copy(areBiometricsEnabled = isBiometricActive)
        }
    }

    private fun onShowConfirmationDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = if (uiState.idBrand == Brand.CostaRica.id) R.string.profile_sure_to_deactivate_biometrics else R.string.profile_sure_to_deactivate_biometrics_sv,
                descriptionResource = if (uiState.idBrand == Brand.CostaRica.id) R.string.profile_you_can_try_later_biometrics else R.string.profile_you_can_try_later_biometrics_sv,
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

    private fun onNavigateToChangePassword() {
        navigateTo("${Screen.ProfileChangePasswordScreen.baseRoute}/${uiState.idBrand}/${uiState.pkUser}/${uiState.userName}/${Screen.ProfileSettingsScreen.baseRoute}")
    }

    data class UIState(
        // Fields
        val openDialog: DialogParameters = DialogParameters(),
        val idBrand: Int? = null,
        val pkUser: String? = null,
        val userName: String? = null,
        val areBiometricsEnabled: Boolean? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
            is UIEvent.OnShowConfirmationDialog -> onShowConfirmationDialog()
            is UIEvent.OnNavigateToChangePassword -> onNavigateToChangePassword()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnShowConfirmationDialog : UIEvent()
        object OnNavigateToChangePassword : UIEvent()
    }
}
