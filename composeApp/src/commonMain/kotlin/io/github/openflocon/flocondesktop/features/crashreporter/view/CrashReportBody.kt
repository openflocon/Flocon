package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.crash_filter_all_frames
import flocondesktop.composeapp.generated.resources.crash_filter_my_code
import flocondesktop.composeapp.generated.resources.crash_framework_frames_hidden
import flocondesktop.composeapp.generated.resources.crash_framework_frames_show
import flocondesktop.composeapp.generated.resources.crash_root_cause_title
import flocondesktop.composeapp.generated.resources.crash_tab_raw
import flocondesktop.composeapp.generated.resources.crash_tab_stack_trace
import flocondesktop.composeapp.generated.resources.crash_tab_suppressed
import io.github.openflocon.flocondesktop.features.crashreporter.model.ParsedStackTrace
import io.github.openflocon.flocondesktop.features.crashreporter.model.StackLine
import io.github.openflocon.flocondesktop.features.crashreporter.model.parseStackTrace
import io.github.openflocon.flocondesktop.features.crashreporter.model.symbolAndLocation
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconSegmentedRow
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.TabType
import org.jetbrains.compose.resources.stringResource

private enum class CrashTab { StackTrace, Raw, Suppressed }

/**
 * What is under the header of a crash: where it crashed in the app, then the stack trace
 * (frames of the app highlighted, frames of the framework hidden by default), the raw text, the suppressed exceptions.
 */
@Composable
internal fun CrashReportBody(
    stackTrace: String,
    modifier: Modifier = Modifier,
) {
    val parsed = remember(stackTrace) { parseStackTrace(stackTrace) }
    var tab by remember { mutableStateOf(CrashTab.StackTrace) }
    var onlyMyCode by remember { mutableStateOf(true) }
    val hasSuppressed = parsed.suppressed.isNotEmpty()
    // nothing to filter out
    val hideFramework = onlyMyCode && parsed.firstAppFrame != null

    Column(
        modifier = modifier.padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FloconSegmentedRow(modifier = Modifier.width(if (hasSuppressed) 420.dp else 280.dp)) {
                FloconTab(
                    text = stringResource(Res.string.crash_tab_stack_trace),
                    isSelected = tab == CrashTab.StackTrace,
                    onSelected = { tab = CrashTab.StackTrace },
                    tabType = TabType.Start,
                    modifier = Modifier.weight(1f),
                )
                FloconTab(
                    text = stringResource(Res.string.crash_tab_raw),
                    isSelected = tab == CrashTab.Raw,
                    onSelected = { tab = CrashTab.Raw },
                    tabType = TabType.Middle,
                    modifier = Modifier.weight(1f),
                )
                if (hasSuppressed) {
                    FloconTab(
                        text = stringResource(Res.string.crash_tab_suppressed, parsed.suppressed.size),
                        isSelected = tab == CrashTab.Suppressed,
                        onSelected = { tab = CrashTab.Suppressed },
                        tabType = TabType.End,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (tab == CrashTab.StackTrace && parsed.firstAppFrame != null) {
                FloconSegmentedRow(modifier = Modifier.width(240.dp)) {
                    FloconTab(
                        text = stringResource(Res.string.crash_filter_my_code),
                        isSelected = onlyMyCode,
                        onSelected = { onlyMyCode = true },
                        tabType = TabType.Start,
                        modifier = Modifier.weight(1f),
                    )
                    FloconTab(
                        text = stringResource(Res.string.crash_filter_all_frames),
                        isSelected = !onlyMyCode,
                        onSelected = { onlyMyCode = false },
                        tabType = TabType.End,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        parsed.firstAppFrame?.let { RootCauseCard(it) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            when (tab) {
                CrashTab.StackTrace -> StackLines(parsed, hideFramework, onShowAll = { onlyMyCode = false })

                CrashTab.Raw -> FloconCodeBlock(
                    code = stackTrace,
                    containerColor = FloconTheme.colors.bg000,
                    borderColor = FloconTheme.colors.line,
                    modifier = Modifier.fillMaxWidth(),
                )

                CrashTab.Suppressed -> FloconCodeBlock(
                    code = parsed.suppressed.joinToString("\n") { it.text },
                    containerColor = FloconTheme.colors.bg000,
                    borderColor = FloconTheme.colors.line,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RootCauseCard(
    frame: StackLine,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (symbol, location) = remember(frame) { frame.symbolAndLocation() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.catCrashSoft, FloconTheme.shapes.medium)
            .border(1.dp, colors.catCrash.copy(alpha = 0.3f), FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Text(
            text = stringResource(Res.string.crash_root_cause_title).uppercase(),
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.catCrash,
        )
        SelectionContainer {
            Column {
                Text(
                    text = symbol,
                    style = FloconTheme.typography.monoMedium,
                    color = colors.ink,
                )
                Text(
                    text = location,
                    style = FloconTheme.typography.monoSmall,
                    color = colors.accent,
                )
            }
        }
    }
}

@Composable
private fun StackLines(
    parsed: ParsedStackTrace,
    hideFramework: Boolean,
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg000, FloconTheme.shapes.medium)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .padding(vertical = FloconTheme.spacing.small),
    ) {
        SelectionContainer {
            Column(Modifier.fillMaxWidth()) {
                parsed.lines.forEach { line ->
                    if (!(hideFramework && line.kind == StackLine.Kind.Framework)) {
                        StackLineRow(line)
                    }
                }
            }
        }
        if (hideFramework && parsed.frameworkFramesCount > 0) {
            Row(
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            ) {
                Text(
                    text = stringResource(Res.string.crash_framework_frames_hidden, parsed.frameworkFramesCount) + " ·",
                    style = FloconTheme.typography.bodySmall,
                    color = colors.inkFaint,
                )
                Text(
                    text = stringResource(Res.string.crash_framework_frames_show),
                    style = FloconTheme.typography.bodySmall,
                    color = colors.accent,
                    modifier = Modifier.clickable(onClick = onShowAll),
                )
            }
        }
    }
}

@Composable
private fun StackLineRow(
    line: StackLine,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val isApp = line.kind == StackLine.Kind.App
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isApp) colors.catCrashSoft else Color.Transparent)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = line.number.toString(),
            style = FloconTheme.typography.monoSmall,
            color = if (isApp) colors.catCrash else colors.lineStrong,
            textAlign = TextAlign.End,
            modifier = Modifier.width(24.dp),
        )
        Text(
            text = line.text,
            style = FloconTheme.typography.monoMedium,
            color = when (line.kind) {
                StackLine.Kind.Header, StackLine.Kind.App -> colors.ink
                StackLine.Kind.Framework, StackLine.Kind.Suppressed -> colors.inkFaint
            },
        )
    }
}
