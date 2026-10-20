package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconFeature(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier,
        content = content
    )
}
