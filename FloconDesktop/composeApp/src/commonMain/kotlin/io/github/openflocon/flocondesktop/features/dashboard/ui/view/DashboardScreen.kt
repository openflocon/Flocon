package io.github.openflocon.flocondesktop.features.dashboard.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.features.dashboard.ui.DashboardViewModel
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
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
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(FloconColors.pannel)
                    .padding(all = 12.dp),
            ) {
                Text(
                    text = "Dashboard",
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = FloconTheme.typography.titleLarge,
                    color = FloconTheme.colorPalette.onSurface,
                )

                DashboardSelectorView(
                    dashboardsState = deviceDashboards,
                    onDashboardSelected = onDashboardSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            state?.let {
                DashboardView(
                    modifier = Modifier.fillMaxSize().padding(all = 8.dp),
                    viewState = state,
                    onClickButton = onClickButton,
                    submitTextField = submitTextField,
                    onUpdateCheckBox = onUpdateCheckBox,
                )
            }
        }
    }
}
