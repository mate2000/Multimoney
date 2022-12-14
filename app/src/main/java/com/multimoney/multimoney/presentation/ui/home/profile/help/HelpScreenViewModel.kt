package com.multimoney.multimoney.presentation.ui.home.profile.help

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCase
import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HelpScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    savedStateHandle: SavedStateHandle,
    private val queryCountryContactUseCase: QueryCountryContactUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    private var contactCountryInfo: CountryContact? = null
    private val idBrand = savedStateHandle[ID_BRAND] ?: 0
    val defaultDialogParameters =
        DialogParameters(descriptionResource = R.string.something_went_wrong)

    private fun getContactInfo() =
        executeUseCase {
            queryCountryContactUseCase.invoke(
                user = dataStorePreferences.getUserName().first(),
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { contactInfo ->
                    contactCountryInfo = contactInfo
                    uiState = uiState.copy(
                        isLoading = false,
                        isAlertResultVisible = false
                    )
                }
                result.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        isAlertResultVisible = true
                    )
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    private fun callAttentionCenter(
        openIntent: (Intent) -> Unit,
        onFailureWithDialog: (isActive: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        try {
            val phone = contactCountryInfo?.customerServicesPhone
            phone?.let {
                val uri = Uri.parse(TEL_PREFIX + phone)
                val intent = Intent(Intent.ACTION_DIAL, uri)
                openIntent(intent)
            } ?: onFailureWithDialog(
                false,
                defaultDialogParameters.copy(isActive = mutableStateOf(true))
            )
        } catch (exception: NullPointerException) {
            onFailureWithDialog(
                false,
                defaultDialogParameters.copy(isActive = mutableStateOf(true))
            )
        }
    }

    private fun openWhatsappLink(
        openIntent: (String) -> Unit,
        onFailureWithDialog: (isActive: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        val link = contactCountryInfo?.whatsappLink
        link?.let {
            openIntent(link)
        } ?: onFailureWithDialog(
            false,
            defaultDialogParameters.copy(isActive = mutableStateOf(true))
        )
    }

    private fun openFAQ(
        openIntent: (Intent) -> Unit,
        onFailureWithDialog: (isActive: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        try {
            val uri = when (idBrand) {
                Brand.Guatemala.id -> Uri.parse(FAQ_LINK_GT)
                Brand.ElSalvador.id -> Uri.parse(FAQ_LINK_SV)
                Brand.CostaRica.id -> Uri.parse(FAQ_LINK_CR)
                else -> null
            }
            val intent = Intent(Intent.ACTION_VIEW, uri)
            openIntent(intent)
        } catch (exception: NullPointerException) {
            onFailureWithDialog(
                false,
                defaultDialogParameters.copy(isActive = mutableStateOf(true))
            )
        }
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.ProfileScreen.route, isRestart = false)

    data class UIState(
        val isLoading: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnGetContactInfo -> getContactInfo()
            is UIEvent.OnChatWithUsClick -> openWhatsappLink(
                uiEvent.openWhatsAppIntent,
                uiEvent.onFailureWithDialog
            )
            is UIEvent.OnCallToAttentionCenterClick -> callAttentionCenter(
                uiEvent.openPhoneIntent,
                uiEvent.onFailureWithDialog
            )
            is UIEvent.OnFAQClick -> openFAQ(uiEvent.openFAQIntent, uiEvent.onFailureWithDialog)
            // Todo add terms and conditions action
            is UIEvent.OnTermsAndConditionsClick -> Timber.d("Open Terms Website")
            is UIEvent.OnAlertResultButtonClick -> onNavigateBack()
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(
                        isLoading = uiEvent.isLoading,
                        openDialog = uiEvent.dialogParameters
                    )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetContactInfo : UIEvent()
        data class OnChatWithUsClick(
            val openWhatsAppIntent: (String) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnCallToAttentionCenterClick(
            val openPhoneIntent: (Intent) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnFAQClick(
            val openFAQIntent: (Intent) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        object OnTermsAndConditionsClick : UIEvent()
        object OnAlertResultButtonClick : UIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val dialogParameters: DialogParameters
        ) : UIEvent()
    }

    companion object {
        const val TEL_PREFIX = "tel:"
        const val FAQ_LINK_GT = "https://www.multimoney.com/gt/ayuda"
        const val FAQ_LINK_SV = "https://www.multimoney.com/sv/ayuda"
        const val FAQ_LINK_CR = "https://www.multimoney.com/cr/ayuda"
    }
}