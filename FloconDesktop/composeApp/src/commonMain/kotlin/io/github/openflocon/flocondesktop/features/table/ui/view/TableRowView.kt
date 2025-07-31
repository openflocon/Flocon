package io.github.openflocon.flocondesktop.features.table.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.table.ui.model.TableRowUiModel
import com.florent37.flocondesktop.features.table.ui.model.previewTableRowUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TableRowView(
    model: TableRowUiModel,
    columnsWidth: Dp = 150.dp,
    onClick: (model: TableRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = {
                onClick(model)
            }).padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row {
            model.columns.fastForEach { column ->
                Text(
                    text = column,
                    modifier =
                    Modifier
                        .width(columnsWidth)
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Row(modifier = Modifier.padding(top = 4.dp)) {
            model.values.fastForEach { column ->
                Text(
                    text = column,
                    modifier =
                    Modifier
                        .width(columnsWidth)
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Thin),
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
        TableRowView(
            model = previewTableRowUiModel(),
            onClick = {},
        )
    }
}
