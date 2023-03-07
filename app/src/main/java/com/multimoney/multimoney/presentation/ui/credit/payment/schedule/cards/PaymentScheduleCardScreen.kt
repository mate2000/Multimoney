package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.ReactActivity
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.APPLICATION_NAME
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.ENDPOINT
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.RESPONSE_IS_ERROR
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.RESPONSE_VALUE
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.RESULT_CODE_PROCESS_FINISHED
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.RESULT_CODE_PROCESS_INCOMPLETE
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.VISA_USER_NAME
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.Companion.VISA_USER_PASS
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnEditCardVisaDirect
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnResumeTimer
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnStopTimer
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentScheduleCardScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentScheduleCardViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            onUIEvent(OnStart)
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }

    PaymentScheduleContent(viewModel)
}

@Composable
@Preview
fun PaymentScheduleContent(
    viewModel: PaymentScheduleCardViewModel = hiltViewModel()
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
                descriptionString = if (isAlertResultSuccess) {
                    stringResource(id = alertResultDescriptionResource, day)
                } else {
                    alertResultDescription
                },
                buttonTextResource = alertResultButtonResource,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnAlertCloseClick) },
                onButtonClick = { viewModel.onUIEvent(OnAlertButtonClick) }
            )
        }
    } else {
        if (viewModel.uiState.isCardListEmpty) {
            PaymentScheduleCardEmptyState(viewModel)
        } else {
            PaymentScheduleCard(viewModel)
        }
    }

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
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
private fun PaymentScheduleCard(viewModel: PaymentScheduleCardViewModel) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
            )

            CustomInformativeText(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 7.dp, end = 16.dp),
                trailingIcon = drawable.ic_information_chip,
                iconSize = 44.dp,
                trailingIconClick = { viewModel.onUIEvent(OnOpenDisclaimerDialog) },
                text = stringResource(id = string.payment_schedule_card_title),
                textStyle = Typography.h6.copy(color = MultimoneyTheme.colors.text)
            )

            viewModel.uiState.cardVisaDirect?.apply {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = string.payment_schedule_card_label_origin),
                    style = Typography.body1.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                CustomInfoButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    imageModifier = Modifier.size(48.dp),
                    startIcon = drawable.ic_visa_card_item,
                    title = detail ?: "",
                    subtitle = stringResource(
                        id = string.visa_card_masked_number,
                        cardMaskedNumber?.takeLast(4) ?: 0
                    ),
                    endIcon = drawable.ic_edit_green,
                    onEndIconClick = {
                        viewModel.onUIEvent(OnEditCardVisaDirect)
                    }
                )
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = string.payment_schedule_card_label_date),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.quickActionLabelColor
                    )
                )

                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(
                        id = string.payment_schedule_card_label_date_description,
                        viewModel.uiState.day
                    ),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        Column {
            CustomButton(
                onClick = { viewModel.onUIEvent(OnProgramClick) },
                text = stringResource(id = string.payment_schedule_card_button),
                modifier = Modifier.padding(bottom = 32.dp, top = 16.dp, start = 16.dp, end = 16.dp).fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
    }
}

@Composable
@Preview
fun PaymentScheduleCardEmptyState(
    viewModel: PaymentScheduleCardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val addCardActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
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
                viewModel.onUIEvent(OnNavigateBack)
            }
            else -> return@rememberLauncherForActivityResult
        }
    }

    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background).fillMaxSize()
    ) {
        TopNavBar(isLeftButtonVisible = false, onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) })
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.payment_schedule_card_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.wrapContentSize())
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomImage(
                    drawableResource = drawable.ic_visa_cards_empty_state
                )
                Text(
                    text = stringResource(id = R.string.payment_cards_list_empty_state_description),
                    modifier = Modifier.padding(vertical = 25.dp, horizontal = 58.dp),
                    style = Typography.body1,
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Center
                )
            }
            CustomButton(
                onClick = {
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
                text = stringResource(id = R.string.payment_cards_list_create),
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary,
                enable = viewModel.uiState.isLoading.not()
            )
        }
    }
}
