package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse

internal class FloconNetworkDataSourceImpl(
    private val encoder: FloconEncoder
) : FloconNetworkDataSource {

    override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {
        // TODO
    }

    override fun loadMocksFromFile(): List<MockNetworkResponse> {
        return emptyList()
    }

    override fun saveBadNetworkConfig(config: BadQualityConfig?) {
        // TODO
    }

    override fun loadBadNetworkConfig(): BadQualityConfig? {
        return null
    }

}