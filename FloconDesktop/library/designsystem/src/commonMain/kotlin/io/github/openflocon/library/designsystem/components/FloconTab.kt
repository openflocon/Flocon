package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * The container of the [FloconTab]s: a segmented control. The selected tab is raised above the others.
 */
@Composable
fun FloconSegmentedRow(
    modifier: Modifier = Modifier,
    content: @Composable SingleChoiceSegmentedButtonRowScope.() -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .background(FloconTheme.colors.bg200, FloconTheme.shapes.medium)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.extraSmall),
        space = FloconTheme.spacing.tiny,
        content = content,
    )
}

// [tabType] is kept for the callers, the tabs are the same whatever their position
@Composable
fun FloconTab(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    @Suppress("UNUSED_PARAMETER") tabType: TabType,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = RoundedCornerShape(6.dp)
    Text(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) colors.bg300 else Color.Transparent)
            .then(if (isSelected) Modifier.border(1.dp, colors.lineStrong, shape) else Modifier)
            .clickable { onSelected() }
            .padding(vertical = FloconTheme.spacing.small),
        text = text,
        textAlign = TextAlign.Center,
        style = FloconTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = if (isSelected) colors.ink else colors.inkMuted,
        maxLines = 1,
    )
}

enum class TabType {
    Start,
    Middle,
    End
}
