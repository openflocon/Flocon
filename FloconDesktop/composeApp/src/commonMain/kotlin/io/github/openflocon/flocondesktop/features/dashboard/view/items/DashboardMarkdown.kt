package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mikepenz.markdown.m3.Markdown
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DashboardMarkdown(
    rowItem: DashboardContainerViewState.RowItem.Markdown,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (rowItem.label.isNotEmpty()) {
            Text(
                text = rowItem.label,
                modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall, bottom = FloconTheme.spacing.extraSmall),
                color = FloconTheme.colors.inkMuted,
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.extraSmall),
        ) {
            Markdown(
                content = rowItem.value,
            )
        }
    }
}
