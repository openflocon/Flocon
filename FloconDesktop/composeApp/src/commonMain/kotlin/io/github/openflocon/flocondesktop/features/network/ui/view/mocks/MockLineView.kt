package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkMethodUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MockLineView(
    item: MockNetworkLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    changeIsEnabled: (id: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.height(12.dp)) {
            Switch(
                modifier = Modifier.scale(0.6f),
                checked = item.isEnabled,
                onCheckedChange = {
                    changeIsEnabled(item.id, it)
                },
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
                        color = FloconTheme.colorPalette.panel.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            )

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
fun MockLineViewPreview() {
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
fun MockLineViewPreview_url() {
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
fun MockLineViewPreview_url_patch() {
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
