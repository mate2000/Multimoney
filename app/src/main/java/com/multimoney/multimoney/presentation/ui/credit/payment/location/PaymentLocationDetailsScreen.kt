package com.multimoney.multimoney.presentation.ui.credit.payment.location

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.location.LocationDetailsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomLabelDescColumn
import com.multimoney.multimoney.presentation.uielement.Size
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentLocationDetailsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: LocationDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnCloseScreenClick) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .padding(horizontal = 24.dp)
                .background(MultimoneyTheme.colors.background)
        ) {
            // Location name
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 24.dp),
                text = viewModel.pointName,
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Address
            CustomLabelDescColumn(
                labelText = stringResource(id = R.string.payment_location_maps_address),
                descriptionText = viewModel.pointAddressDescription
            )

            // Opening time
            CustomLabelDescColumn(
                labelText = stringResource(id = R.string.payment_location_maps_opening_time),
                descriptionText = viewModel.pointSchedule
            )

            // Payment details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Payment Amount
                CustomLabelDescColumn(
                    labelText = stringResource(id = R.string.payment_location_maps_payment_amount),
                    descriptionText = viewModel.paymentAmount
                )
                // Payment ID
                CustomLabelDescColumn(
                    labelText = stringResource(id = R.string.payment_location_maps_payment_id),
                    descriptionText = viewModel.creditNumber
                )
            }

            // Information chip
            CustomInformativeChip(
                text = stringResource(id = viewModel.uiState.informativeText),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp),
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Size.Large
            )
        }

        CustomButton(
            onClick = {
                viewModel.onUIEvent(
                    UIEvent.OnNavigateMapsClick(
                        context = context,
                        latitude = viewModel.pointLatitude,
                        longitude = viewModel.pointLongitude
                    )
                )
            },
            text = stringResource(id = R.string.payment_location_maps_address_button),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = CustomButtonType.PrimaryPrimary
        )
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction,
            onNegativeAction = viewModel.uiState.dialogParameters.negativeAction
        )
    }

    BackHandler {
        viewModel.onUIEvent(UIEvent.OnNavigateBack)
    }
}
