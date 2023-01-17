package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
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
        TopNavBar(isRightButtonVisible = true,
            onLeftButtonClick = { viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnNavigateToHome) })
        if (viewModel.idBrand == Brand.ElSalvador.id) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.smart_transfer_my_contacts_title_SV),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.text
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
                    text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp),
                    style = Typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.smartCardTrending
                    )
                )
                CustomButton(
                    text = stringResource(id = R.string.smart_my_contacts_transfer_accounts_add),
                    onClick = {
                        viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnAddSACAccountClick)
                    },
                    buttonType = CustomButtonType.PrimaryTertiary
                )
            }
            Text(
                text = stringResource(
                    id = R.string.smart_mycontacts_transfer_title,
                    viewModel.uiState.relatedContactList.count()
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.smartCardTrending
                )
            )

        } else {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.smart_transfer_my_contacts_title_CR),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.text
                )
            )
            Text(
                text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.smartCardTrending
                )
            )
            Text(
                text = stringResource(
                    id = R.string.smart_mycontacts_total_transfer_title,
                    viewModel.uiState.relatedContactList.count()
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 8.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.smartCardTrending
                )
            )
        }
        ContactList(viewModel)
    }

    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    title: String? = "",
    subtitle: String = "",
    endIcon: Int? = R.drawable.ic_options,
    shouldCenterEndIcon: Boolean = true,
    onEndIconClick: () -> Unit = {}
) {

}

@Composable
fun ContactList(viewModel: MyContactsTransferViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.relatedContactList) { contact ->
            ContactItem(
                title = contact?.titular,
                subtitle = stringResource(
                    id = R.string.smart_my_concts_number_and_currency_content,
                    contact?.number ?: "", contact?.currency ?: "",
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                endIcon = R.drawable.ic_options,
                onEndIconClick = {
                    viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnAddSACAccountClick)
                }

            )
        }
    }
}