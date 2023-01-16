package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun MyContactsTransferScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: MyContactsTransferViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnCallQueryRelatedContactsByPhoneUseCaseImp)
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            isRightButtonVisible = true,
            onLeftButtonClick = { viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnNavigateToHome)}
        )
        if (viewModel.idBrand == Brand.CostaRica.id.toString()) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.smart_transfer_my_contacts_title),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.text
                )
            )
        } else {

        }
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.smart_iban_transfer_accounts_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.smart_iban_transfer_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.smartCardTrending
                )
            )
            CustomButton(
                text = stringResource(id = R.string.smart_iban_transfer_accounts_add),
                onClick = {
                    viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnAddSACAccountClick)
                },
                buttonType = CustomButtonType.PrimaryTertiary
            )
        }

        ContactListCR()
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onDismissAction = viewModel.uiState.openDialog.dismissAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }

    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun ContactItem() {

}

@Composable
fun ContactListCR() {
}

@Composable
fun ContactListSV() {
}