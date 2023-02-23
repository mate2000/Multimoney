package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.AlertResult

@Composable
fun MaintenanceAlertScreen(
    onBackToHomeAction: () -> Unit = {}
) {
    AlertResult(
        iconResource = R.drawable.ic_alert,
        titleString = stringResource(id = R.string.home_maintenance_state_title),
        descriptionString = stringResource(R.string.home_maintenance_state_description),
        buttonTextResource = R.string.home_maintenance_btn_text,
        isRightButtonVisible = true,
        isLeftButtonVisible = false,
        onButtonClick = onBackToHomeAction,
        onRightButtonClick = onBackToHomeAction
    )
    BackHandler(onBack = onBackToHomeAction)
}