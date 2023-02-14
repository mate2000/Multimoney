package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
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
 * **/

@Composable
@Preview
fun PhoneCountryDialog(
    modifier: Modifier = Modifier,
    padding: Dp = 15.dp,
    defaultSelectedCountry: CountryData = getLibCountries.first(),
    showCountryCode: Boolean = true,
    showCountryFlag: Boolean = true,
    pickedCountry: (CountryData) -> Unit = {},
    countryList: MutableList<CountryData>? = mutableListOf()
) {
    var innerCountryList = mutableListOf<CountryData>()
    var isPickCountry by remember { mutableStateOf(defaultSelectedCountry) }
    var isOpenDialog by remember { mutableStateOf(false) }
    var searchValue by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    // Set colors depending on system theme
    val backgroundColor: Color
    val textColor: Color
    val iconTint: Color
    val dropdownArrowColor: Color
    val searchBorderColor: Color

    if (isSystemInDarkTheme()) {
        backgroundColor = DefaultBlack
        textColor = GrayScale300
        iconTint = Primary500
        dropdownArrowColor = WhiteTransparency90
        searchBorderColor = Color.Transparent
    } else {
        backgroundColor = DefaultBlack
        textColor = GrayScale300
        iconTint = Primary500
        dropdownArrowColor = WhiteTransparency90
        searchBorderColor = Color.Transparent
    }

    Row(
        modifier = Modifier
            .padding(padding)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { isOpenDialog = true }
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = modifier.width(34.dp),
            painter = painterResource(
                id = getFlags(
                    isPickCountry.countryCode
                )
            ),

                contentDescription = null
            )
            if (showCountryCode) {
                Text(
                    text = isPickCountry.countryPhoneCode,
                    modifier = Modifier.padding(start = 4.dp),
                    style = Typography.body2.copy(color = textColor)
            )
            if (showCountryFlag) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = dropdownArrowColor
                )
            }
        }
    }

    // Select Country Dialog
    if (isOpenDialog) {
        Dialog(
            onDismissRequest = { isOpenDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                isOpenDialog = false
                                isSearch = false
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_nav_icon_left),
                                    contentDescription = "Back"
                                )
                            }
                        },
                        backgroundColor = backgroundColor,
                        contentColor = iconTint,
                        actions = {
                            IconButton(onClick = {
                                isSearch = !isSearch
                            }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = "Search"
                                )
                            }
                        }
                    )
                }
            ) { paddingValue ->
                Column(modifier = Modifier.padding(paddingValue).fillMaxSize().background(backgroundColor)) {
                    if (isSearch) {
                        searchValue = dialogSearchView(
                            focusedBorderColor = searchBorderColor,
                            unfocusedBorderColor = searchBorderColor,
                            cursorColor = textColor,
                            iconTint = iconTint
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.background(backgroundColor)
                    ) {
                        innerCountryList = if (!countryList.isNullOrEmpty()) {
                            countryList
                        } else {
                            getLibCountries as MutableList<CountryData>
                        }
                        items(
                                if (searchValue.isEmpty()) {
                                    innerCountryList
                                } else {
                                    innerCountryList.searchCountry(
                                        searchValue,
                                        context = context
                                    )
                                }
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
                                    }
                            ) {
                                Image(
                                    modifier = modifier.width(30.dp),
                                    painter = painterResource(
                                        id = getFlags(
                                            countryItem.countryCode
                                        )
                                    ),
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(id = getCountryName(countryItem.countryCode.lowercase())),
                                    style = Typography.body2,
                                    color = textColor,
                                    modifier = Modifier.padding(horizontal = 18.dp)
                                )
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
    iconTint: Color = MaterialTheme.colors.primary
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
            cursorColor = cursorColor,
            iconTint = iconTint
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
    iconTint: Color = MaterialTheme.colors.primary
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
                    tint = iconTint
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = DefaultBlack,
                focusedIndicatorColor = focusedBorderColor,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = unfocusedBorderColor,
                cursorColor = cursorColor,
                textColor = cursorColor
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
