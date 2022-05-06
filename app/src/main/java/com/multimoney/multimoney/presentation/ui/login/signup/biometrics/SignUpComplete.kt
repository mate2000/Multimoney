package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignUpComplete(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SignUpCompleteViewModel = hiltViewModel()
) {
    Column(Modifier.fillMaxSize()) {
        CustomButton(
            text = stringResource(id = R.string.finalize),
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .weight(0.64f),
            onClick = { viewModel.navigateTo(Screen.SignInScreen.route) }
        )
    }
}