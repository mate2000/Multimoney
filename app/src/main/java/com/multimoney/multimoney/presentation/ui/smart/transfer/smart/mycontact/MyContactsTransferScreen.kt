package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnAddToFavoriteAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnContactClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnQueryValueChange
import com.multimoney.multimoney.presentation.uielement.ContactItem
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomSearchBar
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import java.util.SortedMap

@Composable
fun MyContactsTransferScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: MyContactsTransferViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnCallQueryRelatedContactsByPhoneUseCase)
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
            onRightButtonClick = { viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnNavigateToHome) }
        )
        if (viewModel.idBrand == Brand.ElSalvador.id) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.smart_transfer_my_contacts_title_SV),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.onBoardingTitleText
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp),
                    style = Typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.bodyTextColor
                    )
                )
                CustomButton(
                    modifier = Modifier.padding(end = 4.dp),
                    text = stringResource(id = R.string.smart_my_contacts_transfer_accounts_add),
                    onClick = {
                        viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnAddSACAccountClick)
                    },
                    buttonType = CustomButtonType.PrimaryTertiary
                )
            }
        } else {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.smart_transfer_my_contacts_title_CR),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.onBoardingTitleText
                )
            )
            Text(
                text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.bodyTextColor
                )
            )
        }
        Text(
            text = stringResource(
                id = R.string.smart_mycontacts_total_transfer_title,
                viewModel.uiState.relatedContactList.count()
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 16.dp, start = 16.dp),
            style = Typography.caption.copy(
                fontWeight = FontWeight.Normal,
                color = MultimoneyTheme.colors.smartCardTrending
            )
        )
        CustomSearchBar(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
                .clip(shape = RoundedCornerShape(30))
                .background(MultimoneyTheme.colors.background),
            value = viewModel.uiState.queryValue,
            placeHolder = stringResource(id = R.string.smart_my_concts_placeholder),
            onValueChange = {
                viewModel.onUIEvent(OnQueryValueChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        ContactList(
            contactList = viewModel.uiState.relatedContactList,
            searchedString = viewModel.uiState.queryValue,
            onEndIconClick = { contact ->
                viewModel.onUIEvent(
                    OnAddToFavoriteAccountClick(contact)
                )
            },
            onContactClick = { accounts ->
                viewModel.onUIEvent(OnContactClick(accounts))
            }
        )
    }
    ContactBottomSheet(viewModel = viewModel)
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun ContactList(
    contactList: SortedMap<String, List<PhoneSmart>>,
    searchedString: String,
    onEndIconClick: (contact: PhoneSmart) -> Unit,
    onContactClick: (contact: List<PhoneSmart>) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
        contactList.forEach { (_, contact) ->
            item {
                val subtitleColor =
                    arrayOf(
                        MultimoneyTheme.colors.twoCharacterOneColor,
                        MultimoneyTheme.colors.twoCharacterTwoColor,
                        MultimoneyTheme.colors.twoCharacterTwoColor,
                        MultimoneyTheme.colors.twoCharacterComplementaryTwoColor
                    )

                val color by remember {
                    mutableStateOf(subtitleColor.random())
                }
                if (contact.first().titular.contains(searchedString, true)) {
                    ContactItem(
                        title = contact.first().titular,
                        subtitle = contact.first().number,
                        modifier = Modifier.fillMaxWidth(),
                        endIcon = R.drawable.ic_options,
                        onEndIconClick = {
                            onEndIconClick(contact.first())
                        },
                        colorSubtitle = color,
                        onClick = {
                            onContactClick(contact)
                        }
                    )
                    Divider(color = MultimoneyTheme.colors.dividerWhite30, thickness = 1.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactBottomSheet(viewModel: MyContactsTransferViewModel) {
    CustomModalBottomSheet(
        title = R.string.smart_iban_transfer_send_money,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = viewModel.uiState.bottomSheetState,
        coroutineScope = rememberCoroutineScope()
    ) {
        if (viewModel.uiState.selectedContact.isNotEmpty()) {
            Column(Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = viewModel.uiState.selectedContact.first().titular,
                    style = Typography.body1.copy(
                        fontWeight = FontWeight.Normal,
                        color = MultimoneyTheme.colors.text
                    )
                )
                Text(
                    text = viewModel.uiState.selectedContact.first().number,
                    style = Typography.body1.copy(
                        fontWeight = FontWeight.Normal,
                        color = MultimoneyTheme.colors.subTitleText
                    )
                )
                viewModel.uiState.selectedContact.forEach {
                    ContactAccountSheetDisplay(
                        account = it,
                        onClick = {} // todo add navigation to amount screen
                    )
                }
            }
        }
    }
}

@Composable
fun ContactAccountSheetDisplay(
    account: PhoneSmart,
    onClick: () -> Unit
) {
    Row(
        Modifier.padding(top = 24.dp)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(account.idCurrency.getCurrencyFromId().feeIcon),
            tint = Color.Unspecified,
            contentDescription = ""
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(
                text = "Cuenta Smart " + account.currency,
                style = Typography.body1.copy(
                    fontWeight = FontWeight.Normal,
                    color = MultimoneyTheme.colors.text
                )
            )
            Text(
                text = getMaskedAccountIban(
                    account.accountNumber,
                    stringResource(R.string.payment_account_masked_text)
                ),
                style = Typography.body1.copy(
                    fontWeight = FontWeight.Normal,
                    color = MultimoneyTheme.colors.subTitleText
                )
            )
        }
    }
}
