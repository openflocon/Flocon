package io.github.openflocon.flocondesktop.features.dashboard.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState.ContainerConfig
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState

internal fun DashboardDomainModel.toUi(): DashboardViewState = DashboardViewState(
    items = containers.map { container ->
        DashboardContainerViewState(
            containerName = container.name,
            containerConfig = container.containerConfig.toUI(),
            rows = container.elements.map { element ->
                when (element) {
                    is DashboardElementDomainModel.Button -> DashboardContainerViewState.RowItem.Button(
                        text = element.text,
                        id = element.id,
                    )

                    is DashboardElementDomainModel.Text -> DashboardContainerViewState.RowItem.Text(
                        label = element.label,
                        value = element.value,
                        color = element.color?.let { Color(it) },
                    )

                    is DashboardElementDomainModel.Label -> DashboardContainerViewState.RowItem.Label(
                        label = element.label,
                        color = element.color?.let { Color(it) },
                    )

                    is DashboardElementDomainModel.PlainText -> DashboardContainerViewState.RowItem.PlainText(
                        label = element.label,
                        value = when (element.type) {
                            DashboardElementDomainModel.PlainText.Type.Text -> element.value
                            DashboardElementDomainModel.PlainText.Type.Json -> JsonPrettyPrinter.prettyPrint(
                                element.value
                            )
                        },
                    )

                    is DashboardElementDomainModel.TextField -> DashboardContainerViewState.RowItem.TextField(
                        label = element.label,
                        value = element.value,
                        placeHolder = element.placeHolder,
                        id = element.id,
                    )

                    is DashboardElementDomainModel.CheckBox -> DashboardContainerViewState.RowItem.CheckBox(
                        label = element.label,
                        value = element.value,
                        id = element.id,
                    )
                }
            },
        )
    },
)

internal fun ContainerConfigDomainModel.toUI(): ContainerConfig =
    when (this) {
        is FormContainerConfigDomainModel -> ContainerConfig.Form(
            formId = formId,
            submitText = submitText,
        )

        SectionContainerConfigDomainModel -> ContainerConfig.Section
    }
