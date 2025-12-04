package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportDetailView
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportItemView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrashReporterScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: CrashReporterViewModel = koinViewModel()
    val crashReports by viewModel.crashReports.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()

    CrashReportScreen(
        modifier = Modifier.fillMaxSize(),
        crashReports = crashReports,
        selected = selected,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CrashReportScreen(
    modifier: Modifier = Modifier,
    onAction: (CrashReporterAction) -> Unit,
    crashReports: List<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
) {
    FloconFeature(
        modifier = modifier
    ) {
        Row(Modifier.fillMaxSize()) {
            LeftPannel(
                modifier = Modifier.fillMaxHeight()
                    .width(340.dp),
                crashReports = crashReports,
                onAction = onAction,
                selected = selected,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                color = FloconTheme.colorPalette.primary,
                modifier = Modifier.fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.secondary,
                        shape = FloconTheme.shapes.medium
                    )
            ) {
                selected?.let {
                    CrashReportDetailView(
                        modifier = Modifier
                            .fillMaxSize(),
                        crash = it,
                        onCopy = {
                            onAction(CrashReporterAction.Copy(it))
                        },
                        onDelete = {
                            onAction(CrashReporterAction.Delete(it.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LeftPannel(
    onAction: (CrashReporterAction) -> Unit,
    crashReports: List<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = FloconTheme.colorPalette.primary,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            Modifier.fillMaxSize()
                .padding(all = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Crashes",
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                        .padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(2.dp))
                        .clickable {
                            onAction(CrashReporterAction.Clean)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(crashReports) { crash ->
                    CrashReportItemView(
                        crash = crash,
                        isSelected = crash.id == selected?.id,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onAction(CrashReporterAction.Select(it.id))
                        },
                        onDelete = {
                            onAction(CrashReporterAction.Delete(it.id))
                        }
                    )
                }
            }
        }
    }
}