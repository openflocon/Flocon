package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.bin
import flocondesktop.composeapp.generated.resources.send
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
    ) {
        BasicTextField(
            value = query,
            onValueChange = { updateQuery(it) },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = false,
            maxLines = 10,
            minLines = 5,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            DatabaseButton(
                icon = Res.drawable.bin,
                modifier = Modifier
                    .align(Alignment.TopStart),
                dark = true,
                onClick = {
                    clearQuery()
                },
            )
            DatabaseButton(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                icon = Res.drawable.send,
                dark = false,
                onClick = {
                    executeQuery(query)
                },
            )
        }
    }
}

@Composable
private fun DatabaseButton(
    onClick: () -> Unit,
    icon: DrawableResource,
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)
            .background(if (dark) FloconColors.pannel else Color.White)
            .clickable(onClick = onClick)
            .padding(all = 8.dp),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(if (dark) Color.White else Color.Black),
            modifier = Modifier.fillMaxSize(),
        )
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
