package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.open_external
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DashboardPlainText(
    modifier: Modifier = Modifier,
    rowItem: DashboardContainerViewState.RowItem.PlainText,
    onOpenExternalClicked: (content: String) -> Unit,
) {
    SelectionContainer(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    rowItem.label,
                    modifier =
                    Modifier.padding(start = FloconTheme.spacing.extraSmall)
                        .align(Alignment.CenterStart),
                    color = FloconTheme.colors.ink,
                    style =
                    FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Thin,
                    ),
                )
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    FloconIconButton(
                        tooltip = stringResource(Res.string.open_external),
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        onClick = { onOpenExternalClicked(rowItem.value) },
                    )
                }
            }
            Box(
                modifier =
                Modifier.fillMaxWidth()
                    .background(
                        color = FloconTheme.colors.bg200,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(FloconTheme.spacing.small),
            ) {
                SelectionContainer {
                    Text(
                        text = rowItem.value,
                        color = FloconTheme.colors.ink,
                        style =
                        FloconTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DashboardPlainTextPreview() {
    val rowItem =
        DashboardContainerViewState.RowItem.PlainText(
            label = "label",
            value =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ullamcorper elit. Pellentesque turpis ex, cursus cursus urna sed, iaculis sagittis nisl. Curabitur vehicula nunc eu metus rhoncus placerat. Vivamus at placerat ligula. Morbi ullamcorper cursus tellus, vitae molestie lorem sollicitudin euismod. Sed ullamcorper, risus vitae facilisis tempor, elit leo accumsan purus, ut ultricies augue erat et justo. Duis efficitur mauris eu finibus tincidunt. Aenean magna libero, auctor quis turpis et, viverra porta lorem. Ut tempus odio sit amet vestibulum condimentum. Donec et augue quis arcu blandit sodales. In laoreet odio id turpis ultricies, eu ornare dui blandit. Morbi hendrerit velit turpis, eget ornare ex consequat id. Nullam rhoncus, libero et sollicitudin tristique, risus ipsum luctus neque, ultricies ullamcorper felis metus non turpis. Nullam sed accumsan sem, at fermentum tortor.",
        )
    FloconTheme {
        DashboardPlainText(
            modifier =
            Modifier.fillMaxWidth().background(
                FloconTheme.colors.bg100,
            ),
            rowItem = rowItem,
            onOpenExternalClicked = {},
        )
    }
}

@Preview
@Composable
internal fun DashboardPlainTextViewPreview_json() {
    val rowItem =
        DashboardContainerViewState.RowItem.PlainText(
            label = "json",
            value =
            """
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
        DashboardPlainText(
            modifier =
            Modifier.fillMaxWidth().background(
                FloconTheme.colors.bg100,
            ),
            rowItem = rowItem,
            onOpenExternalClicked = {},
        )
    }
}
