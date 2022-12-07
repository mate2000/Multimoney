package com.multimoney.multimoney.presentation.ui.visa.issuance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCase
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnIssuanceClick
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NfcHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class VisaIssuanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nfcHelper: NfcHelper,
    private val balanceCardInformationUseCase: QueryBalanceCardInformationUseCase
) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var identification: String = ""
    private var user: String = ""
    private var balanceCardInformation: BalanceCardInformation? = null

    init {
        idBrand = savedStateHandle.get<Int>(ID_BRAND) ?: 0
        idClient = savedStateHandle.get<Int>(ID_CLIENT) ?: 0
        idLoanClient = savedStateHandle.get<Int>(ID_LOAN_CLIENT) ?: 0
        identification = savedStateHandle.get<String>(IDENTIFICATION) ?: ""
        user = savedStateHandle.get<String>(USER) ?: ""
        getTextResources()
        onCallQueryBalanceCardInformation()
    }

    private fun getTextResources() {
        when {
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_nfc_subtitle
                )
            }
            idBrand == Brand.ElSalvador.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_sv_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_sv_no_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_nfc_subtitle
                )
            }
            idBrand == Brand.CostaRica.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_cr_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_cr_no_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_nfc_subtitle
                )
            }
            idBrand == Brand.Guatemala.id && nfcHelper.isNfcSupported().not() -> {
                uiState = uiState.copy(
                    titleResource = R.string.visa_issuance_gt_no_nfc_title,
                    subtitleResource = R.string.visa_issuance_gt_no_nfc_subtitle
                )
            }
        }
    }

    private fun onCallQueryBalanceCardInformation() {
        executeUseCase {
            balanceCardInformationUseCase.invoke(
                user,
                identification,
                idBrand,
                idClient,
                idLoanClient,
                CARD_INFORMATION_STATUS
            ).collectLatest { result ->
                result.onSuccess {
                    balanceCardInformation = it
                    uiState = uiState.copy(isLoading = false)
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    data class UIState(
        // Interactions
        val isTextVisible: Boolean = false,
        val titleResource: Int = R.string.empty,
        val subtitleResource: Int = R.string.empty,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is OnIssuanceClick -> popAndNavigateTo(
                "${Screen.VisaCardScreen.baseRoute}/$idBrand",
                Screen.VisaIssuanceScreen.route
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnIssuanceClick : UIEvent()
    }

    companion object {
        private const val CARD_INFORMATION_STATUS = 1
    }
}
