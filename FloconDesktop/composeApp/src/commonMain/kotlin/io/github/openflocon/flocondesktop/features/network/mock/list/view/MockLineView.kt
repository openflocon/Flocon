package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconListItem
import io.github.openflocon.library.designsystem.components.FloconListItemDefaults
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconSwitch

@Composable
fun MockLineView(
    item: MockNetworkLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    changeIsEnabled: (id: String, enabled: Boolean) -> Unit,
    changeIsShared: (id: String, shared: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconListItem(
        leadingContent = {
            FloconSwitch(
                checked = item.isEnabled,
                onCheckedChange = { changeIsEnabled(item.id, it) }
            )
        },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                MockNetworkMethodView(
                    method = item.method
                )
                Text(
                    text = item.displayName.ifBlank { "No name" },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = FloconTheme.typography.titleSmall
                )
            }
        },
        supportingContent = {
            Text(
                text = item.urlPattern,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(.5f)
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloconCheckbox(
                    checked = item.isShared,
                    onCheckedChange = {
                        changeIsShared(item.id, !item.isShared)
                    }
                )
                FloconIconButton(
                    imageVector = Icons.Filled.Delete,
                    onClick = {
                        onDeleteClicked(item.id)
                    },
                )
            }
        },
        colors = FloconListItemDefaults.colors(containerColor = FloconTheme.colorPalette.secondary),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .clickable(onClick = { onClicked(item.id) })
    )
}

@Preview
@Composable
private fun MockLineViewPreview() {
    FloconTheme {
        FloconSurface {
            MockLineView(
                item = MockNetworkLineUiModel(
                    id = "",
                    urlPattern = ".*",
                    isEnabled = true,
                    method = MockNetworkMethodUi.GET,
                    isShared = false,
                    displayName = "Mock for YouTube video",
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
                changeIsShared = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun MockLineViewPreview_url() {
    FloconTheme {
        FloconSurface {
            MockLineView(
                item = MockNetworkLineUiModel(
                    id = "",
                    urlPattern = "http://.*youtube.*v=.*",
                    isEnabled = false,
                    method = MockNetworkMethodUi.ALL,
                    isShared = false,
                    displayName = "Mock for YouTube video",
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
                changeIsShared = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun MockLineViewPreview_url_patch() {
    FloconTheme {
        FloconSurface {
            MockLineView(
                item = MockNetworkLineUiModel(
                    id = "",
                    urlPattern = "http://.*youtube.*v=.*",
                    isEnabled = true,
                    method = MockNetworkMethodUi.PATCH,
                    isShared = true,
                    displayName = "Mock for YouTube video",
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
                changeIsShared = { _, _ -> },
            )
        }
    }
}
