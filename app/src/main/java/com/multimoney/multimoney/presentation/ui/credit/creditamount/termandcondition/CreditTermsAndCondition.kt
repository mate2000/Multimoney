package com.multimoney.multimoney.presentation.ui.credit.creditamount.termandcondition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreditTermAndCondition(
    onAcceptTermsAndCondition: () -> Unit,
    isActive: MutableState<Boolean>,
) {
    Dialog(
        onDismissRequest = {
            isActive.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BackCloseNavBar(isBackVisible = false, onCloseClick = {
                isActive.value = false
            })
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.credit_terms_and_condition_title),
                    modifier = Modifier.padding(top = 8.dp),
                    style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                Text(
                    text = stringResource(id = R.string.credit_terms_and_condition_description),
                    modifier = Modifier.padding(top = 16.dp),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.text
                )
            }
            CustomButton(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.accept),
                buttonType = PrimaryPrimary,
                onClick = {
                    isActive.value = false
                    onAcceptTermsAndCondition()
                }
            )
        }
    }

    BackHandler {
        isActive.value = false
    }
}