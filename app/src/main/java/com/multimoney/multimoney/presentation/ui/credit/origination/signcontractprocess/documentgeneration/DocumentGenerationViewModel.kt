package com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.documentgeneration

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.documentgeneration.DocumentGenerationViewModel.UIEvent.OnOpenSignDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocumentGenerationViewModel @Inject constructor() : BaseViewModel(true) {

    private fun openSignDocument(link: String?) {
        popAndNavigateTo(
            route = "${Screen.SignDocumentScreen.baseRoute}/".plus(link),
            popTo = Screen.DocumentGenerationScreen.route
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnOpenSignDocument -> openSignDocument(uiEvent.link)
        }
    }

    sealed class UIEvent {
        data class OnOpenSignDocument(val link: String?) : UIEvent()
    }
}
