package com.flocon.data.remote.server

import kotlinx.serialization.json.Json

actual fun getServer(json: Json): Server = ServerJvm(json)
