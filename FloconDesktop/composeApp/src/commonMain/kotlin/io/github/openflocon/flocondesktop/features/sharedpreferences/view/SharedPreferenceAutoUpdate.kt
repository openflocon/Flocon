package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_update_live
import flocondesktop.composeapp.generated.resources.refresh
import io.github.openflocon.flocondesktop.features.sharedpreferences.delayLabel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.AutoUpdateDelayUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.autoUpdateDelayPresets
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Immutable
sealed interface PreferenceAutoUpdate {
    @Immutable
    data class Enabled(val delayMs: Int) : PreferenceAutoUpdate {
        // ex: "2s", computed once with the state
        val delayLabel: String = delayLabel(delayMs)
    }

    @Immutable
    object Disabled : PreferenceAutoUpdate
}

private val DotSize = 7.dp
private val HaloSize = 13.dp

/** A segmented group: [Live toggle | interval (when live) | refresh]. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceAutoUpdate(
    state: PreferenceAutoUpdate,
    onChange: (Boolean) -> Unit,
    onAutoUpdateDelayChanged: (delay: Int) -> Unit,
    refreshClicked: () -> Unit,
    modifier: Modifier = Modifier,
    delays: ImmutableList<AutoUpdateDelayUiModel> = remember { autoUpdateDelayPresets() },
) {
    val colors = FloconTheme.colors
    val shape = RoundedCornerShape(8.dp)
    val isLive = state is PreferenceAutoUpdate.Enabled

    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(shape)
            .background(colors.bg300)
            .border(1.dp, colors.lineStrong, shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Live
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .background(if (isLive) colors.accentSoft else colors.bg300)
                .clickable { onChange(!isLive) }
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            Box(
                modifier = Modifier
                    .size(HaloSize)
                    .background(if (isLive) colors.accent.copy(alpha = 0.25f) else colors.bg300, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(DotSize)
                        .background(if (isLive) colors.accent else colors.inkFaint, CircleShape),
                )
            }
            Text(
                text = stringResource(Res.string.auto_update_live),
                style = FloconTheme.typography.labelMedium,
                fontWeight = if (isLive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isLive) colors.accent else colors.inkMuted,
            )
        }

        // interval
        if (state is PreferenceAutoUpdate.Enabled) {
            SegmentSeparator()
            var expanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxHeight()) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable { expanded = true }
                        .pointerHoverIcon(PointerIcon.Hand)
                        .padding(horizontal = FloconTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.delayLabel,
                        style = FloconTheme.typography.monoMedium,
                        color = colors.ink,
                    )
                    FloconIcon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        tint = colors.inkMuted,
                        modifier = Modifier.size(14.dp),
                    )
                }
                FloconDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    delays.forEach { delay ->
                        FloconDropdownMenuItem(
                            text = delay.label,
                            onClick = {
                                onAutoUpdateDelayChanged(delay.delayMs)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }

        // refresh
        SegmentSeparator()
        WithTooltip(stringResource(Res.string.refresh)) {
            Box(
                modifier = Modifier
                    .size(FloconTheme.sizes.pageBarControlHeight)
                    .clickable(onClick = refreshClicked)
                    .pointerHoverIcon(PointerIcon.Hand),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Refresh,
                    tint = colors.ink,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SegmentSeparator() {
    Box(
        Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(FloconTheme.colors.lineStrong),
    )
}

@Preview
@Composable
private fun PreferenceAutoUpdateOffPreview() {
    FloconTheme {
        PreferenceAutoUpdate(
            state = PreferenceAutoUpdate.Disabled,
            onChange = {},
            onAutoUpdateDelayChanged = {},
            refreshClicked = {},
        )
    }
}

@Preview
@Composable
private fun PreferenceAutoUpdateOnPreview() {
    FloconTheme {
        PreferenceAutoUpdate(
            state = PreferenceAutoUpdate.Enabled(delayMs = 2_000),
            onChange = {},
            onAutoUpdateDelayChanged = {},
            refreshClicked = {},
        )
    }
}
