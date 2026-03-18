package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIconButton
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
    Row(
        modifier = modifier
            .clickable { onClicked(item.id) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(50.dp)
                .height(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            FloconSwitch(
                checked = item.isEnabled,
                onCheckedChange = { changeIsEnabled(item.id, it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                MockNetworkMethodView(item.method)
            }

            if (item.displayName.isEmpty()) {
                Text(
                    text = item.urlPattern,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface,
                )
            } else {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.displayName,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = item.urlPattern,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = FloconTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth().alpha(.5f),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(12.dp)
            ) {
                FloconCheckbox(
                    checked = item.isShared,
                    onCheckedChange = {
                        changeIsShared(item.id, !item.isShared)
                    }
                )
            }

            FloconIconButton(
                imageVector = Icons.Filled.Delete,
                onClick = {
                    onDeleteClicked(item.id)
                },
            )
        }
    }
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
