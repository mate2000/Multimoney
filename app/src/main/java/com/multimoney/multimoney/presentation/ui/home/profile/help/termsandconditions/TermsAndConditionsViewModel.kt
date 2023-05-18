package com.multimoney.multimoney.presentation.ui.home.profile.help.termsandconditions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.TermsAndConditionsType
import com.multimoney.domain.interaction.profile.QueryTermsAndConditionsSignedUseCase
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.DATE_SIGNED
import com.multimoney.multimoney.presentation.navigation.HTML
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TITLE
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.VERSION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.encodeURLToUTF
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val queryTermsAndConditionsSignedUseCase: QueryTermsAndConditionsSignedUseCase
) : BaseViewModel(true) {

    val onGetTermsAndConditionsSigned =
        MutableSharedFlow<MultimoneyResult<TermsAndConditionsSigned?>>()

    var uiState by mutableStateOf(UIState())
        private set

    val userName: String = savedStateHandle[USER_NAME] ?: ""
    val identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    val pkUser: String = savedStateHandle[PK_USER] ?: ""
    val idBrand: Int = savedStateHandle[ID_BRAND] ?: 0

    private fun onStart(isSystemOnDarkTheme: Boolean) {
        callQueryTermsAndConditionsSigned(
            identification,
            pkUser.toInt(),
            idBrand,
            userName,
            isSystemOnDarkTheme
        )
    }

    private fun callQueryTermsAndConditionsSigned(
        identification: String,
        pkUser: Int,
        idBrand: Int,
        user: String,
        styleDark: Boolean
    ) = executeUseCase {
        queryTermsAndConditionsSignedUseCase.invoke(
            identification = identification,
            idBrand = idBrand,
            user = user,
            pkUser = pkUser,
            styleDark = styleDark
        ).collectLatest { result ->
            result.onSuccess {
                it?.let { items ->
                    onQuerySuccess(items)
                }
            }.onFailure {
                onShowCustomDialog(it.getError() ?: "")
                onUpdateLoadingState(false)
            }.onLoading {
                onUpdateLoadingState(true)
            }
        }
    }

    fun getStringResource(type: String): Int {
        return when (type) {
            TermsAndConditionsType.SMART.type -> R.string.profile_terms_and_conditions_smart_title
            TermsAndConditionsType.VENTAS.type -> R.string.profile_terms_and_conditions_sales_title
            TermsAndConditionsType.CRYPTO.type -> R.string.profile_terms_and_conditions_crypto_title
            else -> R.string.empty
        }
    }

    private fun onTermsAndConditionsClicked(
        title: String,
        html: String,
        version: String,
        dateSigned: String
    ) {
        val tempFile = File.createTempFile(TERMS_FILE_PREFIX, FILE_SUFFIX)
        val writer = BufferedWriter(FileWriter(tempFile))
        writer.write(html)
        writer.close()
        navigateTo(
            Screen.ProfileTermsAndConditionsDetailScreen.baseRoute.plus(
                getNavParam(
                    TITLE,
                    title
                )
            ).plus(getNavParam(HTML, tempFile.absolutePath.encodeURLToUTF()))
                .plus(getNavParam(VERSION, version)).plus(getNavParam(DATE_SIGNED, dateSigned))
        )
    }

    private fun onQuerySuccess(items: TermsAndConditionsSigned) {
        uiState = uiState.copy(termsAndConditionsSigned = items, isLoading = false)
    }

    private fun onUpdateLoadingState(isLoading: Boolean) {
        uiState = uiState.copy(isLoading = isLoading)
    }

    private fun onShowCustomDialog(description: String) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                isActive = mutableStateOf(true),
                titleResource = R.string.error,
                description = description
            )
        )
    }

    data class UIState(
        // Fields
        val termsAndConditionsSigned: TermsAndConditionsSigned? = null,
        val termsAndConditionsTitleResource: Int? = R.string.empty,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileHelpScreen.route, false)
            is UIEvent.OnStart -> onStart(event.isSystemInDarkTheme)
            is UIEvent.OnTermsAndConditionsClicked -> onTermsAndConditionsClicked(
                event.title,
                event.html,
                event.version,
                event.dateSigned
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnStart(val isSystemInDarkTheme: Boolean) : UIEvent()
        data class OnTermsAndConditionsClicked(
            val title: String,
            val html: String,
            val version: String,
            val dateSigned: String
        ) : UIEvent()
    }

    companion object {
        const val TERMS_FILE_PREFIX = "terms"
        const val FILE_SUFFIX = ".txt"
    }
}