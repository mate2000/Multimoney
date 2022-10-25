package com.multimoney.multimoney.presentation.ui.payment.paymentvoucher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaymentVoucherScreen(
    paymentVoucherViewModel: PaymentVoucherViewModel = hiltViewModel()
) {
    val view = LocalView.current
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
}
