package com.multimoney.multimoney.presentation.ui.credit.origination.document

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
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.Companion.PACKAGE_NAME
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
@Preview
fun CreditDocumentScreen(
    viewModel: CreditDocumentViewModel = hiltViewModel(),
    sharedViewModel: CreditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val launchOnFidoActivityResult =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            viewModel.onUIEvent(
                OnOpenOnFidoSdk(
                    result,
                    onOnFidoCompleted = {
                        sharedViewModel.onUIEvent(
                            OnNextStep
                        )
                    },
                    onOnFidoError = {
                        sharedViewModel.onUIEvent(
                            OnOpenDialogValueChange(
                                it
                            )
                        )
                    },
                    onContinueEnable = {
                        sharedViewModel.onUIEvent(
                            CreditViewModel.UIEvent.OnContinueEnable(
                                it
                            )
                        )
                    }
                )
            )
        }

    LaunchedEffect(context) {
        viewModel.onFidoTokenEvent.collect { event ->
            event.onSuccess {
                sharedViewModel.apply {
                    onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(false))
                    onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(true))
                    onUIEvent(
                        CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
                            launchOnFidoActivityResult.launch(
                                viewModel.onFidoHelper.getOnFidoIntent(
                                    sharedViewModel.idBrand.toInt(),
                                    AppFlow.CREDIT_ORIGINATION,
                                    it?.sdkToken ?: "",
                                    onRefreshToke = { refreshToken ->
                                        viewModel.onUIEvent(
                                            RefreshOnFidoToken(
                                                UserData(
                                                    pkUser = sharedViewModel.pkUser,
                                                    identification = sharedViewModel.identification,
                                                    userName = sharedViewModel.email,
                                                    firstName = sharedViewModel.firstName,
                                                    firstLastName = sharedViewModel.lastName,
                                                    email = sharedViewModel.email
                                                ),
                                                PACKAGE_NAME,
                                                refreshToken
                                            )
                                        )
                                    }
                                )
                            )
                        }, nextStep = CreditStep.Eight.id, previousStep = CreditStep.Six.id)
                    )
                }
            }.onLoading {
                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(false))
                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(true))
            }.onFailure {
                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(false))
                sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnFailureWithDialog(
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
            OnCallInFidoToken(
                UserData(
                    pkUser = sharedViewModel.pkUser,
                    identification = sharedViewModel.identification,
                    userName = sharedViewModel.email,
                    firstName = sharedViewModel.firstName,
                    firstLastName = sharedViewModel.lastName,
                    email = sharedViewModel.email
                ),
                PACKAGE_NAME
            )
        )
    }

    Column(
        Modifier.padding(end = 16.dp, start = 16.dp, top = 28.dp)
    ) {
        Text(
            text = stringResource(id = string.sign_up_id_validation_title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = string.sign_up_id_validation_subtitle),
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )

        Row(
            Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            CustomImage(
                drawableResource = drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = string.sign_up_id_validation_one),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.text,
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
                drawableResource = drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = string.sign_up_id_validation_two),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.text,
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
                drawableResource = drawable.ic_validation,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = string.sign_up_id_validation_three),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
