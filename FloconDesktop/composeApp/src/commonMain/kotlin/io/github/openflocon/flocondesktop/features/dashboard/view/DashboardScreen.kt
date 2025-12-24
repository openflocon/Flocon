package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.dashboard_action_delete
import io.github.openflocon.flocondesktop.features.dashboard.DashboardViewModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val viewModel: DashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val deviceDashboards by viewModel.deviceDashboards.collectAsStateWithLifecycle()
    val arrangement by viewModel.arrangement.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    DashboardScreen(
        state = state,
        deviceDashboards = deviceDashboards,
        arrangement = arrangement,
        modifier = modifier,
        onDashboardSelected = viewModel::onDashboardSelected,
        onClickButton = viewModel::onButtonClicked,
        submitTextField = viewModel::onTextFieldSubmit,
        submitForm = viewModel::onFormSubmit,
        onUpdateCheckBox = viewModel::onUpdateCheckBox,
        deleteCurrentDashboard = viewModel::deleteCurrentDashboard,
        onDeleteClicked = viewModel::onDeleteClicked,
        onArrangementClicked = viewModel::onArrangementClicked,
        onOpenExternalClicked = viewModel::onOpenExternalClicked,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardViewState?,
    deviceDashboards: DashboardsStateUiModel,
    arrangement: DashboardArrangement,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    onDeleteClicked: (DeviceDashboardUiModel) -> Unit,
    deleteCurrentDashboard: () -> Unit,
    onArrangementClicked: (DashboardArrangement) -> Unit,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    submitForm: (formId: String, formValues: Map<String, Any>) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    onOpenExternalClicked: (content: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(
        modifier = modifier
    ) {
        Column(Modifier.fillMaxSize()) {
            FloconPageTopBar(
                modifier = Modifier.fillMaxWidth(),
                selector = null,
                filterBar = {
                    Box(modifier = Modifier.weight(1f)) // to have actions on the right
                },
                actions = {
                    DashboardArrangementView(
                        onArrangementClicked = onArrangementClicked,
                        arrangement = arrangement
                    )

                    FloconOverflow {
                        FloconDropdownMenuItem(
                            text = stringResource(Res.string.dashboard_action_delete),
                            leadingIcon = Icons.Outlined.Delete,
                            onClick = { deleteCurrentDashboard() }
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(Modifier.fillMaxSize()) {
                DashboardSidebarView(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(340.dp),
                    state = deviceDashboards,
                    onDashboardSelected = onDashboardSelected,
                    onDeleteClicked = onDeleteClicked,
                )

                Spacer(modifier = Modifier.width(12.dp))

                state?.let {
                    DashboardView(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(FloconTheme.shapes.medium)
                            .background(FloconTheme.colorPalette.primary)
                            .padding(all = 8.dp),
                        viewState = state,
                        onClickButton = onClickButton,
                        submitTextField = submitTextField,
                        submitForm = submitForm,
                        onUpdateCheckBox = onUpdateCheckBox,
                        arrangement = arrangement,
                        onOpenExternalClicked = onOpenExternalClicked,
                    )
                }
            }
        }
    }
}
