package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditStatusView
import com.multimoney.data.util.catalog.CreditStatusView.CREDIT_STATUS_APPROVED
import com.multimoney.data.util.catalog.CreditStatusView.CREDIT_STATUS_PROCESS_STARTED
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_ACCEPT_CONTRACT_REFUSE_FIRST_TIME
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_ACCEPT_CONTRACT_REFUSE_SECOND_TIME
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_PROCESS_CREATE_ACCOUNT_FAILURE
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_PROCESS_MISSING_SIGNATURE
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_PROCESS_ON_FIDO_INCOMPLETE
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_PROCESS_SIGNATURE_REFUSE_FIRST_TIME
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_PROCESS_SIGNATURE_REFUSE_SECOND_TIME
import com.multimoney.multimoney.presentation.ui.home.product.credit.CreditProcessType.CREDIT_START_PROCESS_INCOMPLETE
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip

@Composable
@Preview
fun CardWithOutProduct(action: () -> Unit = {}) {
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

// Credit In Process
@Composable
@Preview
fun CardWithCreditInProcessAcceptContract() {
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
fun CreditApprovedOrStarted(creditStatusView: CreditStatusView, amount: Double? = 0.0) {
    val title: Int
    var description = ""
    val actionText: Int

    when (creditStatusView) {
        CREDIT_STATUS_APPROVED -> {
            title = R.string.home_product_credit_approved_card_title
            description = stringResource(id = R.string.home_product_credit_approved_card_description, amount ?: 0.0)
            actionText = R.string.home_product_credit_approved_card_action
        }
        CREDIT_STATUS_PROCESS_STARTED -> {
            title = R.string.home_product_credit_approved_process_started_card_title
            description = stringResource(id = R.string.home_product_credit_approved_process_started_card_description)
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


@Composable
@Preview
fun CardWithCreditInProcessOnFidoOrAbandonProcess(
    type: CreditProcessType = CREDIT_ACCEPT_CONTRACT_REFUSE_FIRST_TIME
) {
    val chipText = R.string.home_product_process_credit_label
    val title: Int
    val description: Int
    val actionText: Int
    when (type) {
        CREDIT_ACCEPT_CONTRACT_REFUSE_FIRST_TIME -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action
        }
        CREDIT_START_PROCESS_INCOMPLETE -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action
        }
        CREDIT_ACCEPT_CONTRACT_REFUSE_SECOND_TIME -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CREDIT_PROCESS_MISSING_SIGNATURE -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CREDIT_PROCESS_SIGNATURE_REFUSE_FIRST_TIME -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CREDIT_PROCESS_SIGNATURE_REFUSE_SECOND_TIME -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CREDIT_PROCESS_CREATE_ACCOUNT_FAILURE -> {
            title = R.string.home_product_process_title
            description = R.string.home_product_process_description
            actionText = R.string.home_product_process_action

        }
        CREDIT_PROCESS_ON_FIDO_INCOMPLETE -> {
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
            startIcon = R.drawable.ic_time,
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