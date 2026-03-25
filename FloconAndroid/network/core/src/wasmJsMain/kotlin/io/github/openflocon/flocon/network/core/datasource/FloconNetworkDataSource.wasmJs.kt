package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse

internal actual inline fun buildFloconNetworkDataSource(context: FloconContext): FloconNetworkDataSource = FloconNetworkDataSourceWasmJs()

internal class FloconNetworkDataSourceWasmJs : FloconNetworkDataSource {
    override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {}
    override fun loadMocksFromFile(): List<MockNetworkResponse> = emptyList()
    override fun saveBadNetworkConfig(config: BadQualityConfig?) {}
    override fun loadBadNetworkConfig(): BadQualityConfig? = null
}
