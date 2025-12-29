package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DatabaseQueryLogItemView(
    log: DatabaseQueryUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.then(
            if (log.isFromOldSession) {
                Modifier.alpha(0.8f)
            } else Modifier
        ), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.width(60.dp),
            text = log.dateFormatted,
            style = FloconTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = FloconTheme.colorPalette.onSurface
                .copy(alpha = 0.6f)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = log.sqlQuery,
                style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                color = findColor(log.type)
            )
            log.bindArgs?.let {
                Text(
                    text = "Args: ${log.bindArgs}",
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun findColor(type: DatabaseQueryUiModel.Type?): Color {
    return when (type) {
        DatabaseQueryUiModel.Type.Select -> Color(0xFF28A745)
        DatabaseQueryUiModel.Type.Insert -> Color(0xFF007BFF)
        DatabaseQueryUiModel.Type.Update -> Color(0xFFFFC107)
        DatabaseQueryUiModel.Type.Delete -> Color(0xFFDC3545)
        DatabaseQueryUiModel.Type.Transaction,
        null -> FloconTheme.colorPalette.onSurface
    }
}
