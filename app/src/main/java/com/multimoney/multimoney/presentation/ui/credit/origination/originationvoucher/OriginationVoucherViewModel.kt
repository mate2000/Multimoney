package com.multimoney.multimoney.presentation.ui.credit.origination.originationvoucher

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryGetInfoDepositUseCase
import com.multimoney.domain.model.credit.GetInfoDeposit
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.origination.originationvoucher.OriginationVoucherViewModel.UIEvent.OnCallQueryGetInfoDebit
import com.multimoney.multimoney.presentation.ui.credit.origination.originationvoucher.OriginationVoucherViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.originationvoucher.OriginationVoucherViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.originationvoucher.OriginationVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.API_DATE_FORMAT
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class OriginationVoucherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryGetInfoDepositUseCase: QueryGetInfoDepositUseCase,
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    var idBrand: Int = 0
    var idPrint: Long = 0
    var user: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        user = savedStateHandle[USER] ?: ""
    }

    private fun onCallQueryGetInfoDebit() {
        executeUseCase {
            queryGetInfoDepositUseCase.invoke(
                idBrand = idBrand,
                idPrint = idPrint,
                user = user
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false, infoDeposit = it)
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onMessage {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it?.message ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onNavigateToHome() {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
    }

    fun getDateFormatted(): String {
        return uiState.infoDeposit?.date?.let {
            val date = API_DATE_FORMAT.parse(it)
            date?.let {
                getCurrentDate(date)
            } ?: run {
                ""
            }
        } ?: run {
            ""
        }
    }

    fun getHourFormatted(): String {
        return uiState.infoDeposit?.date?.let {
            val date = API_DATE_FORMAT.parse(it)
            date?.let {
                getCurrentTime(date)
            } ?: run {
                ""
            }
        } ?: run {
            ""
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val infoDeposit: GetInfoDeposit? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is OnCallQueryGetInfoDebit -> onCallQueryGetInfoDebit()
            is OnCloseClick -> onNavigateToHome()
            is OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnCallQueryGetInfoDebit : UIEvent()
        object OnCloseClick : UIEvent()
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}
