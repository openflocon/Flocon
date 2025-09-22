package io.github.openflocon.flocondesktop.device.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.device.DeviceAction
import io.github.openflocon.flocondesktop.device.models.PermissionUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider

@Composable
internal fun PermissionPage(
    state: PermissionUiState,
    onAction: (DeviceAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        itemsIndexed(
            items = state.list,
            key = { _, item -> item.name }
        ) { index, item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.medium)
                    .clickable(onClick = {
                        onAction(
                            DeviceAction.ChangePermission(
                                permission = item.name,
                                granted = item.granted
                            )
                        )
                    })
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = item.name,
                    style = FloconTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                FloconCheckbox(
                    checked = item.granted,
                    onCheckedChange = null,
                    uncheckedColor = FloconTheme.colorPalette.secondary
                )
            }
            if (index != state.list.lastIndex) {
                FloconHorizontalDivider(
                    color = FloconTheme.colorPalette.secondary
                )
            }
        }
    }
}
