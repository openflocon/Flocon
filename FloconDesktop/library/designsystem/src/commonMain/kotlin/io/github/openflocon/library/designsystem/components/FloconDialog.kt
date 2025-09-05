@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
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
                .border(
                    width = 1.dp,
                    brush = Brush.sweepGradient(
                        0f to FloconTheme.colorPalette.accent,
                        .1f to FloconTheme.colorPalette.onAccent.copy(alpha = .5f),
                        .3f to FloconTheme.colorPalette.accent,
                        1f to FloconTheme.colorPalette.accent
                    ),
                    shape = FloconTheme.shapes.large
                )
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
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloconIcon(
                imageVector = Icons.AutoMirrored.Outlined.List
            )
            Column(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = FloconTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = FloconTheme.colorPalette.onPrimary,
                )
                Text(
                    text = "Une description",
                    style = FloconTheme.typography.labelSmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = .5f),
                )
            }
            trailingContent()
        }
        Spacer(Modifier.height(8.dp))
        FloconHorizontalDivider(color = FloconTheme.colorPalette.secondary)
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
