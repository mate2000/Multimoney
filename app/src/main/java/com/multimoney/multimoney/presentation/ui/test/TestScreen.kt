package com.multimoney.multimoney.presentation.ui.test

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import timber.log.Timber


@Composable
fun TestScreen(
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: TestViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
//        viewModel.mutationUserValidationUseCase()
        viewModel.executeNavigation(onNavigate = onNavigate)
    }

    // Display api response data
    TestScreen(viewModel.data)

    // Create start activity result for OnFido
    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onFidoHelper.getOnFidoClient().handleActivityResult(
                result.resultCode,
                result.data,
                object : Onfido.OnfidoResultListener {
                    override fun userCompleted(captures: Captures) {
                        Timber.d("ONFIDO", "Captured")
                    }

                    override fun userExited(exitCode: ExitCode) {
                        Timber.d("ONFIDO", "ExitCode")
                    }

                    override fun onError(exception: OnfidoException) {
                        Timber.d("ONFIDO", "OnfidoException")
                    }
                })

        }
    val focusManager = LocalFocusManager.current
    Column {
        ChartButton {
            viewModel.navigateToChart()
        }
        OnFidoButton {
            //launchOnFidoActivityResult.launch(viewModel.onFidoHelper.getOnFidoIntent())
            viewModel.mutationUserValidationUseCase()
        }
        var textValue by remember { mutableStateOf("Hello World Invisible") }
        CustomOutlinedTextField(
            value = textValue,
            placeHolder = "Prueba",
            onValueChange = { textValue = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            isPassword = true
        )
        var checked by remember { mutableStateOf(true) }
        CustomCheckBox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}

@Composable
fun ChartButton(navigateToChart: () -> Unit) {
    CustomButton(
        onClick = navigateToChart,
        text = "Chart"
    )
}

@Composable
fun OnFidoButton(navigateToChart: () -> Unit) {
    Button(onClick = navigateToChart, content = {
        Text(text = "OnFido")
    })
}

/**
 * Defining the compose screen without viewModel,
 * passing the data directly as parameters makes
 * ui testing easier
 */
@Composable
fun TestScreen(
    data: UserData?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        data?.let {
            Text(text = data.firstName ?: "", color = MaterialTheme.colors.onBackground)
        }
    }
}