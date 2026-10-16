package io.github.openflocon.flocon.pluginsold.dashboard.model

import io.github.openflocon.flocon.pluginsold.dashboard.model.config.ContainerConfig

data class DashboardConfig(
    val id: String,
    val containers: List<ContainerConfig>
)