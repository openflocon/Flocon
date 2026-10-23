package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState.ContainerConfig
import io.github.openflocon.flocondesktop.features.dashboard.model.previewDashboardContainerViewState
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardButton
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardCheckBox
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardHtml
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardLabel
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardMarkdown
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardPlainText
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardText
import io.github.openflocon.flocondesktop.features.dashboard.view.items.DashboardTextField
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DashboardContainer(
    viewState: DashboardContainerViewState,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    submitForm: (formId: String, values: Map<String, Any>) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    onOpenExternalClicked: (content: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputState by
        remember(viewState) {
            mutableStateOf(
                viewState.rows.filterIsInstance<DashboardContainerViewState.InputItem>()
                    .associate { it.id to it.value }
            )
        }

    Box(
        modifier =
        modifier.border(
            width = 1.dp,
            color = FloconTheme.colors.bg200,
            shape = FloconTheme.shapes.medium
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.medium),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = FloconTheme.spacing.extraSmall),
                textAlign = TextAlign.Center,
                text = viewState.containerName,
                style =
                FloconTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = FloconTheme.colors.ink,
            )

            Column(
                modifier = Modifier.padding(top = FloconTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            ) {
                viewState.rows.fastForEach { rowItem ->
                    when (rowItem) {
                        is DashboardContainerViewState.RowItem.TextField -> {
                            DashboardTextField(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                value = inputState[rowItem.id].toString(),
                                onValueChange = {
                                    inputState =
                                        inputState.toMutableMap().apply {
                                            this[rowItem.id] = it
                                        }
                                },
                                onSubmit = {
                                    submitTextField(
                                        rowItem.id,
                                        inputState[rowItem.id].toString()
                                    )
                                },
                                // A form only needs a single submit button, not on every
                                // textfield
                                showSubmitButton =
                                viewState.containerConfig !is ContainerConfig.Form
                            )
                        }

                        is DashboardContainerViewState.RowItem.CheckBox -> {
                            DashboardCheckBox(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                value = inputState[rowItem.id] as Boolean,
                                onCheckedChange = {
                                    inputState =
                                        inputState.toMutableMap().apply {
                                            this[rowItem.id] = it
                                        }
                                    onUpdateCheckBox(rowItem.id, it)
                                }
                            )
                        }

                        is DashboardContainerViewState.RowItem.Button -> {
                            DashboardButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClickButton = onClickButton,
                                rowItem = rowItem,
                            )
                        }

                        is DashboardContainerViewState.RowItem.Text -> {
                            DashboardText(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }

                        is DashboardContainerViewState.RowItem.Label -> {
                            DashboardLabel(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }

                        is DashboardContainerViewState.RowItem.PlainText -> {
                            DashboardPlainText(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                                onOpenExternalClicked = onOpenExternalClicked,
                            )
                        }

                        is DashboardContainerViewState.RowItem.Markdown -> {
                            DashboardMarkdown(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }

                        is DashboardContainerViewState.RowItem.Html -> {
                            DashboardHtml(
                                modifier = Modifier.fillMaxWidth(),
                                rowItem = rowItem,
                            )
                        }
                    }
                }

                // Submit button for forms
                if (viewState.containerConfig is ContainerConfig.Form) {
                    DashboardButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClickButton = {
                            submitForm(
                                viewState.containerConfig.formId,
                                inputState,
                            )
                        },
                        rowItem =
                        DashboardContainerViewState.RowItem.Button(
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
private fun DashboardContainerPreview() {
    FloconTheme {
        Box(modifier = Modifier.background(FloconTheme.colors.bg000).padding(all = FloconTheme.spacing.small)) {
            DashboardContainer(
                onClickButton = {},
                viewState = previewDashboardContainerViewState(),
                submitTextField = { _, _ -> },
                submitForm = { _, _ -> },
                onUpdateCheckBox = { _, _ -> },
                onOpenExternalClicked = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
