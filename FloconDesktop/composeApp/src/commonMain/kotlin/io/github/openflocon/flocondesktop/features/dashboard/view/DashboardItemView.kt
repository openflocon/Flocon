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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
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
fun DashboardContainerView(
    viewState: DashboardItemViewState,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    submitForm: (formId: String, values: Map<String, Any>) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backColor = FloconTheme.colorPalette.primary
    val textColor = FloconTheme.colorPalette.onSurface

    var inputState by remember {
        mutableStateOf(
            viewState.rows
                .filterIsInstance<DashboardItemViewState.InputItem>()
                .associate { it.id to it.value }
        )
    }

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
                text = viewState.containerName,
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
                                value = inputState[rowItem.id].toString(),
                                onValueChange = {
                                    inputState = inputState.toMutableMap().apply {
                                        this[rowItem.id] = it
                                    }
                                },
                                onSubmit = {
                                    submitTextField(rowItem.id, inputState[rowItem.id].toString())
                                },
                                // A form only needs a single submit button, not on every textfield
                                showSubmitButton = viewState.containerConfig !is FormContainerConfigDomainModel
                            )
                        }

                        is DashboardItemViewState.RowItem.CheckBox -> {
                            DashboardCheckBoxView(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                value = inputState[rowItem.id] as Boolean,
                                onCheckedChange = {
                                    inputState = inputState.toMutableMap().apply {
                                        this[rowItem.id] = it
                                    }
                                    onUpdateCheckBox(rowItem.id, it)
                                }
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

                // Submit button for forms
                if (viewState.containerConfig is FormContainerConfigDomainModel) {
                    DashboardButtonView(
                        modifier = Modifier.fillMaxWidth(),
                        onClickButton = {
                            submitForm(
                                viewState.containerConfig.formId,
                                inputState,
                            )
                        },
                        rowItem = DashboardItemViewState.RowItem.Button(
                            id = "_",
                            text = viewState.containerConfig.submitText,
                        ),
                    )
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
            DashboardContainerView(
                onClickButton = {},
                viewState = previewDashboardItemViewState(),
                submitTextField = { _, _ -> },
                submitForm = { _, _ -> },
                onUpdateCheckBox = { _, _ -> },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
