package io.github.openflocon.flocon.plugins.dashboard.builder

import io.github.openflocon.flocon.plugins.dashboard.dsl.DashboardDsl
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig

@DashboardDsl
class DashboardBuilder(private val id: String) {
    private val containers = mutableListOf<ContainerConfig>()

    fun add(container: ContainerConfig) {
        containers.add(container)
    }

    fun build(): DashboardConfig {
        return DashboardConfig(
            id = id,
            containers = containers
        )
    }
}