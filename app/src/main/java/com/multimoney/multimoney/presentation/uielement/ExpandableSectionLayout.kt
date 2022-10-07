package com.multimoney.multimoney.presentation.uielement

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * This composable handles the creation of a expandable layout with a section title,
 * in which you can pass the main content as a param
 *
 * @param modifier: modifier used only in the root column
 * @param enabled: enables the expand feature
 * @param titleIcon: Icon displayed with title, it can be null to show only section title
 * @param title: string for the section title and content description
 * @param content: the main content of the layout, it can be hide
 * **/

@Composable
fun ExpandableSectionLayout(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowIcon = if (expanded && enabled) Icons.Default.ExpandLess else Icons.Default.ExpandMore

    Column(modifier = modifier) {
        Divider(
            color = MaterialTheme.colors.primary.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    style = Typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                    color = MultimoneyTheme.colors.text,
                )
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = null,
                    tint = Primary400
                )
            }
        }
        AnimatedVisibility(visible = (expanded && enabled)) {
            content()
        }
    }
}
