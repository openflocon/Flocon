package com.flocon.data.remote.server

import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.serialization.json.Json

actual fun getServer(
    json: Json,
    networkRepository: NetworkRepository,
): Server = ServerJvm(json, networkRepository)
