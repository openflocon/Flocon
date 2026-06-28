package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconContext

internal actual inline fun buildFloconNetworkDataSource(
    context: FloconContext
): FloconNetworkDataSource = FloconNetworkDataSourceImpl()