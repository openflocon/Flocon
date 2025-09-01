package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.previewDashboardViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardView(
    viewState: DashboardViewState,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
    submitForm: (formId: String, values: Map<String, Any>) -> Unit,
    onUpdateCheckBox: (checkBoxId: String, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(viewState.items) {
            DashboardContainerView(
                modifier = Modifier.fillMaxWidth(),
                viewState = it,
                onClickButton = onClickButton,
                submitTextField = submitTextField,
                submitForm = submitForm,
                onUpdateCheckBox = onUpdateCheckBox,
            )
        }
    }
}

@Composable
@Preview
private fun DashboardViewPreview() {
    FloconTheme {
        DashboardView(
            viewState = previewDashboardViewState(),
            onClickButton = {},
            submitTextField = { _, _ -> },
            submitForm = { _, _ -> },
            onUpdateCheckBox = { _, _ -> },
        )
    }
}
