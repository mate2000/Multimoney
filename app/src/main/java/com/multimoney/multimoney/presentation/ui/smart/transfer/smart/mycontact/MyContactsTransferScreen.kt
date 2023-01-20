package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency5
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomSearchBar
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.toTwoChar

@Composable
fun MyContactsTransferScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: MyContactsTransferViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

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
                viewModel.uiState.relatedContactList?.count()
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
    val background: Color
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        background = Transparent
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    } else {
        background = WhiteTransparency5
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(background)
    ) {
        ConstraintLayout(
            Modifier
                .background(background)
                .fillMaxWidth()
        ) {
            val (startIconId, titleId, subTitleId, endIconId) = createRefs()
            Surface(
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(startIconId) {
                        top.linkTo(parent.top, margin = 17.dp)
                        start.linkTo(parent.start, margin = 4.dp)
                        bottom.linkTo(parent.bottom, margin = 17.dp)
                    },
                border = BorderStroke(1.dp, Color.Gray),
                color = background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = title.toTwoChar(),
                        style = Typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = subtitleColor
                    )
                }
            }
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier.constrainAs(titleId) {
                        top.linkTo(startIconId.top, margin = 8.dp)
                        start.linkTo(startIconId.end, margin = 22.dp)
                        if (endIcon != null) {
                            end.linkTo(endIconId.start, margin = 16.dp)
                        } else {
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                        bottom.linkTo(subTitleId.top)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                    style = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = Modifier.constrainAs(subTitleId) {
                    top.linkTo(titleId.bottom, margin = 8.dp)
                    start.linkTo(titleId.start)
                    height = Dimension.fillToConstraints
                },
                text = subtitle,
                style = Typography.caption.copy(fontSize = 13.sp),
                color = subtitleColor
            )
            if (endIcon != null) {
                Image(
                    painter = painterResource(id = endIcon),
                    modifier = Modifier
                        .constrainAs(endIconId) {
                            if (shouldCenterEndIcon) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            } else {
                                // align the icon to the top
                                top.linkTo(parent.top, 17.dp)
                            }
                            end.linkTo(parent.end, margin = 18.dp)
                        }
                        .clickable {
                            onEndIconClick()
                        },
                    contentDescription = ""
                )
            }

        }
    }
}

@Composable
fun ContactList(viewModel: MyContactsTransferViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.relatedContactList) { contact ->
            if (
                (contact?.titular?.contains(viewModel.uiState.queryValue, true) == true)
            ) {
                ContactItem(
                    title = contact.titular,
                    subtitle =
                        contact.number,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, bottom = 15.dp),
                    endIcon = R.drawable.ic_options,
                    onEndIconClick = {
                        viewModel.onUIEvent(
                            MyContactsTransferViewModel.UIEvent.OnAddToFavoriteAccountClick(
                                contact
                            )
                        )
                    }

                )
                Divider(color = MultimoneyTheme.colors.dividerWhite30, thickness = 1.dp)
            }
        }
    }
}