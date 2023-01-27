package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
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
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme.colors
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.ContactItem
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomSearchBar
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.capitalizedAllWords

@Composable
fun MyContactsTransferScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: MyContactsTransferViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.onUIEvent(MyContactsTransferViewModel.UIEvent.OnCallQueryRelatedContactsByPhoneUseCase)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    }

    Column(
        modifier = Modifier
            .background(colors.background)
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
                    color = colors.onBoardingTitleText
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.Start),
                    style = Typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.bodyTextColor
                    )
                )
                CustomButton(
                    text = stringResource(id = R.string.smart_my_contacts_transfer_accounts_add),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End),
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
                    color = colors.onBoardingTitleText
                )
            )
            Text(
                text = stringResource(id = R.string.smart_mycontacts_transfer_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colors.bodyTextColor
                )
            )
        }
        Text(
            text = stringResource(
                id = R.string.smart_mycontacts_total_transfer_title,
                viewModel.uiState.relatedContactList?.count()
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, end = 16.dp, start = 16.dp),
            style = Typography.caption.copy(
                fontWeight = FontWeight.Normal,
                color = colors.smartCardTrending
            )
        )
        CustomSearchBar(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp)
                .clip(shape = RoundedCornerShape(30))
                .background(colors.background),
            value = viewModel.uiState.queryValue,
            placeHolder = stringResource(id = R.string.smart_my_concts_placeholder),
            onValueChange = {
                viewModel.onUIEvent(
                    MyContactsTransferViewModel.UIEvent.OnQueryValueChange(
                        it
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })

        )

        viewModel.apply {
            ContactList(uiState) {
                onUIEvent(MyContactsTransferViewModel.UIEvent.OnAddToFavoriteAccountClick(it))
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun ContactList(
    uiState: MyContactsTransferViewModel.UIState = MyContactsTransferViewModel.UIState(),
    onEndIconClick: (PhoneSmart) -> Unit = {}
) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
        items(uiState.relatedContactList) { contact ->
            val randomColor = colors.coloredInitialChar
            val colorSubtitle by remember { mutableStateOf(randomColor.random()) }
            if (
                (contact?.titular?.contains(uiState.queryValue, true) == true)
            ) {
                ContactItem(
                    title = contact.titular?.capitalizedAllWords(),
                    subtitle =
                    contact.number ?: "",
                    modifier = Modifier
                        .fillMaxWidth(),
                    endIcon = R.drawable.ic_options,
                    onEndIconClick = { onEndIconClick.invoke(contact) },
                    colorSubtitle = colorSubtitle
                )
                Divider(color = colors.dividerWhite30, thickness = 1.dp)
            }
        }
    }
}
