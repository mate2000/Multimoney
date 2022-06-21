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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.BaseEvent.OnOnFidoCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnInitValues
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.uielement.CustomImage
import kotlinx.coroutines.flow.collectLatest

@Composable
@Preview
fun SignUpIdVerificationScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpIdVerificationViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(true) {
//        viewModel.onUIEvent(
//            OnCallInFidoToken(
//                sharedViewModel.userData?.firstName ?: "",
//                sharedViewModel.userData?.lastName ?: "",
//                sharedViewModel.userData?.email ?: "",
//                context.packageName,
//                Brand.Revamp.id,
//                sharedViewModel.userData?.email ?: ""
//            )
//        )
        sharedViewModel.onUIEvent(OnContinueEnable(true))
        viewModel.onUIEvent(
            OnCallInFidoToken(
                "Diego",
                "Sanchez",
                "diegomm2@gmail.com",
                context.packageName,
                Brand.Revamp.id,
                "diegomm2@gmail.com"
            )
        )
    }

    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            viewModel.onUIEvent(OnOpenOnFidoSdk(result))
        }

    LaunchedEffect(true) {
        viewModel.baseEvent.collectLatest { event ->
            when (event) {
                is OnOnFidoCompleted -> sharedViewModel.nextStep()
            }
        }
        viewModel.onFidoTokenEvent.collectLatest { event ->
            event.onSuccess {

                sharedViewModel.onUIEvent(OnLoadingValueChange(false))
            }.onLoading {
                sharedViewModel.onUIEvent(OnLoadingValueChange(true))
            }.onFailure {
                sharedViewModel.onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = viewModel.uiState.onFidoTokenFailure
                    )
                )
            }
        }
    }

    LaunchedEffect(viewModel.uiState.onFidoTokenSuccess) {
        if (viewModel.isFirstLaunch.not()) {
            sharedViewModel.nextAction = {
                launchOnFidoActivityResult.launch(
                    viewModel.onFidoHelper.getOnFidoIntent(
                        viewModel.uiState.onFidoTokenSuccess.second?.sdkToken ?: "",
//                    viewModel.onRefreshToken(
//                        sharedViewModel.userData?.firstName ?: "",
//                        sharedViewModel.userData?.lastName ?: "",
//                        sharedViewModel.userData?.email ?: "",
//                        context.packageName,
//                        Brand.Revamp.id,
//                        sharedViewModel.userData?.email ?: ""
//                    )
                        viewModel.onRefreshToken(
                            "Diego",
                            "Sanchez",
                            "diegomm2@gmail.com",
                            context.packageName,
                            Brand.Revamp.id,
                            "diegomm2@gmail.com"
                        )
                    )
                )
            }
        }
    }

    viewModel.onUIEvent(OnInitValues(false, stringResource(id = R.string.placeholder_error)))

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