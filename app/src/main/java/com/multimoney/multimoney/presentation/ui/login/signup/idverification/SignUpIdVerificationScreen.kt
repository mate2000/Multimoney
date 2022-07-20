package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.DialogParameters

@Composable
@Preview
fun SignUpIdVerificationScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpIdVerificationViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            viewModel.onUIEvent(
                SignUpIdVerificationViewModel.UIEvent.OnOpenOnFidoSdk(
                    result,
                    onOnFidoCompleted = {
                        sharedViewModel.apply {
                            onUIEvent(
                                SignUpViewModel.UIEvent.OnUseDataValueChange(
                                    userData = userData?.copy(
                                        currentStep = SignUpStep.Six.name
                                    )
                                )
                            )
                            onUIEvent(SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase)
                            onUIEvent(SignUpViewModel.UIEvent.OnOnFidoVerifiedChanged(true))
                        }
                    },
                    onOnFidoError = {
                        sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnOpenDialogValueChange(it))
                    },
                    onContinueEnable = {
                        sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueEnable(it))
                    }
                )
            )
        }

    viewModel.onUIEvent(
        SignUpIdVerificationViewModel.UIEvent.OnInitValues(
            false,
            stringResource(id = R.string.placeholder_error)
        )
    )

    LaunchedEffect(context) {
        sharedViewModel.onUIEvent(
            SignUpViewModel.UIEvent.OnSetNavigation(
                nextStep = SignUpStep.Six.id,
                previousStep = SignUpStep.Three.id
            )
        )
        viewModel.onFidoTokenEvent.collect { event ->
            event.onSuccess {
                sharedViewModel.apply {
                    onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(false))
                    onUIEvent(SignUpViewModel.UIEvent.OnContinueEnable(true))
                    onUIEvent(SignUpViewModel.UIEvent.OnSetNavigation(nextAction = {
                        launchOnFidoActivityResult.launch(
                            viewModel.onFidoHelper.getOnFidoIntent(
                                it?.sdkToken ?: "",
                                onRefreshToke = { refreshToken ->
                                    viewModel.onUIEvent(
                                        SignUpIdVerificationViewModel.UIEvent.RefreshOnFidoToken(
                                            sharedViewModel.userData,
                                            context.packageName,
                                            refreshToken
                                        )
                                    )
                                }
                            )
                        )
                    }, nextStep = SignUpStep.Six.id, previousStep = SignUpStep.Three.id))
                }
            }.onLoading {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueEnable(false))
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(true))
            }.onFailure {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueEnable(false))
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            SignUpIdVerificationViewModel.UIEvent.OnCallInFidoToken(
                sharedViewModel.userData,
                context.packageName
            )
        )
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