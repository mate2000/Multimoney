package com.multimoney.multimoney.presentation.ui.qrcodescanner

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(): BaseViewModel(shouldObserveToken = true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onPermissionDialogDismissed() {
        uiState = uiState.copy(
            permissionDialog = DialogParameters(
                titleResource = R.string.crypto_send_address_permission_camera_title,
                descriptionResource = R.string.crypto_send_address_permission_camera_description,
                positiveResource = R.string.common_allow,
                negativeResource = R.string.common_deny,
                positiveAction = {
                    uiState = uiState.copy(
                        requestCameraPermission = mutableStateOf(true),
                        permissionDialog = uiState.permissionDialog.copy(
                            isActive = mutableStateOf(false)
                        )
                    )
                },
                isActive = mutableStateOf(true),
                isCancelable = false
            )
        )
    }

    private fun onShowEnablePermissionsInSettingsDialog() {
        uiState = uiState.copy(
            permissionDialog = DialogParameters(
                titleResource = R.string.crypto_send_address_permission_camera_title,
                descriptionResource = R.string.crypto_send_address_permission_camera_description,
                positiveResource = R.string.common_open_settings,
                negativeResource = R.string.common_deny,
                positiveAction = {
                    uiState = uiState.copy(
                        openPermissionInSettings = mutableStateOf(true),
                        permissionDialog = uiState.permissionDialog.copy(
                            isActive = mutableStateOf(false)
                        )
                    )
                },
                isActive = mutableStateOf(true),
                isCancelable = false
            )
        )
    }

    private fun onPermissionInSettingsOpened() {
        uiState = uiState.copy(
            openPermissionInSettings = mutableStateOf(false)
        )
    }

    private fun onGetQrCodeFromSavedState(qrCodeResult: String) {
        uiState = uiState.copy(cryptoAddress = mutableStateOf(qrCodeResult))
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnCameraPermissionMissed -> onPermissionDialogDismissed()
            is UIEvent.OnShowEnablePermissionsInSettingsDialog -> onShowEnablePermissionsInSettingsDialog()
            is UIEvent.OnPermissionInSettingsOpened -> onPermissionInSettingsOpened()
            is UIEvent.OnSetQrCodeFromSavedState -> onGetQrCodeFromSavedState(event.qrCodeResult)
        }
    }

    data class UIState(
        val cryptoAddress: MutableState<String> = mutableStateOf(""),
        val permissionDialog: DialogParameters = DialogParameters(),
        val requestCameraPermission: MutableState<Boolean> = mutableStateOf(false),
        val openPermissionInSettings: MutableState<Boolean> = mutableStateOf(false)
    )

    sealed interface UIEvent {
        object OnCameraPermissionMissed : UIEvent
        object OnShowEnablePermissionsInSettingsDialog : UIEvent
        object OnPermissionInSettingsOpened : UIEvent
        data class OnSetQrCodeFromSavedState(val qrCodeResult: String): UIEvent
    }
}
