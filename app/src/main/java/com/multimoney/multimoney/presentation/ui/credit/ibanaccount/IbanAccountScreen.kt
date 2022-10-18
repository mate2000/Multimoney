package com.multimoney.multimoney.presentation.ui.credit.ibanaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.VisualTransformationMasks
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun IbanAccountScreen(
    sharedViewModel: CreditViewModel,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: IbanAccountViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnUpdateUserInfo(
                sharedViewModel.identification,
                sharedViewModel.email,
                sharedViewModel.idBrand
            )
        )

        viewModel.baseEvent.collect { event ->
            when (event) {
                is CreditAmountViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnContinueEnable(
                        event.isFormValid
                    )
                )
            }
        }
    }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.iban_account_tile),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
            color = MultimoneyTheme.colors.labelText
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 32.dp),
            value = viewModel.uiState.accountNumber,
            leadingIconComposable = { tint ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_account),
                        contentDescription = "",
                        tint = tint
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.iban_account_cr),
                        style = Typography.body2.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = tint
                        ),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = false,
            placeHolder = stringResource(id = R.string.iban_account_hint),
            onValueChange = {
                viewModel.onUIEvent(
                    IbanAccountViewModel.UIEvent.OnIncomeValueChange(
                        it
                    )
                )
            },
            canShowNonErrorMessage = true,
            isError = viewModel.uiState.incomeError.first,
            errorMessage = stringResource(id = viewModel.uiState.incomeError.second),
            customTransformation = MaskVisualTransformation(
                VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.mask,
                VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.maskChar
            )
        )
    }
}