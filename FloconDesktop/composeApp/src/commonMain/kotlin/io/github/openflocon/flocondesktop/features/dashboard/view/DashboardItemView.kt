package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardItemViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.previewDashboardItemViewState
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardButtonView
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardCheckBoxView
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardPlainTextView
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardTextFieldView
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardTextView
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardItemView(
    viewState: DashboardItemViewState,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backColor = FloconTheme.colorPalette.primary
    val textColor = FloconTheme.colorPalette.onSurface

    Box(
        modifier = modifier.shadow(
            elevation = 4.dp,
            clip = true,
            shape = RoundedCornerShape(20.dp),
        ).background(backColor),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Increase padding for more breathing room
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                textAlign = TextAlign.Center,
                text = viewState.sectionName,
                style = FloconTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = textColor,
            )

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Increase padding for more breathing room
                viewState.rows.fastForEach { rowItem ->
                    when (rowItem) {
                        is DashboardItemViewState.RowItem.TextField -> {
                            DashboardTextFieldView(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                submitTextField = submitTextField,
                            )
                        }

                        is DashboardItemViewState.RowItem.CheckBox -> {
                            DashboardCheckBoxView(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                onUpdateCheckBox = onUpdateCheckBox,
                            )
                        }

                        is DashboardItemViewState.RowItem.Button -> {
                            DashboardButtonView(
                                modifier = Modifier.fillMaxWidth(),
                                onClickButton = onClickButton,
                                rowItem = rowItem,
                            )
                        }

                        is DashboardItemViewState.RowItem.Text -> {
                            DashboardTextView(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }

                        is DashboardItemViewState.RowItem.PlainText -> {
                            DashboardPlainTextView(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardItemViewPreview() {
    FloconTheme {
        Box(modifier = Modifier.background(Color.White).padding(all = 8.dp)) {
            DashboardItemView(
                onClickButton = {},
                viewState = previewDashboardItemViewState(),
                submitTextField = { _, _ -> },
                onUpdateCheckBox = { _, _ -> },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
