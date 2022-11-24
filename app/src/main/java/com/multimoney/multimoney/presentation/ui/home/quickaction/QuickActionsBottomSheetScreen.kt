package com.multimoney.multimoney.presentation.ui.home.quickaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.domain.model.security.QuickAction
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.*
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.util.catalog.QuickActionsProductType
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuickActionBottomSheetScreen(
    shareViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    viewModel: QuickActionsBottomSheetViewModel = hiltViewModel()
) {

    CustomModalBottomSheet(
        title = R.string.quick_action_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 16.dp)

        ) {
            shareViewModel.uiState.quickActions.let { quickActions ->
                val creditActions =
                    quickActions?.filter { quickAction -> quickAction.productType == QuickActionsProductType.Credit.value }
                val smartActions =
                    quickActions?.filter { quickAction -> quickAction.productType == QuickActionsProductType.Smart.value }
                val cryptoActions =
                    quickActions?.filter { quickAction -> quickAction.productType == QuickActionsProductType.Crypto.value }

                creditActions.let {
                    Text(
                        text = stringResource(id = R.string.quick_action_bottom_sheet_credit_section),
                        style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.creditNotApprovedText
                    )
                    QuickActionsRow(
                        viewModel = viewModel,
                        quickActions = it,
                        quickActionsBackgroundColor = Primary500
                    )
                }
                when (viewModel.quickActionUiState.idBrand) {
                    Brand.CostaRica.id.toString(), Brand.ElSalvador.id.toString() -> {
                        /*
                        SMART SECTION
                         */
                        smartActions.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(modifier = Modifier.fillMaxWidth(), color = WhiteTransparency16)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(id = R.string.quick_action_bottom_sheet_smart_section),
                                style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.creditNotApprovedText
                            )
                            QuickActionsRow(
                                viewModel = viewModel,
                                quickActions = it,
                                quickActionsBackgroundColor = Secondary500
                            )
                        }

                        /*
                        CRYPTO SECTION
                         */

                        cryptoActions.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(modifier = Modifier.fillMaxWidth(), color = WhiteTransparency16)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = R.string.quick_action_bottom_sheet_crypto_section),
                                style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                                color = MultimoneyTheme.colors.creditNotApprovedText
                            )
                            QuickActionsRow(
                                viewModel = viewModel,
                                quickActions = it,
                                quickActionsBackgroundColor = ComplementaryTwo500
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    viewModel: QuickActionsBottomSheetViewModel,
    quickActions: List<QuickAction>?,
    quickActionsBackgroundColor: Color
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        if (quickActions != null) {
            items(quickActions.count()) { index ->
                QuickActionItem(
                    viewModel.getSmartQuickAction(
                        label = quickActions[index].name,
                        iconId = quickActions[index].iconId,
                    ), backgroundColor = quickActionsBackgroundColor
                ) {
                    // send to savings smart screen
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    quickActionDummy: QuickActionDummy, backgroundColor: Color, action: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .width(88.dp)
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(shape = CircleShape, color = backgroundColor)
                .clickable { action() }) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(16.dp)
                    .background(shape = CircleShape, color = Color.Transparent),
                painter = painterResource(id = quickActionDummy.icon ?: 0),
                contentDescription = quickActionDummy.label
            )
        }
        Text(
            text = quickActionDummy.label,
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.quickActionLabelColor,
            textAlign = TextAlign.Center,
        )
    }
}