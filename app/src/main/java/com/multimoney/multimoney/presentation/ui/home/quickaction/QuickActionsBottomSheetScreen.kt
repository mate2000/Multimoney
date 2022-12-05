package com.multimoney.multimoney.presentation.ui.home.quickaction

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.QuickAction
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryTwo500
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Secondary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency16
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
    if (shareViewModel.uiState.idBrand.isNotEmpty()) {
        CustomModalBottomSheet(
            title = getQuickActionsHeaderTitlePerCountry(idBrand = shareViewModel.uiState.idBrand.toInt()),
            closeIcon = R.drawable.ic_close_bottom_sheet,
            modalBottomSheetState = modalBottomSheetState,
            coroutineScope = coroutineScope
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 16.dp)
            ) {
                shareViewModel.uiState.quickActions?.let { quickActions ->
                    val creditActions =
                        quickActions.filter { quickAction -> quickAction.productType == QuickActionsProductType.Credit.value }
                    val smartActions =
                        quickActions.filter { quickAction -> quickAction.productType == QuickActionsProductType.Smart.value }
                    val cryptoActions =
                        quickActions.filter { quickAction -> quickAction.productType == QuickActionsProductType.Crypto.value }

                    if(creditActions.isNotEmpty()){
                        Text(
                            text = stringResource(id = R.string.quick_action_bottom_sheet_credit_section),
                            style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.creditNotApprovedText
                        )
                        QuickActionsRow(
                            viewModel = viewModel,
                            quickActions = creditActions,
                            quickActionsBackgroundColor = Primary500,
                            shareViewModel = shareViewModel
                        )
                    }
                    when (shareViewModel.uiState.idBrand) {
                        Brand.CostaRica.id.toString(), Brand.ElSalvador.id.toString() -> {
                            // smart section
                            if(smartActions.isNotEmpty()){
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = WhiteTransparency16
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(id = R.string.quick_action_bottom_sheet_smart_section),
                                    style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                                    color = MultimoneyTheme.colors.creditNotApprovedText
                                )
                                QuickActionsRow(
                                    viewModel = viewModel,
                                    quickActions = smartActions,
                                    quickActionsBackgroundColor = Secondary500,
                                    shareViewModel = shareViewModel
                                )
                            }
                            // crypto section
                            if (cryptoActions.isNotEmpty()){
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = WhiteTransparency16
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(id = R.string.quick_action_bottom_sheet_crypto_section),
                                    style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                                    color = MultimoneyTheme.colors.creditNotApprovedText
                                )
                                QuickActionsRow(
                                    viewModel = viewModel,
                                    quickActions = cryptoActions,
                                    quickActionsBackgroundColor = ComplementaryTwo500,
                                    shareViewModel = shareViewModel
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun getQuickActionsHeaderTitlePerCountry(idBrand: Int): Int {
    return when (idBrand) {
        Brand.Guatemala.id -> R.string.quick_action_bottom_sheet_title_gt
        else -> R.string.quick_action_bottom_sheet_title
    }
}

@Composable
fun QuickActionsRow(
    viewModel: QuickActionsBottomSheetViewModel,
    quickActions: List<QuickAction>?,
    quickActionsBackgroundColor: Color,
    shareViewModel: HomeViewModel
) {
    val localDensity = LocalDensity.current
    var widthIs by remember { mutableStateOf(88.dp) }
    var quickActionItemWidth: Dp

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .onGloballyPositioned { coordinates ->
                widthIs = with(localDensity) { coordinates.size.width.toDp() }
            }
    ) {
        if (quickActions != null) {
            quickActionItemWidth = widthIs.times(0.2857f)
            items(quickActions.count()) { index ->
                QuickActionItem(
                    viewModel.getSmartQuickAction(
                        label = quickActions[index].name,
                        iconId = quickActions[index].iconId
                    ),
                    backgroundColor = quickActionsBackgroundColor,
                    itemWidth = quickActionItemWidth
                ) {
                    Log.e("Clicking", "Item")
                    // send to savings smart screen

                    shareViewModel.onUIEvent(
                        HomeViewModel.UIEvent.OnOpenQuickActionFlow(
                            quickActions[index].flow
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    quickActionDummy: QuickActionDummy,
    backgroundColor: Color,
    itemWidth: Dp,
    action: () -> Unit = {},
    ) {
    Column(
        modifier = Modifier
            .width(itemWidth)
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(shape = CircleShape, color = backgroundColor)
                .clickable { action() }
        ) {
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
            textAlign = TextAlign.Center
        )
    }
}
