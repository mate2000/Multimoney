package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.SemanticPositive800
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStartedStatus.CreditStatusApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditApprovedOrStartedStatus.CreditStatusProcessStarted
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditAcceptContractRefuseFirstTime
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditAcceptContractRefuseSecondTime
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessMissingSignature
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessSignatureRefuseFirstTime
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditProcessSignatureRefuseSecondTime
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip

/**
 * Composable function to show the option to active smart product
 */
@Composable
@Preview
fun CardSmartProduct(action: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .clickable { action.invoke() }
    ) {
        Text(
            text = stringResource(id = R.string.home_product_not_approved_title),
            modifier = Modifier.padding(top = 20.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = stringResource(id = R.string.home_product_not_approved_description),
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.home_product_not_approved_action),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
        )
    }
}

/**
 * Composable to handle the status without credit GT
 */
@Composable
@Preview
fun CardGTWithoutCredit() {
    Column(
        modifier = Modifier
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_description),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.home_product_gt_with_out_credit_action),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}

// Credit In Process
/**
 * Composable function handle to status of the credit flow
 *
 * 1- Credit Approved
 * 2- Credit Started
 *
 */
@Composable
fun CreditApprovedOrStarted(
    creditApprovedOrStartedStatus: CreditApprovedOrStartedStatus,
    amount: String? = "0.0",
    idBrand: Int
) {
    val title: Int
    var description = ""
    val actionText: Int

    when (creditApprovedOrStartedStatus) {
        CreditStatusApproved -> {
            title = R.string.home_product_credit_approved_card_title
            description = if (idBrand == Brand.Guatemala.id) {
                stringResource(id = R.string.home_product_gt_credit_approved_card_description, amount ?: "0.0")
            } else {
                stringResource(id = R.string.home_product_credit_approved_card_description, amount ?: "0.0")
            }
            actionText = R.string.home_product_credit_approved_card_action
        }
        CreditStatusProcessStarted -> {
            title = R.string.home_product_credit_approved_process_started_card_title
            description =
                stringResource(id = R.string.home_product_credit_approved_process_started_card_description)
            actionText = R.string.home_product_credit_approved_process_started_card_action
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(top = 20.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = actionText),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
        )
    }
}

/**
 * Composable function show the status of Validation in Process
 */
@Composable
@Preview
fun CardCreditValidationInProcess() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_product_process_credit_label),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 42.dp),
            shape = RoundedCornerShape(12.dp),
            background = BlackTransparency20,
            startIcon = R.drawable.ic_warning
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_description),
            modifier = Modifier.padding(top = 8.dp, bottom = 42.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

@Composable
@Preview
fun CardCreditOnFidoRequired() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_product_process_credit_label),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 42.dp),
            shape = RoundedCornerShape(12.dp),
            background = BlackTransparency20,
            startIcon = R.drawable.ic_warning
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_product_process_accept_contract_description),
            modifier = Modifier.padding(top = 8.dp, bottom = 42.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}


@Composable
@Preview
fun CardWithCreditInProcess(
    type: CreditProcessStarted = CreditAcceptContractRefuseFirstTime,
    action: () -> Unit = {},
) {
    val chipText = R.string.home_product_process_credit_label
    val title: Int
    val description: Int
    val actionText: Int
    var startIcon = R.drawable.ic_time
    when (type) {
        CreditAcceptContractRefuseFirstTime -> {
            title = R.string.home_credit_sign_document_reject_title
            description = R.string.home_credit_sign_document_reject_description
            actionText = R.string.home_credit_sign_document_reject_action_text
        }
        CreditAcceptContractRefuseSecondTime -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action
        }
        CreditStartProcessIncomplete -> {
            title = R.string.home_product_credit_not_completed_title
            description = R.string.home_product_credit_not_completed_description
            actionText = R.string.home_product_credit_not_completed_action
            startIcon = R.drawable.ic_warning
        }
        CreditProcessMissingSignature -> {
            title = R.string.home_product_credit_signature_missing_title
            description = R.string.home_product_credit_signature_missing_description
            actionText = R.string.home_product_credit_signature_missing_action

        }
        CreditProcessSignatureRefuseFirstTime -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CreditProcessSignatureRefuseSecondTime -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CreditProcessCreateAccountFailure -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CreditProcessOnFidoIncomplete -> {
            title = R.string.home_on_fido_pending_title
            description = R.string.home_on_fido_pending_description
            actionText = R.string.home_on_fido_pending_action_text

        }
    }

    Column(
        modifier = Modifier
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                action.invoke()
            }
    ) {
        CustomInformativeChip(
            text = stringResource(id = chipText),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = BlackTransparency20,
            startIcon = startIcon,
            startIconTint = MultimoneyTheme.colors.iconColor
        )
        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = description),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 21.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = actionText),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun CardCreditMaxAttempts(
    action: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .clickable { action.invoke() }
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_product_process_credit_label),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = MultimoneyTheme.colors.chipBackground,
            startIcon = R.drawable.ic_warning
        )
        Text(
            text = stringResource(id = R.string.sign_credit_max_attempts_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.sign_credit_max_attempts_message),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 21.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = stringResource(id = R.string.sign_credit_max_attempts_contact),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OngoingCredit(
    action: () -> Unit = {},
    progress: Float,
    viewModel: ProductViewModel
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 12.dp, start = 24.dp, end = 24.dp)
        .clickable { action.invoke() }) {
        Text(
            text = stringResource(id = R.string.home_product_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        viewModel.balanceCredit?.getSummary()?.currentBalanceLabel
        Text(
            text = viewModel.balanceCredit?.getSummary()?.availableBalanceLabel.toString(),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
                progress = 0.7f,
                backgroundColor = SemanticPositive800,
                color = DefaultWhite)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Saldo $234,234",
                modifier = Modifier.padding(top = 14.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
            Text(
                text = " de $234,234",
                modifier = Modifier.padding(top = 14.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.textSubhead
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)) {
            Column(modifier = Modifier.weight(0.5F)) {
                Text(
                    text = "Cuota",
                    modifier = Modifier.padding(top = 14.dp),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    text = viewModel.balanceCredit?.getSummary()?.monthlyQuotaLabel.toString(),
                    modifier = Modifier.padding(),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
            }
            Column(modifier = Modifier.weight(0.5F)) {
                Text(
                    text = "Vence",
                    modifier = Modifier.padding(top = 14.dp),
                    style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Chip(
                    enabled = false,
                    colors = ChipDefaults.chipColors(disabledBackgroundColor = SemanticPositive800,
                        disabledContentColor = MultimoneyTheme.colors.text),
                    modifier = Modifier
                        .height(28.dp)
                        .padding(top = 2.dp),
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Primary300)
                        )
                    },
                    onClick = {
                        //Empty on purpose
                    },
                    content = {
                        Text(
                            text = viewModel.balanceCredit?.getSummary()?.paymentDateLabel.toString(),
                            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                )
            }
        }
    }
}

sealed class CreditProcessStarted {
    object CreditAcceptContractRefuseFirstTime : CreditProcessStarted()
    object CreditStartProcessIncomplete : CreditProcessStarted()
    object CreditAcceptContractRefuseSecondTime : CreditProcessStarted()
    object CreditProcessMissingSignature : CreditProcessStarted()
    object CreditProcessSignatureRefuseFirstTime : CreditProcessStarted()
    object CreditProcessSignatureRefuseSecondTime : CreditProcessStarted()
    object CreditProcessCreateAccountFailure : CreditProcessStarted()
    object CreditProcessOnFidoIncomplete : CreditProcessStarted()
}

sealed class CreditApprovedOrStartedStatus {
    object CreditStatusApproved : CreditApprovedOrStartedStatus()
    object CreditStatusProcessStarted : CreditApprovedOrStartedStatus()
}