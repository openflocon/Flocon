package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun DatabaseQueryView(
    query: String,
    autoUpdate: Boolean,
    updateQuery: (query: String) -> Unit,
    onAction: (action: DatabaseTabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = FloconTheme.colorPalette.primary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        DatabaseQueryToopbarView(
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
            isQueryEmpty = query.isBlank(),
        )
        FloconTextField(
            value = query,
            onValueChange = updateQuery,
            singleLine = false,
            minLines = 5,
            maxLines = 10,
            textStyle = FloconTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier.fillMaxWidth()
                .onKeyEvent { keyEvent ->
                    // detect CMD + Enter
                    if (keyEvent.type == KeyEventType.KeyDown
                        && keyEvent.key == androidx.compose.ui.input.key.Key.Enter &&
                        (keyEvent.isMetaPressed || keyEvent.isCtrlPressed)
                    ) {
                        onAction(DatabaseTabAction.ExecuteQuery(query))

                        // Return 'true' to indicate that the event was consumed
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                }.padding(horizontal = 6.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconButton(
                onClick = {
                    onAction(DatabaseTabAction.ExecuteQuery(query))
                },
                containerColor = FloconTheme.colorPalette.tertiary,
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                val contentColor = FloconTheme.colorPalette.onTertiary
                Row(
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                    Text("Run Query", color = contentColor)
                }

            }


            Row(
                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = autoUpdate,
                    onCheckedChange = {
                        onAction(DatabaseTabAction.UpdateAutoUpdate(it))
                    }, colors = CheckboxDefaults.colors(
                        uncheckedColor = FloconTheme.colorPalette.secondary,
                        checkedColor = FloconTheme.colorPalette.secondary,
                    )
                )
                Text(
                    "Auto Update",
                    color = FloconTheme.colorPalette.onPrimary,
                    style = FloconTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Preview
@Composable
private fun DatabaseQueryViewPreview() {
    FloconTheme {
        DatabaseQueryView(
            modifier = Modifier.fillMaxWidth(),
            query = "SELECT * FROM TABLE_NAME",
            updateQuery = {},
            autoUpdate = true,
            onAction = {}
        )
    }
}
