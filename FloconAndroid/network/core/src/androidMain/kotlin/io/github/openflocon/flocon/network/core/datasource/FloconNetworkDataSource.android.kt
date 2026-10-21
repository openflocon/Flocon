package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.core.FloconEncoder

internal actual inline fun buildFloconNetworkDataSource(
    context: FloconContext,
    encoder: FloconEncoder
): FloconNetworkDataSource = FloconNetworkDataSourceAndroid(
    context = context.context,
    encoder = encoder
)