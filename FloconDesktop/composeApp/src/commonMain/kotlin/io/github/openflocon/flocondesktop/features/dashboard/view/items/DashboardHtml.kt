package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import javax.swing.JEditorPane

@Composable
internal fun DashboardHtml(
    rowItem: DashboardContainerViewState.RowItem.Html,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (rowItem.label.isNotEmpty()) {
            Text(
                text = rowItem.label,
                modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall, bottom = FloconTheme.spacing.extraSmall),
                color = FloconTheme.colors.ink,
                style = FloconTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Thin,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FloconTheme.colors.bg200,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(FloconTheme.spacing.small),
        ) {
            SwingPanel(
                modifier = Modifier.fillMaxWidth().height(600.dp), // Height needs to be fixed or dynamic
                factory = {
                    JEditorPane().apply {
                        contentType = "text/html"
                        isEditable = false
                    }
                },
                update = {
                    it.text = rowItem.value
                }
            )
        }
    }
}
