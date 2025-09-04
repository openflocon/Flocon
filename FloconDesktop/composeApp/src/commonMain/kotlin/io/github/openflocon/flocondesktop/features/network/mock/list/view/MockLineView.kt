package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconSwitch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MockLineView(
    item: MockNetworkLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    changeIsEnabled: (id: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(FloconTheme.shapes.small)
            .background(FloconTheme.colorPalette.primary)
            .clickable(onClick = { onClicked(item.id) })
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(4.dp))
        FloconSwitch(
            checked = item.isEnabled,
            onCheckedChange = { changeIsEnabled(item.id, it) }
        )
        MockNetworkMethodView(
            method = item.method
        )
        Text(
            text = item.urlPattern,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = FloconTheme.colorPalette.onSecondary,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = FloconTheme.colorPalette.secondary,
                    shape = FloconTheme.shapes.small,
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
        FloconIconButton(
            imageVector = Icons.Filled.Delete,
            onClick = { onDeleteClicked(item.id) }
        )
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
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
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
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
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
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
            )
        }
    }
}
