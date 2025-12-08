package io.github.openflocon.flocondesktop.features.dashboard.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class DashboardContainerViewState(
    val containerName: String,
    val containerConfig: ContainerConfig,
    val rows: List<RowItem>,
) {
    @Immutable
    sealed interface RowItem {
        @Immutable
        data class Text(
            val label: String,
            val value: String,
            val color: Color?,
        ) : RowItem

        @Immutable
        data class Label(
            val label: String,
            val color: Color?,
        ) : RowItem

        @Immutable
        data class PlainText(
            val label: String,
            val value: String,
        ) : RowItem

        @Immutable
        data class Button(
            val text: String,
            val id: String,
        ) : RowItem

        @Immutable
        data class TextField(
            val label: String,
            val placeHolder: String?,
            override val value: String,
            override val id: String,
        ) : RowItem,
            InputItem

        @Immutable
        data class CheckBox(
            val label: String,
            override val value: Boolean,
            override val id: String,
        ) : RowItem,
            InputItem

        @Immutable
        data class Markdown(
            val label: String,
            val value: String,
        ) : RowItem

        @Immutable
        data class Html(
            val label: String,
            val value: String,
        ) : RowItem
    }

    /** Any item that can be used to change the value of a field */
    @Immutable
    interface InputItem {
        val id: String
        val value: Any
    }

    @Immutable
    sealed interface ContainerConfig {
        @Immutable
        data class Form(
            val formId: String,
            val submitText: String,
        ) : ContainerConfig

        @Immutable
        object Section : ContainerConfig
    }
}

fun previewDashboardContainerViewState() = DashboardContainerViewState(
    containerName = "User",
    containerConfig = DashboardContainerViewState.ContainerConfig.Section,
    rows = listOf(
        DashboardContainerViewState.RowItem.Text("username", "flo", color = null),
        DashboardContainerViewState.RowItem.Text("user.id", "1234567", color = Color.Red),
        DashboardContainerViewState.RowItem.CheckBox(label = "isEnabled", value = true, "id"),
        DashboardContainerViewState.RowItem.TextField(
            label = "change name",
            value = "florent",
            id = "id",
            placeHolder = "new name"
        ),
        DashboardContainerViewState.RowItem.Button("click me", "id"),
    ),
)
