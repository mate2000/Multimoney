package com.multimoney.multimoney.presentation.ui.visa.card

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_BRAND
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButtonBig
import com.multimoney.multimoney.presentation.uielement.CustomCardVisaVertical
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun VisaCardScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopBackStack: () -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: VisaCardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .weight(0.11f)
        ) {
            TopNavBar(
                rightButtonIcon = R.drawable.ic_gear,
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                onRightButtonClick = {
                    // TODO: Execute action when implemented
                    Toast.makeText(
                        context, "TBD3", Toast.LENGTH_LONG
                    ).show()
                })
        }
        CustomCardVisaVertical(
            modifier = Modifier
                .weight(0.45f)
                .padding(start = 68.dp, end = 68.dp)
                .fillMaxSize(),
            isTextVisible = viewModel.uiState.isTextVisible
        )
        Column(
            Modifier
                .fillMaxWidth()
                .weight(0.09f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomInformativeChip(
                text = stringResource(
                    id = R.string.visa_card_available_amount,
                    viewModel.uiState.availableAmount
                ),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    viewModel.onUIEvent(
                        UIEvent.OnAvailableAmountClick(
                            navBackStackEntry.arguments?.getString(
                                ID_BRAND,
                                ""
                            ) ?: ""
                        )
                    )
                },
                shape = RoundedCornerShape(24.dp),
                background = WhiteTransparency10,
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Large
            )
        }
        Column(
            Modifier
                .background(MultimoneyTheme.colors.backgroundBottomOptions)
                .weight(0.35f)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (viewModel.uiState.isNfcAvailable) {
                    CustomButtonBig(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        icon = R.drawable.ic_link,
                        text = stringResource(id = R.string.link),
                        onClick = {
                            // TODO: Execute action when implemented
                            Toast.makeText(
                                context, "TBD1", Toast.LENGTH_LONG
                            ).show()
                        })
                }
                CustomButtonBig(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    icon = R.drawable.ic_eye,
                    text = stringResource(id = R.string.see_data),
                    onClick = {
                        // TODO: Execute action when implemented
                        Toast.makeText(
                            context, "TBD2", Toast.LENGTH_LONG
                        ).show()
                    })
                CustomButtonBig(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    icon = R.drawable.ic_locked,
                    text = stringResource(id = R.string.locked),
                    onClick = {
                        // TODO: Execute action when implemented
                        Toast.makeText(
                            context, "TBD3", Toast.LENGTH_LONG
                        ).show()
                    })
            }
        }
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}