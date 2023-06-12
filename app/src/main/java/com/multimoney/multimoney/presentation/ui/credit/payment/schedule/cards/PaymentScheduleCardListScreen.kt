package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.ReactActivity
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnResumeTimer
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnStopTimer
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.VisaUtils.APPLICATION_NAME
import com.multimoney.multimoney.presentation.util.VisaUtils.ENDPOINT
import com.multimoney.multimoney.presentation.util.VisaUtils.RESPONSE_IS_ERROR
import com.multimoney.multimoney.presentation.util.VisaUtils.RESPONSE_VALUE
import com.multimoney.multimoney.presentation.util.VisaUtils.RESULT_CODE_PROCESS_FINISHED
import com.multimoney.multimoney.presentation.util.VisaUtils.RESULT_CODE_PROCESS_INCOMPLETE
import com.multimoney.multimoney.presentation.util.VisaUtils.VISA_USER_NAME
import com.multimoney.multimoney.presentation.util.VisaUtils.VISA_USER_PASS
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType

@Composable
fun PaymentScheduleCardListScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentScheduleCardListViewModel = hiltViewModel(),
    isRestart: Boolean = true
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            isOnRestart = isRestart
            if (isOnRestart) {
                executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(OnStart)
                isOnRestart = false
            }
        }
    }
    PaymentScheduleAccountContent(viewModel)
}

@Composable
@Preview
fun PaymentScheduleAccountContent(
    viewModel: PaymentScheduleCardListViewModel = hiltViewModel()
) {
    if (viewModel.uiState.isAlertResultVisible) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = if (isAlertResultSuccess) {
                    string.empty
                } else {
                    alertResultDescriptionResource
                },
                descriptionString = alertResultDescription,
                buttonTextResource = alertResultButtonResource,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
            )
        }
    } else {
        val context = LocalContext.current
        val addCardActivityResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel.logEvents(AdjustEventType.SETTINGS_CTA_FIRST_FINISH_FLOW_CARD_8008)
            viewModel.onUIEvent(OnResumeTimer)
            when (it.resultCode) {
                RESULT_CODE_PROCESS_FINISHED -> {
                    val response: String? = it.data?.getStringExtra(RESPONSE_VALUE)
                    val isError: Boolean? = it.data?.getBooleanExtra(RESPONSE_IS_ERROR, false)
                    viewModel.onUIEvent(
                        OnHandleAddCardResponse(
                            response = response.orEmpty(),
                            isError = isError ?: false
                        )
                    )
                }
                RESULT_CODE_PROCESS_INCOMPLETE -> {
                    viewModel.onUIEvent(OnNavigateBackHome)
                }
                else -> return@rememberLauncherForActivityResult
            }
        }
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize()
        ) {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
            )
            Text(
                modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.payment_schedule_card_list_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Left
            )

            viewModel.uiState.clientCardList?.let { cardList ->
                LazyColumn(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                    items(cardList) { card ->
                        CustomInfoButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            imageModifier = Modifier.size(48.dp),
                            startIcon = R.drawable.ic_visa_card_item,
                            title = card?.detail ?: "",
                            subtitle = stringResource(
                                id = string.visa_card_masked_number,
                                card?.cardMaskedNumber?.takeLast(4) ?: 0
                            ),
                            endIcon = R.drawable.ic_right_chevron,
                            onEndIconClick = {
                                viewModel.onUIEvent(OnCardSelected(card))
                            },
                            onClick = {
                                viewModel.onUIEvent(OnCardSelected(card))
                            }

                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            CustomButton(
                text = stringResource(id = R.string.payment_schedule_card_list_add_card),
                modifier = Modifier
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.logEvents(AdjustEventType.SETTINGS_CTA_FIRST_START_FLOW_CARD_8007)
                    viewModel.onUIEvent(OnStopTimer)
                    val intent = Intent(context, ReactActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(APPLICATION_NAME, viewModel.reactApplicationName)
                    bundle.putString(VISA_USER_NAME, viewModel.reactUserName)
                    bundle.putString(VISA_USER_PASS, viewModel.reactUserPass)
                    bundle.putString(ENDPOINT, viewModel.reactEndPoint)
                    intent.putExtras(bundle)
                    addCardActivityResult.launch(intent)
                },
                buttonType = CustomButtonType.PrimaryTertiary,
                trailingIcon = R.drawable.ic_plus
            )
            if (viewModel.uiState.openDialog.isActive.value) {
                CustomDialog(
                    title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                    message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
                    positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                    negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                    openDialogCustom = viewModel.uiState.openDialog.isActive,
                    onPositiveAction = viewModel.uiState.openDialog.positiveAction,
                    onNegativeAction = viewModel.uiState.openDialog.negativeAction
                )
            }
        }
        LoadingIndicator(viewModel.uiState.isLoading)
    }
}
