@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler

@Composable
fun FloconDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        EscapeHandler {
            onDismissRequest()
            true
        }
        FloconSurface(
            shape = RoundedCornerShape(10.dp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            content()
        }
    }
}

@Composable
fun FloconDialogHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier
            .background(FloconTheme.colorPalette.primary)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .padding(start = 4.dp)
                .weight(1f),
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            trailingContent()
        }
    }
}

@Composable
fun FloconDialogButtons(
    onCancel: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        FloconOutlinedButton(
            onClick = onCancel
        ) {
            Text(
                text = "Cancel"
            )
        }
        FloconButton(
            onClick = onValidate
        ) {
            Text(
                text = "Save"
            )
        }
    }
}
