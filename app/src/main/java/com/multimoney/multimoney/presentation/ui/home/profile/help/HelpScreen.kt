package com.multimoney.multimoney.presentation.ui.home.profile.help

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnCallToAttentionCenterClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnChatWithUsClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnFAQClick
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnGetContactInfo
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.home.profile.help.HelpScreenViewModel.UIEvent.OnTermsAndConditionsClick
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomItemRow
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun HelpScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    viewModel: HelpScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack
        )
        viewModel.onUIEvent(OnGetContactInfo)
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    HelpScreenContent(viewModel)
}

@Composable
fun HelpScreenContent(viewModel: HelpScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        HelpOptions(
            onChatWithUsClick = {
                viewModel.onUIEvent(OnChatWithUsClick { whatsAppIntent ->
                    openIntent(
                        context = context,
                        intent = whatsAppIntent,
                        onFailure = { errorMessage ->
                            viewModel.onUIEvent(
                                HelpScreenViewModel.UIEvent.OnFailureWithDialog(
                                    isLoading = false, openDialog = DialogParameters(
                                        description = errorMessage ?: "",
                                        isActive = mutableStateOf(true)
                                    )
                                )
                            )
                        }
                    )
                })
            },
            onCallAttentionCenterClick = {
                viewModel.onUIEvent(OnCallToAttentionCenterClick { phoneIntent ->
                    openIntent(
                        context = context,
                        intent = phoneIntent,
                        onFailure = { errorMessage ->
                            viewModel.onUIEvent(
                                HelpScreenViewModel.UIEvent.OnFailureWithDialog(
                                    isLoading = false, openDialog = DialogParameters(
                                        description = errorMessage ?: "",
                                        isActive = mutableStateOf(true)
                                    )
                                )
                            )
                        }
                    )
                })
            },
            onFAQClick = {
                viewModel.onUIEvent(OnFAQClick { faqIntent ->
                    openIntent(
                        context = context,
                        intent = faqIntent,
                        onFailure = { errorMessage ->
                            viewModel.onUIEvent(
                                HelpScreenViewModel.UIEvent.OnFailureWithDialog(
                                    isLoading = false, openDialog = DialogParameters(
                                        description = errorMessage ?: "",
                                        isActive = mutableStateOf(true)
                                    )
                                )
                            )
                        }
                    )
                })
            },
            onTermsAndConditions = { viewModel.onUIEvent(OnTermsAndConditionsClick) }
        )
    }
}

@Composable
fun HelpOptions(
    onChatWithUsClick: () -> Unit,
    onCallAttentionCenterClick: () -> Unit,
    onFAQClick: () -> Unit,
    onTermsAndConditions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(R.string.profile_help_and_info_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_chat_with_us_label),
            startIcon = R.drawable.ic_chat_bot,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onChatWithUsClick,
            startIconColor = MultimoneyTheme.colors.text
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_call_attention_center_label),
            startIcon = R.drawable.ic_phone,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onCallAttentionCenterClick,
            startIconColor = MultimoneyTheme.colors.text
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_faq_label),
            startIcon = R.drawable.ic_help,
            endIcon = R.drawable.ic_right_chevron,
            onClick = onFAQClick,
            startIconColor = MultimoneyTheme.colors.text
        )
        CustomItemRow(
            title = stringResource(R.string.profile_help_terms_and_conditions_label),
            startIcon = R.drawable.ic_document,
            onClick = onTermsAndConditions,
            endIcon = R.drawable.ic_right_chevron,
            startIconColor = MultimoneyTheme.colors.text
        )
    }
}

fun openIntent(context: Context, intent: Intent, onFailure: (error: String?) -> Unit) {
    try {
        context.startActivity(intent)
    } catch (s: SecurityException) {
        onFailure(s.message)
    } catch (noActivity: ActivityNotFoundException) {
        onFailure(noActivity.message)
    }
}
