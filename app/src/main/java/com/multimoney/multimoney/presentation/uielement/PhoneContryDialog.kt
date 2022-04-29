package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.getCountryName
import com.togitech.ccp.data.utils.getFlags
import com.togitech.ccp.data.utils.getLibCountries
import com.togitech.ccp.utils.searchCountry

/**
 * PhoneCountryDialog: This PhoneCountryDialog is used to select country
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param padding: Column padding.
 * @param defaultSelectedCountry: Default country to start phone text field.
 * @param showCountryCode: Show country code.
 * @param showCountryFlag: Show country flag.
 * @param pickedCountry: Function to handle picked country selected.
 * @param dialogAppBarColor: Change select country dialog AppBarColor.
 * @param dialogAppBarTextColor: Change select country dialog AppBarTextColor.
 * @param dialogFocusedBorderColorSearch: Change select country dialog FocusedBorderColorSearch.
 * @param dialogUnFocusedBorderColorSearch: Change select country dialog UnFocusedBorderColorSearch.
 * @param dialogCursorColorSearch: Change select country dialog CursorColorSearch.
 * **/

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun PhoneCountryDialog(
    modifier: Modifier = Modifier,
    padding: Dp = 15.dp,
    defaultSelectedCountry: CountryData = getLibCountries().first(),
    showCountryCode: Boolean = true,
    showCountryFlag: Boolean = true,
    pickedCountry: (CountryData) -> Unit = {},
    countryCodeTextColor: Color = MultimoneyTheme.colors.text,
    dialogAppBarColor: Color = MultimoneyTheme.colors.primary,
    dialogAppBarTextColor: Color = DefaultWhite,
    dialogFocusedBorderColorSearch: Color = MultimoneyTheme.colors.primary,
    dialogUnFocusedBorderColorSearch: Color = MultimoneyTheme.colors.secondary,
    dialogCursorColorSearch: Color = MultimoneyTheme.colors.primary,
) {
    val countryList: List<CountryData> = getLibCountries()
    var isPickCountry by remember { mutableStateOf(defaultSelectedCountry) }
    var isOpenDialog by remember { mutableStateOf(false) }
    var searchValue by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .padding(padding)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { isOpenDialog = true },
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = modifier.width(34.dp),
                painter = painterResource(
                    id = getFlags(
                        isPickCountry.countryCode
                    )
                ), contentDescription = null
            )
            if (showCountryCode) {
                Text(
                    text = isPickCountry.countryPhoneCode,
                    modifier = Modifier.padding(start = 4.dp),
                    style = Typography.body2.copy(color = countryCodeTextColor)
                )
                if (showCountryFlag) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }
    }

    //Select Country Dialog
    if (isOpenDialog) {
        Dialog(
            onDismissRequest = { isOpenDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.phone_country_dialog_title),
                                textAlign = TextAlign.Center,
                                modifier = modifier.fillMaxWidth()
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                isOpenDialog = false
                                isSearch = false
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        backgroundColor = dialogAppBarColor,
                        contentColor = dialogAppBarTextColor,
                        actions = {
                            IconButton(onClick = {
                                isSearch = !isSearch
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Search"
                                )
                            }
                        }
                    )
                }
            ) { paddingValue ->
                Surface(modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValue)) {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        elevation = 4.dp,
                    ) {
                        Column {
                            if (isSearch) {
                                searchValue = dialogSearchView(
                                    focusedBorderColor = dialogFocusedBorderColorSearch,
                                    unfocusedBorderColor = dialogUnFocusedBorderColorSearch,
                                    cursorColor = dialogCursorColorSearch,
                                )
                            }
                            LazyColumn {
                                items(
                                    (if (searchValue.isEmpty()) {
                                        countryList
                                    } else {
                                        countryList.searchCountry(
                                            searchValue,
                                            context = context
                                        )
                                    })
                                ) { countryItem ->
                                    Row(
                                        Modifier
                                            .padding(
                                                horizontal = 18.dp,
                                                vertical = 18.dp
                                            )
                                            .clickable {
                                                pickedCountry(countryItem)
                                                isPickCountry = countryItem
                                                isOpenDialog = false
                                            }) {
                                        Image(
                                            modifier = modifier.width(30.dp),
                                            painter = painterResource(
                                                id = getFlags(
                                                    countryItem.countryCode
                                                )
                                            ), contentDescription = null
                                        )
                                        Text(
                                            stringResource(id = getCountryName(countryItem.countryCode.lowercase())),
                                            Modifier.padding(horizontal = 18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun dialogSearchView(
    focusedBorderColor: Color = MaterialTheme.colors.primary,
    unfocusedBorderColor: Color = MaterialTheme.colors.onSecondary,
    cursorColor: Color = MaterialTheme.colors.primary,
): String {
    var searchVal by remember { mutableStateOf("") }
    Row {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            value = searchVal, onValueChange = { searchVal = it },
            fontSize = 14.sp,
            hint = stringResource(id = R.string.phone_country_dialog_search),
            textAlign = TextAlign.Start,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            cursorColor = cursorColor

        )
    }
    return searchVal
}


@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Center,
    focusedBorderColor: Color = MaterialTheme.colors.primary,
    unfocusedBorderColor: Color = MaterialTheme.colors.onSecondary,
    cursorColor: Color = MaterialTheme.colors.primary,
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f)
            )
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                textAlign = textAlign,
                fontSize = fontSize
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Black.copy(0.2f)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = focusedBorderColor,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = unfocusedBorderColor,
                cursorColor = cursorColor,
            )
        )
        if (value.isEmpty()) {
            Text(
                text = hint,
                style = MaterialTheme.typography.body1,
                color = Color.Gray,
                modifier = Modifier.then(
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 52.dp)
                )
            )
        }
    }
}