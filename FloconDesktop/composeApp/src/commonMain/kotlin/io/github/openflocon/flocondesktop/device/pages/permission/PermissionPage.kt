package io.github.openflocon.flocondesktop.device.pages.permission

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun PermissionPage(
    deviceSerial: String
) {
    val viewModel = koinViewModel<PermissionViewModel> { parametersOf(deviceSerial) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: PermissionUiState,
    onAction: (PermissionAction) -> Unit
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
            items = uiState.list,
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
                            PermissionAction.ChangePermission(
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
            if (index != uiState.list.lastIndex) {
                FloconHorizontalDivider(
                    color = FloconTheme.colorPalette.secondary
                )
            }
        }
    }
}
