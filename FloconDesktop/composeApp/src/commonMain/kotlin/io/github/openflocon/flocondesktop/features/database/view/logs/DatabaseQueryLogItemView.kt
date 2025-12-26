package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DatabaseQueryLogItemView(
    log: DatabaseQueryUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
             Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = log.sqlQuery,
            style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = FloconTheme.colorPalette.onSurface
        )
        log.bindArgs?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Args: ${log.bindArgs}",
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}
