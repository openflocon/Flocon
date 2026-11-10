package com.flocon.data.remote.connect

import io.github.openflocon.domain.connect.repository.LocalNetworkRepository

actual fun getLocalNetworkRepository(): LocalNetworkRepository = LocalNetworkRepositoryImpl()
