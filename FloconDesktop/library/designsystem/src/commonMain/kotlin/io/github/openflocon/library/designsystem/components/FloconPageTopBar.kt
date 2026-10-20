package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconPageTopBar(
    modifier: Modifier = Modifier,
    selector: (@Composable RowScope.() -> Unit)? = null,
    filterBar: (@Composable RowScope.() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.(contentPadding: PaddingValues) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .padding(all = FloconTheme.spacing.small)
    ) {
        if (selector != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                selector()
            }
        }
        if (filterBar != null || actions != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                if (filterBar != null) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                    ) {
                        filterBar()
                    }
                }
                if (actions != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    ) {
                        actions()
                    }
                }
            }
        }
        content(PaddingValues())
    }
}
