package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.analytics_excluded_count
import flocondesktop.composeapp.generated.resources.analytics_restore_all
import flocondesktop.composeapp.generated.resources.analytics_restore_event
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconIconButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnalyticsExcludedEvents(
    eventNames: ImmutableList<String>,
    onRestore: (String) -> Unit,
    onRestoreAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (eventNames.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { expanded = true },
        modifier = modifier,
        anchorContent = {
            FloconButton(onClick = { expanded = true }) {
                Text(stringResource(Res.string.analytics_excluded_count, eventNames.size))
            }
        },
    ) {
        eventNames.forEach { eventName ->
            key(eventName) {
                FloconDropdownMenuItem(
                    text = eventName,
                    onClick = { onRestore(eventName) },
                    leadingIcon = Icons.Outlined.Restore,
                    secondaryAction = {
                        FloconIconButton(
                            imageVector = Icons.Outlined.Close,
                            tooltip = stringResource(Res.string.analytics_restore_event),
                            onClick = { onRestore(eventName) },
                        )
                    },
                )
            }
        }
        FloconDropdownSeparator()
        FloconDropdownMenuItem(
            text = stringResource(Res.string.analytics_restore_all),
            onClick = {
                expanded = false
                onRestoreAll()
            },
        )
    }
}

@Preview
@Composable
private fun AnalyticsExcludedEventsPreview() {
    FloconTheme {
        AnalyticsExcludedEvents(
            eventNames = persistentListOf("heartbeat", "scroll", "screen_view"),
            onRestore = {},
            onRestoreAll = {},
        )
    }
}
