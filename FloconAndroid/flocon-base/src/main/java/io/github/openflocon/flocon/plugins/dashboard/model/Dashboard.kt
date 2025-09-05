package io.github.openflocon.flocon.plugins.dashboard.model

import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig

data class DashboardConfig(
    val id: String,
    val containers: List<ContainerConfig>
)