package io.github.openflocon.flocondesktop.features.network.badquality.list.view

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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BadNetworkLineView(
    item: NetworkBadQualityLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    enableClicked: (id: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
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
                    enableClicked(item.id, it)
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
            Text(
                text = item.name,
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
