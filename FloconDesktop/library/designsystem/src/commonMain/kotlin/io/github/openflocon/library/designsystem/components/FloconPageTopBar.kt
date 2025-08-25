package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconPageTopBar(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.(contentPadding: PaddingValues) -> Unit = {},
) {
    val contentPadding = PaddingValues(all = 12.dp)
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(contentPadding), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = FloconTheme.typography.titleLarge,
                color = FloconTheme.colorPalette.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            trailing()
        }
        content(PaddingValues(start = 12.dp, bottom = 12.dp, end = 12.dp))
    }
}
