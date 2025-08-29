package io.github.openflocon.flocondesktop.features.network.detail.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailHeadersView(
    headers: List<NetworkDetailHeaderUi>,
    labelWidth: Dp,
    onAuthorizationClicked: (value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        headers.fastForEachIndexed { index, item ->
            val isAuthBearer = item.name.equals(
                "authorization",
                ignoreCase = true
            ) && item.value.startsWith("Bearer ")
            if (isAuthBearer) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    FloconLineDescription(
                        label = item.name,
                        value = item.value,
                        labelWidth = labelWidth,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FloconButton(
                        onClick = {
                            onAuthorizationClicked(item.value)
                        }
                    ) {
                        Text("Decode\nJWT", textAlign = TextAlign.Center, color = FloconTheme.colorPalette.surface)
                    }
                }
            } else {
                FloconLineDescription(
                    label = item.name,
                    value = item.value,
                    labelWidth = labelWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (index != headers.lastIndex) {
                FloconHorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview
@Composable
private fun DetailHeadersViewPreview() {
    FloconTheme {
        DetailHeadersView(
            headers = listOf(
                previewNetworkDetailHeaderUi()
            ),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
            onAuthorizationClicked = {},
        )
    }
}
