package com.multimoney.multimoney.presentation.ui.payment.location

import androidx.compose.foundation.background
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
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.Size
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun LocationDetailsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: LocationDetailsViewModel = hiltViewModel()
) {

//    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar()

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
                    .padding(top = 54.dp, bottom = 24.dp),
                text = viewModel.locationName,
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Address
            LocationInfo(
                title = stringResource(id = R.string.payment_location_maps_address),
                text = viewModel.locationAddress
            )

            // Opening time
            LocationInfo(
                title = stringResource(id = R.string.payment_location_maps_opening_time),
                text = viewModel.locationOpeningTime
            )

            // Payment details
            Row(modifier = Modifier.fillMaxWidth()) {
                // Payment Amount
                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                    LocationInfo(
                        title = stringResource(id = R.string.payment_location_maps_payment_amount),
                        text = viewModel.paymentAmount
                    )
                }
                // Payment ID
                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                    LocationInfo(
                        title = stringResource(id = R.string.payment_location_maps_payment_id),
                        text = viewModel.paymentId
                    )
                }
            }

            // Information chip
            CustomInformativeChip(
                text = stringResource(id = R.string.payment_location_maps_info),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp),
                background = WhiteTransparency10,
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Size.Large
            )
        }

        CustomButton(
            onClick = {viewModel.onUIEvent(LocationDetailsViewModel.UIEvent.OnNavigateMapsClick(
                context = context,
                latitude = viewModel.latitude,
                longitude = viewModel.longitude
            ))},
            text = stringResource(id = R.string.payment_location_maps_address_button),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = CustomButtonType.PrimaryPrimary,
        )
    }
}

@Composable
fun LocationInfo(title: String = "", text: String = "") {
    Text(
        text = title,
        style = Typography.body1.copy(
            color = MultimoneyTheme.colors.text,
            fontWeight = FontWeight.SemiBold
        ),
        color = MultimoneyTheme.colors.quickActionLabelColor
    )
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 16.dp),
        style = Typography.body1.copy(
            fontWeight = FontWeight.SemiBold,
            color = MultimoneyTheme.colors.text,
        ),
        color = MultimoneyTheme.colors.labelText
    )
}