package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.dashboard.DashboardViewModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val viewModel: DashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val deviceDashboards by viewModel.deviceDashboards.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    DashboardScreen(
        state = state,
        deviceDashboards = deviceDashboards,
        modifier = modifier,
        onDashboardSelected = viewModel::onDashboardSelected,
        onClickButton = viewModel::onButtonClicked,
        submitTextField = viewModel::onTextFieldSubmit,
        submitForm = viewModel::onFormSubmit,
        onUpdateCheckBox = viewModel::onUpdateCheckBox,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardViewState?,
    deviceDashboards: DashboardsStateUiModel,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    submitForm: (formId: String, formValues: Map<String, Any>) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(
        modifier = modifier
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                DashboardSelectorView(
                    dashboardsState = deviceDashboards,
                    onDashboardSelected = onDashboardSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        )

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
            )
        }
    }
}
