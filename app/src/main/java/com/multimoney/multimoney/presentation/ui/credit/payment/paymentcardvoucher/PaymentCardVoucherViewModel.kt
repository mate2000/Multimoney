package com.multimoney.multimoney.presentation.ui.credit.payment.paymentcardvoucher

import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENT_AMOUNT_VALUE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_AUTOMATIC_PAYMENT_CHECKED
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentcardvoucher.PaymentCardVoucherViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentcardvoucher.PaymentCardVoucherViewModel.UIEvent.OnScheduleAutomaticPayment
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentcardvoucher.PaymentCardVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PaymentCardVoucherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {

    var referenceNumber: String? = null
    var currentAmountValueString: String? = null
    var currency: String? = null
    var currentDate: String = ""
    var currentTime: String = ""
    var card: CardVisaDirect? = null
    var isAutomaticProgrammedPaymentChecked: Boolean? = false
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var identification: String? = null
    private var paymentDate: String? = ""

    init {
        referenceNumber = savedStateHandle[REFERENCE_NUMBER] ?: ""
        currentAmountValueString = savedStateHandle[CURRENT_AMOUNT_VALUE]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        card = savedStateHandle[CARD_SELECTED]
        isAutomaticProgrammedPaymentChecked = savedStateHandle[IS_AUTOMATIC_PAYMENT_CHECKED]
        currentDate = getCurrentDate(Calendar.getInstance().time)
        currentTime = getCurrentTime(Calendar.getInstance().time)
        paymentDate = savedStateHandle[PAYMENT_DATE] ?: ""
    }

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onNavigateToHome() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)

    private fun onScheduleAutomaticPayment() = navigateTo(
        route = "${Screen.PaymentScheduleCardScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
        encodeData(
            card
        )
        }/$paymentDate/${false}/${Screen.PaymentCardVoucherScreen.baseRoute}/${false}/$identification"
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnScheduleAutomaticPayment -> onScheduleAutomaticPayment()
            is OnCloseClick -> onNavigateToHome()
            is OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()

        object OnScheduleAutomaticPayment : UIEvent()
    }
}
