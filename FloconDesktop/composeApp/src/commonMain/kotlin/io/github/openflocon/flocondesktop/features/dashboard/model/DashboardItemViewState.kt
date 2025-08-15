package io.github.openflocon.flocondesktop.features.dashboard.model

import androidx.compose.ui.graphics.Color

data class DashboardItemViewState(
    val sectionName: String,
    val rows: List<RowItem>,
) {
    sealed interface RowItem {
        data class Text(
            val label: String,
            val value: String,
            val color: Color?,
        ) : RowItem
        data class PlainText(
            val label: String,
            val value: String,
        ) : RowItem
        data class Button(
            val text: String,
            val id: String,
        ) : RowItem
        data class TextField(
            val label: String,
            val placeHolder: String?,
            val value: String,
            val id: String,
        ) : RowItem
        data class CheckBox(
            val label: String,
            val value: Boolean,
            val id: String,
        ) : RowItem
    }
}

fun previewDashboardItemViewState() = DashboardItemViewState(
    sectionName = "User",
    rows = listOf(
        DashboardItemViewState.RowItem.Text("username", "flo", color = null),
        DashboardItemViewState.RowItem.Text("user.id", "1234567", color = Color.Red),
        DashboardItemViewState.RowItem.CheckBox(label = "isEnabled", value = true, "id"),
        DashboardItemViewState.RowItem.TextField(label = "change name", value = "florent", id = "id", placeHolder = "new name"),
        DashboardItemViewState.RowItem.Button("click me", "id"),
    ),
)
