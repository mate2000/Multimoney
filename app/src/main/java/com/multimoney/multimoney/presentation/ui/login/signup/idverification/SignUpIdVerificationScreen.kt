package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures

@Composable
@Preview
fun SignUpIdVerificationScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {
    sharedViewModel.isContinueEnabled = true
    val onfidoError = stringResource(id = R.string.placeholder_error)

    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            sharedViewModel.onFidoHelper.getOnFidoClient().handleActivityResult(
                result.resultCode,
                result.data,
                object : Onfido.OnfidoResultListener {
                    override fun userCompleted(captures: Captures) {
                        sharedViewModel.nextStep()
                    }

                    override fun userExited(exitCode: ExitCode) {
                        // Empty on purpose
                    }

                    override fun onError(exception: OnfidoException) {
                        sharedViewModel.openDialog = DialogParameters(
                            description = onfidoError,
                            isActive = mutableStateOf(true)
                        )
                    }
                })
        }

    LaunchedEffect(true) {
        sharedViewModel.nextAction = {
            launchOnFidoActivityResult.launch(
                sharedViewModel.onFidoHelper.getOnFidoIntent()
            )
        }
    }

    Column(
        Modifier.padding(end = 16.dp, start = 16.dp, top = 28.dp)
    ) {
        Text(
            text = stringResource(id = R.string.sign_up_id_validation_title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.sign_up_id_validation_subtitle),
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.textSubhead,
                fontWeight = FontWeight.SemiBold
            )
        )

        Row(
            Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.sign_up_id_validation_one),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.textSubhead,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Row(
            Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.sign_up_id_validation_two),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.textSubhead,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Row(
            Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.sign_up_id_validation_three),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.textSubhead,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}