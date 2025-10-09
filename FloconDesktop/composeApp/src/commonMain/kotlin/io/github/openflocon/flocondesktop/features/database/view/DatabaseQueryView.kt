package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DatabaseQueryView(
    query: String,
    updateQuery: (query: String) -> Unit,
    executeQuery: (query: String) -> Unit,
    clearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
    ) {
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
                        executeQuery(query)

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
                onClick = { executeQuery(query) },
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                Text("Run", modifier = Modifier.padding(horizontal = 10.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            FloconButton(
                onClick = clearQuery,
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                Text("Reset", modifier = Modifier.padding(horizontal = 10.dp))
            }
        }
    }
}

@Preview
@Composable
private fun DatabaseQueryViewPreview() {
    FloconTheme {
        DatabaseQueryView(
            executeQuery = {},
            clearQuery = {},
            modifier = Modifier.fillMaxWidth(),
            query = "SELECT * FROM TABLE_NAME",
            updateQuery = {},
        )
    }
}
