package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartSelectSendingTypeScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartSelectSendingTypeViewModel = hiltViewModel()
){
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {  },
            isRightButtonVisible = false
        )
        SendingTypeOptions()
    }
    BackHandler {}
}

@Composable
fun SendingTypeOptions(

){
    Column {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = "Enviar dinero a"
        )
        CustomInfoButton(
            title = ""
        )
        CustomInfoButton(
            title = ""
        )
        CustomInfoButton(
            title = ""
        )
    }

}