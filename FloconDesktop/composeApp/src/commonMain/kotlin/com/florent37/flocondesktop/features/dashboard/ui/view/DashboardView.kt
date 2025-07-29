package com.florent37.flocondesktop.features.dashboard.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.dashboard.ui.model.DashboardViewState
import com.florent37.flocondesktop.features.dashboard.ui.model.previewDashboardViewState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardView(
    viewState: DashboardViewState,
    onClickButton: (buttonId: String) -> Unit,
    submitTextField: (textFieldId: String, value: String) -> Unit,
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
            DashboardItemView(
                modifier = Modifier.fillMaxWidth(),
                viewState = it,
                onClickButton = onClickButton,
                submitTextField = submitTextField,
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
            onUpdateCheckBox = { _, _ -> },
        )
    }
}
