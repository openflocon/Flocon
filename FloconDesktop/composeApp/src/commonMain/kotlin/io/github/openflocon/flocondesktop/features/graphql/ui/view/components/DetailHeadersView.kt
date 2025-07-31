package io.github.openflocon.flocondesktop.features.graphql.ui.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.graphql.ui.model.GraphQlDetailHeaderUi
import com.florent37.flocondesktop.features.graphql.ui.model.previewGraphQlDetailHeaderUi
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailHeadersView(
    headers: List<GraphQlDetailHeaderUi>,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        headers.fastForEachIndexed { index, item ->
            DetailHeadersItemView(
                state = item,
                labelWidth = labelWidth,
                modifier = Modifier.fillMaxWidth(),
            )
            if (index != headers.lastIndex) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun DetailHeadersItemView(
    state: GraphQlDetailHeaderUi,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Row(
            modifier = modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.name,
                style = MaterialTheme.typography.bodySmall, // Slightly smaller title for details
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Muted label color
                modifier = Modifier.width(labelWidth).padding(end = 8.dp),
            )
            Text(
                text = state.value,
                style = MaterialTheme.typography.bodySmall, // Body text for the URL
                color = MaterialTheme.colorScheme.onBackground, // Primary text color
                modifier = Modifier.weight(1f), // Takes remaining space
            )
        }
    }
}

@Preview
@Composable
private fun DetailHeadersItemViewPreview() {
    FloconTheme {
        DetailHeadersItemView(
            state = previewGraphQlDetailHeaderUi(),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
