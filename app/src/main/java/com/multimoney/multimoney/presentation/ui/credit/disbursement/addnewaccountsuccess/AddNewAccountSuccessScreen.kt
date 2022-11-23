package com.multimoney.multimoney.presentation.ui.credit.disbursement.addnewaccountsuccess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun AddNewAccountSuccessScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: AddNewAccountSuccessViewModel = hiltViewModel()
) {

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }


    // View

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopNavBar(
            isRightButtonVisible = true,
            isLeftButtonVisible = false,
            onRightButtonClick = {
                viewModel.onUIEvent(
                    AddNewAccountSuccessViewModel.UIEvent.OnCloseClick(
                        focusManager
                    )
                )
            }
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(drawableResource = R.drawable.ic_success_symbol)
            CustomInformativeText(
                modifier = Modifier.padding(top = 32.dp),
                text = stringResource(id = R.string.disbursement_request_sent_successfully),
                textStyle = Typography.h4.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.W600,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                )
            )
            CustomInformativeText(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(id = R.string.disbursement_info_verification_wait),
                textStyle = Typography.body2.copy(
                    color = MultimoneyTheme.colors.labelText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center
                )
            )
        }
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            onClick = { viewModel.onUIEvent(AddNewAccountSuccessViewModel.UIEvent.OnNavigateBackToHome) },
            buttonType = CustomButtonType.PrimaryPrimary,
            text = stringResource(id = R.string.understood),
            enable = true
        )

    }
}