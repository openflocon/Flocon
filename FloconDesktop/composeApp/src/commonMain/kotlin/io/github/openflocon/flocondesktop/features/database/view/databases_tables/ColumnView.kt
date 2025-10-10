package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun ColumnView(
    model: TableUiModel.ColumnUiModel,
    onClicked: (TableUiModel.ColumnUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable {
                onClicked(model)
            }
            .padding(start = 46.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val color = FloconTheme.colorPalette.onSurface

        Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            Image(
                imageVector = Icons.Outlined.ViewColumn,
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
            )
            if (model.isPrimaryKey) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd).size(10.dp).background(
                            FloconTheme.colorPalette.accent,
                            shape = CircleShape,
                        ).padding(1.dp)
                ) {
                    Image(
                        imageVector = Icons.Filled.VpnKey,
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onAccent),
                        contentDescription = null,
                    )
                }
            }
        }
        Text(
            model.name,
            style = FloconTheme.typography.bodyMedium,
            color = color,
        )

        Text(
            model.type,
            style = FloconTheme.typography.bodyMedium,
            color = color.copy(alpha = 0.6f),
        )
    }
}
