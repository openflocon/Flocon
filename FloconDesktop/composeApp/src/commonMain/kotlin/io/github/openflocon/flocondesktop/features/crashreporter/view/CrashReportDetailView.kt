package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock

@Composable
internal fun CrashReportDetailView(
    modifier: Modifier,
    onCopy: (CrashReporterSelectedUiModel) -> Unit,
    onDelete: (CrashReporterSelectedUiModel) -> Unit,
    crash: CrashReporterSelectedUiModel
) {
    SelectionContainer {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = crash.exceptionType,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = crash.dateFormatted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = crash.exceptionMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Actions(
                    modifier = Modifier.height(40.dp),
                    onCopy = {
                        onCopy(crash)
                    },
                    onDelete = {
                        onDelete(crash)
                    }
                )
            }
            FloconCodeBlock(
                code = crash.stackTrace,
                containerColor = FloconTheme.colorPalette.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable {
                    onDelete()
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable {
                    onCopy()
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.CopyAll,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
    }
}
