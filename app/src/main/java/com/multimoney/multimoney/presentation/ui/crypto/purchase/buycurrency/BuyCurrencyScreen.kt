package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.PurchaseConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton

@Composable
fun BuyCurrencyScreen(
    sharedViewModel: PurchaseCryptoSharedViewModel = hiltViewModel(),
    viewModel: BuyCurrencyScreenViewModel = hiltViewModel()
) {

    BuyCurrencyScreenContent()
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BuyCurrencyScreenContent() {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

    ModalBottomSheetLayout(
        sheetContent = {
            PurchaseConfirmationBottomSheet()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            ConstraintLayout {
                val (title, conversionCurrencyToDollars, amountInput, availableBalance, counter, button) = createRefs()
                TitleSection(modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
                ConversionCurrencyToDollarsSection(modifier = Modifier.constrainAs(conversionCurrencyToDollars) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
                AmountInputSection(modifier = Modifier.constrainAs(amountInput) {
                    top.linkTo(conversionCurrencyToDollars.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
                AvailableBalanceSection(modifier = Modifier.constrainAs(availableBalance) {
                    bottom.linkTo(counter.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
                CounterSection(modifier = Modifier.constrainAs(counter) {
                    top.linkTo(availableBalance.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                })
                CustomButton(
                    modifier = Modifier
                        .constrainAs(button) {
                            top.linkTo(availableBalance.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier
) {
    Text(text = "00:15")
}

@Composable
fun AvailableBalanceSection(
    modifier: Modifier = Modifier
) {
    Text(text = "")
}

@Composable
fun AmountInputSection(
    modifier: Modifier = Modifier
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        //custom edittext
        Text(text = "0 BTC")
    }
}

@Composable
fun ConversionCurrencyToDollarsSection(
    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        //Icon(painter = , contentDescription =)
        Text(text = "1 BTC = $100.000")
    }
}

@Composable
fun TitleSection(
    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Cuanto queres comprar?")
    }
}
