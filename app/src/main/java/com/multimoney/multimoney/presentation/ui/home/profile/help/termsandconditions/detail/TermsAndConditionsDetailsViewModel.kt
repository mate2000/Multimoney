package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.DATE_SIGNED
import com.multimoney.multimoney.presentation.navigation.HTML
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TITLE
import com.multimoney.multimoney.presentation.navigation.VERSION
import com.multimoney.multimoney.presentation.util.decodeURLFromUTF
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.Scanner
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    init {
        val tempHtmlPath = savedStateHandle[HTML] ?: ""
        var extractedHtml = ""
        var tempFileReader: File? = null
        if (tempHtmlPath.isNotBlank()) {
            tempFileReader = File(tempHtmlPath.decodeURLFromUTF())
            val scanner = Scanner(tempFileReader)
            while (scanner.hasNextLine()) {
                extractedHtml += scanner.nextLine()
            }
            scanner.close()
        }

        if (tempFileReader?.exists() == true && tempFileReader.isDirectory) {
            tempFileReader.deleteRecursively()
        }

        uiState = uiState.copy(
            html = extractedHtml,
            title = savedStateHandle[TITLE],
            version = savedStateHandle[VERSION],
            dateSigned = savedStateHandle[DATE_SIGNED]
        )
    }

    data class UIState(
        // Fields
        val html: String? = null,
        val title: String? = null,
        val version: String? = null,
        val dateSigned: String? = null,
        val termsAndConditionsSigned: TermsAndConditionsSigned? = null,
        val termsAndConditionsTitleResource: Int? = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(
                Screen.ProfileTermsAndConditionsScreen.route,
                false
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
    }
}
