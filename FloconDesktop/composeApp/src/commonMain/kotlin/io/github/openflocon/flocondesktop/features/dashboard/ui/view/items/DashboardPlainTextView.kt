package io.github.openflocon.flocondesktop.features.dashboard.ui.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.dashboard.ui.model.DashboardItemViewState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DashboardPlainTextView(
    modifier: Modifier = Modifier,
    rowItem: DashboardItemViewState.RowItem.PlainText,
) {
    SelectionContainer(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                rowItem.label,
                modifier = Modifier.padding(start = 4.dp),
                color = FloconColors.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Thin,
                ),
            )
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    ).padding(8.dp),
            ) {
                SelectionContainer {
                    Text(
                        text = rowItem.value,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DashboardPlainTextViewPreview() {
    val rowItem = DashboardItemViewState.RowItem.PlainText(
        label = "label",
        value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ullamcorper elit. Pellentesque turpis ex, cursus cursus urna sed, iaculis sagittis nisl. Curabitur vehicula nunc eu metus rhoncus placerat. Vivamus at placerat ligula. Morbi ullamcorper cursus tellus, vitae molestie lorem sollicitudin euismod. Sed ullamcorper, risus vitae facilisis tempor, elit leo accumsan purus, ut ultricies augue erat et justo. Duis efficitur mauris eu finibus tincidunt. Aenean magna libero, auctor quis turpis et, viverra porta lorem. Ut tempus odio sit amet vestibulum condimentum. Donec et augue quis arcu blandit sodales. In laoreet odio id turpis ultricies, eu ornare dui blandit. Morbi hendrerit velit turpis, eget ornare ex consequat id. Nullam rhoncus, libero et sollicitudin tristique, risus ipsum luctus neque, ultricies ullamcorper felis metus non turpis. Nullam sed accumsan sem, at fermentum tortor.",
    )
    FloconTheme {
        DashboardPlainTextView(
            modifier = Modifier.background(
                FloconColors.pannel,
            ),
            rowItem = rowItem,
        )
    }
}

@Preview
@Composable
internal fun DashboardPlainTextViewPreview_json() {
    val rowItem = DashboardItemViewState.RowItem.PlainText(
        label = "json",
        value = """
                                {
                                  "testData": {
                                    "name": "John Doe",
                                    "age": 30,
                                    "isStudent": false,
                                    "courses": [
                                      {
                                        "title": "History I",
                                        "credits": 3
                                      },
                                      {
                                        "title": "Math II",
                                        "credits": 4
                                      }
                                    ],
                                    "address": {
                                      "street": "123 Main St",
                                      "city": "Anytown",
                                      "zipCode": "12345"
                                    }
                                  },
                                  "status": "success",
                                  "message": "Test data loaded successfully."
                                }
        """.trimIndent(),
    )
    FloconTheme {
        DashboardPlainTextView(
            modifier = Modifier.background(
                FloconColors.pannel,
            ),
            rowItem = rowItem,
        )
    }
}
