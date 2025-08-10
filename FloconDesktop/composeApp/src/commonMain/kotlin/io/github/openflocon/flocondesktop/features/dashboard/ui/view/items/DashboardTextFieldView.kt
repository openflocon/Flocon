package io.github.openflocon.flocondesktop.features.dashboard.ui.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardItemViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardTextFieldView(
    rowItem: DashboardItemViewState.RowItem.TextField,
    submitTextField: (id: String, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember(rowItem.value) {
        mutableStateOf(rowItem.value)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            rowItem.label,
            modifier = Modifier.padding(start = 4.dp),
            color = FloconTheme.colorPalette.onSurface,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Thin,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                rowItem.placeHolder?.takeIf { value.isEmpty() }?.let {
                    Text(
                        text = it,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                    )
                }
                BasicTextField(
                    textStyle = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                    onValueChange = {
                        value = it
                    },
                )
            }
            DashboardSendButton(
                icon = Icons.AutoMirrored.Outlined.Send,
                onClick = {
                    submitTextField(rowItem.id, value)
                },
            )
        }
    }
}

@Composable
private fun DashboardSendButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(all = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
internal fun DashboardTextFieldViewPreview_placeholder() {
    val rowItem = DashboardItemViewState.RowItem.TextField(
        id = "1",
        label = "label",
        placeHolder = "placeholder",
        value = "",
    )
    FloconTheme {
        DashboardTextFieldView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.panel,
            ),
            rowItem = rowItem,
            submitTextField = { _, _ -> },
        )
    }
}

@Preview
@Composable
internal fun DashboardTextFieldViewPreview_withValue() {
    val rowItem = DashboardItemViewState.RowItem.TextField(
        id = "1",
        label = "label",
        placeHolder = "placeholder",
        value = "value",
    )
    FloconTheme {
        DashboardTextFieldView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.panel,
            ),
            rowItem = rowItem,
            submitTextField = { _, _ -> },
        )
    }
}
