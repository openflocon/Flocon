package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.pluginsold.network.model.BadQualityConfig
import io.github.openflocon.flocon.pluginsold.network.model.MockNetworkResponse

internal actual fun buildFloconNetworkDataSource(context: FloconContext): FloconNetworkDataSource {
    return FloconNetworkDataSourceIOs()
}

// TODO
internal class FloconNetworkDataSourceIOs : FloconNetworkDataSource {
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