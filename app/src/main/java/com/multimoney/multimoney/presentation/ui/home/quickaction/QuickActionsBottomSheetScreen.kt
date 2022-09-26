package com.multimoney.multimoney.presentation.ui.home.quickaction

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
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
            Text(
                text = stringResource(id = R.string.quick_action_bottom_sheet_credit_section),
                style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.creditNotApprovedText
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(items = viewModel.getQuickActions(), itemContent = { item ->
                    QuickActionItem(item, viewModel)
                })
            }
        }
    }
}

@Composable
fun QuickActionItem(
    quickActionDummy: QuickActionDummy,
    quickActionsBottomSheetViewModel: QuickActionsBottomSheetViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(88.dp)
            .padding(end = 16.dp)
            .clickable {
                Toast
                    .makeText(context, "QuickAction clicked", Toast.LENGTH_SHORT)
                    .show()
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = quickActionDummy.icon), contentDescription = "")
        Text(
            text = quickActionDummy.label,
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.quickActionLabelColor,
            textAlign = TextAlign.Center
        )
    }
}