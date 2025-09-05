package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
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
        modifier = modifier.background(
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
            textStyle = FloconTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                onClick = clearQuery,
                modifier = Modifier
                    .align(Alignment.TopStart),
            )
            FloconIconButton(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                onClick = { executeQuery(query) },
                modifier = Modifier
                    .align(Alignment.TopEnd),
            )
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
