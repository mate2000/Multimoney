package com.multimoney.multimoney.presentation.ui.login.signup.completed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnSetupDeviceInfo
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnSignIn
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getDeviceName
import com.multimoney.multimoney.presentation.util.getDeviceType
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
fun SignUpCompleted(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpCompletedViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {
    val fragmentActivity = LocalContext.current as FragmentActivity
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            onUIEvent(
                OnSetupDeviceInfo(
                    getDeviceName(fragmentActivity) ?: "",
                    getDeviceType(fragmentActivity).value
                )
            )
            onUIEvent(OnSignIn)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(
            drawableResource = drawable.ic_logo_multimoney2,
            modifier = Modifier
                .wrapContentSize()
                .size(200.dp, 72.dp)
        )
        Text(
            text = stringResource(
                id = when (sharedViewModel.idBrand) {
                    Brand.Mexico.id -> R.string.sign_up_complete_title_mx
                    else -> R.string.sign_up_complete_title
                }
            ),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText,
            textAlign = TextAlign.Center
        )
    }

    BackHandler {}

    viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.SignUpSuccess)
}
