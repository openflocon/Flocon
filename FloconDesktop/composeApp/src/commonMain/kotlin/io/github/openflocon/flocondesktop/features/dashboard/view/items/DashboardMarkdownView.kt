package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DashboardMarkdownView(
    modifier: Modifier = Modifier,
    rowItem: DashboardContainerViewState.RowItem.Markdown,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (rowItem.label.isNotEmpty()) {
            Text(
                text = rowItem.label,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                color = FloconTheme.colorPalette.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Thin,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FloconTheme.colorPalette.secondary,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(8.dp),
        ) {
            Markdown(
                content = rowItem.value,
            )
        }
    }
}
