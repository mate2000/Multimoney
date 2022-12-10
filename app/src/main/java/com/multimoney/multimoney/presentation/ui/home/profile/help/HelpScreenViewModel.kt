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
    private val savedStateHandle: SavedStateHandle,
    private val queryCountryContactUseCase: QueryCountryContactUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    // Todo remove this values, this are for testing purposes
    private var contactCountryInfo: CountryContact? = CountryContact(
        whatsappLink = "https://api.whatsapp.com/send/?phone=50325651069&text&type=phone_number&app_absent=0",
        customerServicesPhone = "22459000"
    )
    private val idBrand = savedStateHandle[ID_BRAND] ?: 0

    private fun getContactInfo() =
        executeUseCase {
            queryCountryContactUseCase.invoke(
                user = dataStorePreferences.getUserName().first(),
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { contactInfo ->
                    contactCountryInfo = contactInfo
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure {
                    onUIEvent(
                        UIEvent.OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                    uiState = uiState.copy(isLoading = false)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }

            }
        }

    private fun callAttentionCenter(openIntent: (Intent) -> Unit) {
        val uri = Uri.parse(TEL_PREFIX + contactCountryInfo?.customerServicesPhone)
        val intent = Intent(Intent.ACTION_DIAL, uri)
        openIntent(intent)
    }

    private fun openWhatsappLink(openIntent: (Intent) -> Unit) {
        val uri = Uri.parse(contactCountryInfo?.whatsappLink)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        openIntent(intent)
    }

    private fun openFAQ(openIntent: (Intent) -> Unit) {
        val uri = when (idBrand) {
            Brand.Guatemala.id -> Uri.parse(FAQ_LINK_GT)
            Brand.ElSalvador.id -> Uri.parse(FAQ_LINK_SV)
            Brand.CostaRica.id -> Uri.parse(FAQ_LINK_CR)
            else -> null
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        openIntent(intent)
    }

    data class UIState(
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
            is UIEvent.OnGetContactInfo -> getContactInfo()
            is UIEvent.OnChatWithUsClick -> openWhatsappLink(uiEvent.openWhatsAppIntent)
            is UIEvent.OnCallToAttentionCenterClick -> callAttentionCenter(uiEvent.openPhoneIntent)
            is UIEvent.OnFAQClick -> openFAQ(uiEvent.openFAQIntent)
            // Todo add terms and conditions action
            is UIEvent.OnTermsAndConditionsClick -> Timber.d("Open Terms Website")
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = uiEvent.isLoading, openDialog = uiEvent.openDialog)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetContactInfo : UIEvent()
        data class OnChatWithUsClick(val openWhatsAppIntent: (Intent) -> Unit) : UIEvent()
        data class OnCallToAttentionCenterClick(val openPhoneIntent: (Intent) -> Unit) : UIEvent()
        data class OnFAQClick(val openFAQIntent: (Intent) -> Unit) : UIEvent()
        object OnTermsAndConditionsClick : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()
    }

    companion object {
        const val TEL_PREFIX = "tel:"
        const val FAQ_LINK_GT = "https://www.multimoney.com/gt/preguntas-frecuentes"
        const val FAQ_LINK_SV = "https://www.multimoney.com/sv/preguntas-frecuentes"
        const val FAQ_LINK_CR = "https://www.multimoney.com/cr/preguntas-frecuentes"
    }
}