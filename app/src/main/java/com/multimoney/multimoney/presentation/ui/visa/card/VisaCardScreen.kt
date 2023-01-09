package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.BaseEvent.OnOpenNfcConfig
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.BaseEvent.OnOpenTapAndPayConfig
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnBlockUnblockCardClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnCallNovoGetFavoriteCard
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHandleTapAndPayIntentResult
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHidePaymentSuccessScreen
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnInitializeBiometricPrompt
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigatePreferences
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnOpenDialogConfirmToStartTokenizationProcess
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnSeeDataClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnStartPaymentProcess
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButtonBig
import com.multimoney.multimoney.presentation.uielement.CustomCardVisaVertical
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.formatExpirationDate
import com.multimoney.multimoney.presentation.util.getCardNumberFour
import com.multimoney.multimoney.presentation.util.getCardNumberOne
import com.multimoney.multimoney.presentation.util.getCardNumberThree
import com.multimoney.multimoney.presentation.util.getCardNumberTwo
import com.multimoney.multimoney.presentation.util.getTapAndPayIntent

@Composable
@Preview
@OptIn(ExperimentalMaterialApi::class)
fun VisaCardScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: VisaCardViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val fragmentActivity = LocalContext.current as FragmentActivity

    val launch = rememberLauncherForActivityResult(contract = StartActivityForResult(), onResult = { result ->
        viewModel.onUIEvent(OnHandleTapAndPayIntentResult(result))
    })

    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                viewModel.onUIEvent(OnCallNovoGetFavoriteCard)
                isOnRestart = false
            }
        }
    }

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(
                onNavigate = onNavigate,
                onPopBackStack = onPopBackStack,
                onPopAndNavigate = onPopAndNavigate
            )
            onUIEvent(OnStart)
        }
    }

    viewModel.onUIEvent(
        OnInitializeBiometricPrompt(
            biometricPromptTitle = stringResource(id = string.visa_card_biometric_dialog_title),
            biometricPromptDescription = stringResource(id = string.visa_card_biometric_dialog_subtitle),
            biometricPromptNegative = stringResource(id = string.cancel)
        )
    )

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                OnOpenTapAndPayConfig -> {
                    launch.launch(context.getTapAndPayIntent())
                }
                OnOpenNfcConfig -> {
                    launch.launch(viewModel.nfcHelper.getIntentToRequestActivateNfc())
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        if (viewModel.deviceHasNFC()) {
            TopNavBar(
                rightButtonIcon = R.drawable.ic_gear,
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onRightButtonClick = {
                    viewModel.onUIEvent(OnNavigatePreferences)
                }
            )
        } else {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isRightButtonVisible = false
            )
        }

        CustomCardVisaVertical(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 42.dp, bottom = 16.dp)
                .fillMaxWidth(),
            isBlocked = viewModel.uiState.isCardBlocked,
            isTextVisible = viewModel.uiState.isCardTextVisible,
            cardNumberOne = viewModel.balanceCardInformation?.cardInformation?.cardNumber?.getCardNumberOne() ?: "",
            cardNumberTwo = viewModel.balanceCardInformation?.cardInformation?.cardNumber?.getCardNumberTwo() ?: "",
            cardNumberThree = viewModel.balanceCardInformation?.cardInformation?.cardNumber?.getCardNumberThree() ?: "",
            cardNumberFour = viewModel.balanceCardInformation?.cardInformation?.cardNumber?.getCardNumberFour() ?: "",
            date = viewModel.balanceCardInformation?.cardInformation?.expDate?.formatExpirationDate() ?: "",
            cvv = viewModel.balanceCardInformation?.cardInformation?.cValidation ?: "",
            holderName = viewModel.balanceCardInformation?.cardInformation?.holderName ?: ""
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomInformativeChip(
                text = stringResource(
                    id = R.string.visa_card_available_amount,
                    viewModel.availableBalanceLabel.orEmpty()
                ),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                onClick = {
                    viewModel.onUIEvent(UIEvent.OnAvailableAmountClick)
                },
                shape = RoundedCornerShape(24.dp),
                background = MultimoneyTheme.colors.backgroundInformativeChip,
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Large
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (viewModel.uiState.isNfcAvailable && viewModel.uiState.isCardTokenize.not()) {
                CustomButtonBig(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    icon = R.drawable.ic_link,
                    text = stringResource(id = string.link),
                    enabled = viewModel.uiState.isCardBlocked.not(),
                    onClick = {
                        viewModel.onUIEvent(OnOpenDialogConfirmToStartTokenizationProcess)
                    }
                )
            } else if (viewModel.uiState.isNfcAvailable && viewModel.uiState.isCardTokenize) {
                CustomButtonBig(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    icon = R.drawable.ic_pay,
                    text = stringResource(id = R.string.pay),
                    enabled = viewModel.uiState.isCardBlocked.not(),
                    onClick = {
                        viewModel.onUIEvent(OnStartPaymentProcess)
                    }
                )
            }
            CustomButtonBig(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                icon = R.drawable.ic_eye,
                text = stringResource(id = R.string.see_data),
                onClick = {
                    viewModel.onUIEvent(OnSeeDataClick(fragmentActivity))
                }
            )
            CustomButtonBig(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                icon = viewModel.uiState.blockUnblockButtonIcon,
                text = stringResource(id = viewModel.uiState.blockUnblockButtonText),
                onClick = {
                    viewModel.onUIEvent(OnBlockUnblockCardClick)
                }
            )
        }
        if (viewModel.uiState.isCardBlocked) {
            CustomInformativeChip(
                text = stringResource(id = viewModel.uiState.visaCardBlockDisclaimer),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.textInformation),
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Large
            )
        }
    }

    if (viewModel.uiState.showPaymentSuccessScreen) {
        AlertResult(
            iconResource = drawable.ic_success_symbol,
            titleResource = string.visa_payment_success_title,
            descriptionResource = string.visa_payment_success_description,
            buttonTextResource = string.finalize,
            isTopNavBarVisible = false,
            onButtonClick = { viewModel.onUIEvent(OnHidePaymentSuccessScreen) }
        )
    }

    VisaCardPasswordBottomSheetScreen(
        viewModel,
        coroutineScope,
        viewModel.uiState.bottomSheetVisibleState
    )

    LoadingIndicator(viewModel.uiState.isLoading)

    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource).ifEmpty { viewModel.uiState.dialogParameters.description },
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}
