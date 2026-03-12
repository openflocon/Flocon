package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.pluginsold.network.model.BadQualityConfig
import io.github.openflocon.flocon.pluginsold.network.model.MockNetworkResponse

internal interface FloconNetworkDataSource {
    fun saveMocksToFile(mocks: List<MockNetworkResponse>)
    fun loadMocksFromFile(): List<MockNetworkResponse>
    fun saveBadNetworkConfig(config: BadQualityConfig?)
    fun loadBadNetworkConfig(): BadQualityConfig?
}

internal expect inline fun buildFloconNetworkDataSource(
    context: FloconContext
): FloconNetworkDataSource