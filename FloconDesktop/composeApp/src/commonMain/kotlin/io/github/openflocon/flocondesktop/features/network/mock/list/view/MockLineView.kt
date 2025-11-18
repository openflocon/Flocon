package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconSwitch
import androidx.compose.ui.tooling.preview.Preview

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
        modifier = modifier.padding(vertical = 2.dp),
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
            modifier = Modifier.fillMaxWidth()
                .clickable {
                    onClicked(item.id)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                MockNetworkMethodView(item.method)
            }

            Text(
                text = item.urlPattern,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = FloconTheme.colorPalette.onSurface,
                modifier = Modifier.weight(2f)
                    .background(
                        color = FloconTheme.colorPalette.primary.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            )

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
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
                changeIsShared = { _, _ -> },
            )
        }
    }
}
