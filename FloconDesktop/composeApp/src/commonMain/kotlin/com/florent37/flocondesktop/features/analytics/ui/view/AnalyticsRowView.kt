package com.florent37.flocondesktop.features.analytics.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.analytics.ui.model.AnalyticsRowUiModel
import com.florent37.flocondesktop.features.analytics.ui.model.previewAnalyticsRowUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnalyticsRowView(
    model: AnalyticsRowUiModel,
    onClick: (model: AnalyticsRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = FloconColors.pannel)
            .clickable(onClick = {
                onClick(model)
            })
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = model.eventName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Text(
            text = model.dateFormatted,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f),
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            model.properties.fastForEach {
                Row(
                    Modifier
                        .background(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = it.name + " :",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = it.value,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            if (model.hasMoreProperties) {
                Text(
                    text = "...",
                    modifier =
                    Modifier
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        AnalyticsRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewAnalyticsRowUiModel(),
            onClick = {},
        )
    }
}
