package io.github.openflocon.flocondesktop.features.dashboard.ui.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.domain.models.DashboardDomainModel
import io.github.openflocon.domain.models.DashboardElementDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardItemViewState
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardViewState

internal fun DashboardDomainModel.toUi(): DashboardViewState = DashboardViewState(
    items = sections.map {
        DashboardItemViewState(
            sectionName = it.name,
            rows = it.elements.map {
                when (it) {
                    is DashboardElementDomainModel.Button -> DashboardItemViewState.RowItem.Button(
                        text = it.text,
                        id = it.id,
                    )

                    is DashboardElementDomainModel.Text -> DashboardItemViewState.RowItem.Text(
                        label = it.label,
                        value = it.value,
                        color = it.color?.let { Color(it) },
                    )

                    is DashboardElementDomainModel.PlainText -> DashboardItemViewState.RowItem.PlainText(
                        label = it.label,
                        value = when (it.type) {
                            DashboardElementDomainModel.PlainText.Type.Text -> it.value
                            DashboardElementDomainModel.PlainText.Type.Json -> JsonPrettyPrinter.prettyPrint(it.value)
                        },
                    )

                    is DashboardElementDomainModel.TextField -> DashboardItemViewState.RowItem.TextField(
                        label = it.label,
                        value = it.value,
                        placeHolder = it.placeHolder,
                        id = it.id,
                    )

                    is DashboardElementDomainModel.CheckBox -> DashboardItemViewState.RowItem.CheckBox(
                        label = it.label,
                        value = it.value,
                        id = it.id,
                    )
                }
            },
        )
    },
)
