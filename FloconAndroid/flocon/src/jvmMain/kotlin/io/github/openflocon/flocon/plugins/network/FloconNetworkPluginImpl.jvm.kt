package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.mapper.parseBadQualityConfig
import io.github.openflocon.flocon.plugins.network.mapper.parseMockResponses
import io.github.openflocon.flocon.plugins.network.mapper.toJsonString
import io.github.openflocon.flocon.plugins.network.mapper.writeMockResponsesToJson
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

