package io.github.openflocon.flocondesktop.features.dashboard.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardItemViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState

internal fun DashboardDomainModel.toUi(): DashboardViewState = DashboardViewState(
    items = containers.map { container ->
        DashboardItemViewState(
            containerName = container.name,
            containerConfig = container.containerConfig,
            rows = container.elements.map { element ->
                when (element) {
                    is DashboardElementDomainModel.Button -> DashboardItemViewState.RowItem.Button(
                        text = element.text,
                        id = element.id,
                    )

                    is DashboardElementDomainModel.Text -> DashboardItemViewState.RowItem.Text(
                        label = element.label,
                        value = element.value,
                        color = element.color?.let { Color(it) },
                    )

                    is DashboardElementDomainModel.PlainText -> DashboardItemViewState.RowItem.PlainText(
                        label = element.label,
                        value = when (element.type) {
                            DashboardElementDomainModel.PlainText.Type.Text -> element.value
                            DashboardElementDomainModel.PlainText.Type.Json -> JsonPrettyPrinter.prettyPrint(element.value)
                        },
                    )

                    is DashboardElementDomainModel.TextField -> DashboardItemViewState.RowItem.TextField(
                        label = element.label,
                        value = element.value,
                        placeHolder = element.placeHolder,
                        id = element.id,
                    )

                    is DashboardElementDomainModel.CheckBox -> DashboardItemViewState.RowItem.CheckBox(
                        label = element.label,
                        value = element.value,
                        id = element.id,
                    )
                }
            },
        )
    },
)
