package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse

internal interface FloconNetworkDataSource {
    fun saveMocksToFile(mocks: List<MockNetworkResponse>)
    fun loadMocksFromFile(): List<MockNetworkResponse>
    fun saveBadNetworkConfig(config: BadQualityConfig?)
    fun loadBadNetworkConfig(): BadQualityConfig?
}

internal expect inline fun buildFloconNetworkDataSource(
    context: FloconContext,
    encoder: FloconEncoder
): FloconNetworkDataSource