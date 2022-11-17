package com.multimoney.multimoney.presentation.ui.credit.origination.onfido

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.Companion.PACKAGE_NAME
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnConfigureOnFidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnOpenOnfidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
@Preview
fun CreditOnfidoScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: CreditOnfidoViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val whatsAppLink = stringResource(
        id = string.whatsapp_deep_link,
        SignUpViewModel.PHONE_HARDCODED
    )

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    if (viewModel.idBrand != null) {
        if (viewModel.idBrand == Brand.Guatemala.id) {
            viewModel.onUIEvent(
                OnSetCloseDialogTexts(
                    string.credit_close_dialog_gt_title,
                    stringResource(id = string.credit_close_dialog_gt_description)
                )
            )
        } else {
            viewModel.onUIEvent(
                OnSetCloseDialogTexts(
                    string.credit_close_dialog_title,
                    stringResource(id = string.credit_close_dialog_description)
                )
            )
        }
    }

    val launchOnFidoActivityResult = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        viewModel.onUIEvent(
            OnConfigureOnFidoSdk(result)
        )
    }

    LaunchedEffect(context) {
        viewModel.onFidoTokenEvent.collect { event ->
            event.onSuccess {
                viewModel.apply {
                    onUIEvent(
                        OnOpenOnfidoSdk(onOpenOnfidoSdk = {
                            viewModel.countDownTimer.stopTimer()
                            launchOnFidoActivityResult.launch(
                                viewModel.onFidoHelper.getOnFidoIntent(
                                    viewModel.idBrand,
                                    AppFlow.CREDIT_ORIGINATION,
                                    it?.sdkToken ?: "",
                                    onRefreshToke = { refreshToken ->
                                        viewModel.onUIEvent(
                                            RefreshOnFidoToken(
                                                firstName = viewModel.firstName,
                                                lastName = viewModel.lastName,
                                                identification = viewModel.identification,
                                                applicationId = PACKAGE_NAME,
                                                user = viewModel.email,
                                                injectNewToken = refreshToken
                                            )
                                        )
                                    }
                                )
                            )
                        })
                    )
                }
            }.onLoading {
                viewModel.onUIEvent(OnContinueEnable(false))
                viewModel.onUIEvent(OnLoadingValueChange(true))
            }.onFailure {
                viewModel.onUIEvent(OnContinueEnable(false))
                viewModel.onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnCallInFidoToken(
                firstName = viewModel.firstName,
                lastName = viewModel.lastName,
                identification = viewModel.identification,
                PACKAGE_NAME,
                user = viewModel.email
            )
        )
    }

    if (viewModel.uiState.isAlertVisible) {
        var title: Int = string.empty
        var subtitle: Int = string.empty
        var action: Int = string.empty
        if (viewModel.idBrand != null) {
            when (viewModel.idBrand) {
                Brand.Guatemala.id -> {
                    title = string.save_credit_operation_error_title
                    subtitle = string.save_credit_operation_error_subtitle_gt
                    action = string.save_credit_operation_error_action_gt
                }
                else -> {
                    title = string.save_credit_operation_error_title
                    subtitle = string.save_credit_operation_error_subtitle
                    action = string.save_credit_operation_error_action
                }
            }
        }
        AlertResult(
            iconResource = drawable.ic_error_symbol,
            titleResource = title,
            descriptionResource = subtitle,
            buttonTextResource = action,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
            onButtonClick = {
                context.openWhatsAppDeepLink(whatsAppLink)
            }
        )
    } else {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().background(MultimoneyTheme.colors.background)
        ) {
            val (topBar, content, button) = createRefs()

            TopNavBar(
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
            )
            Column(
                Modifier.padding(end = 16.dp, start = 16.dp, top = 24.dp).constrainAs(content) {
                    top.linkTo(topBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                    height = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = stringResource(id = string.sign_up_id_validation_title),
                    style = Typography.h5.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(id = string.sign_up_id_validation_subtitle),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Row(
                    Modifier.padding(top = 32.dp).fillMaxWidth()
                ) {
                    CustomImage(
                        drawableResource = drawable.ic_validation,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = string.sign_up_id_validation_one),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Row(
                    Modifier.padding(top = 32.dp).fillMaxWidth()
                ) {
                    CustomImage(
                        drawableResource = drawable.ic_validation,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = string.sign_up_id_validation_two),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Row(
                    Modifier.padding(top = 32.dp).fillMaxWidth()
                ) {
                    CustomImage(
                        drawableResource = drawable.ic_validation,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = string.sign_up_id_validation_three),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            CustomButton(
                onClick = { viewModel.onUIEvent(OnContinueClick) },
                text = stringResource(id = string.button_continue),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp).fillMaxWidth()
                    .height(48.dp).constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                buttonType = PrimaryPrimary,
                enable = viewModel.uiState.isContinueEnabled
            )
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    BackHandler {
        viewModel.onUIEvent(OnCloseClick)
    }
}
