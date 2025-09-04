@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        FloconSurface(
            shape = FloconTheme.shapes.large,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colorPalette.onPrimary,
        )
        trailingContent()
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
