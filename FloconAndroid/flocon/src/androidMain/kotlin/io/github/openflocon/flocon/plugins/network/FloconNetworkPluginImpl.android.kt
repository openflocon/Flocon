package io.github.openflocon.flocon.plugins.network

import android.content.Context
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

internal actual fun buildFloconNetworkDataSource(context: FloconContext): FloconNetworkDataSource {
    return FloconNetworkDataSourceAndroid(
        context = context.appContext,
    )
}

internal class FloconNetworkDataSourceAndroid(private val context: Context) : FloconNetworkDataSource {
    override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {
        try {
            val file = File(context.filesDir, FLOCON_NETWORK_MOCKS_JSON)
            val jsonString = writeMockResponsesToJson(mocks = mocks)
            FileOutputStream(file).use {
                it.write(jsonString.toByteArray())
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveMocksToFile", t)
        }
    }

    override fun loadMocksFromFile(): List<MockNetworkResponse> {
        return try {
            val file = File(context.filesDir, FLOCON_NETWORK_MOCKS_JSON)
            if (!file.exists()) {
                return emptyList()
            }

            val jsonString = FileInputStream(file).use {
                it.readBytes().toString(Charsets.UTF_8)
            }
            parseMockResponses(jsonString = jsonString)
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadMocksFromFile", t)
            emptyList()
        }
    }

    override fun loadBadNetworkConfig(): BadQualityConfig? {
        return try {
            val file = File(context.filesDir, FLOCON_NETWORK_BAD_CONFIG_JSON)
            if (!file.exists()) {
                return null
            }

            val jsonString = FileInputStream(file).use {
                it.readBytes().toString(Charsets.UTF_8)
            }
            parseBadQualityConfig(jsonString = jsonString)
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadBadNetworkConfig", t)
            null
        }
    }

    override fun saveBadNetworkConfig(config: BadQualityConfig?) {
        try {
            val file = File(context.filesDir, FLOCON_NETWORK_BAD_CONFIG_JSON)
            if (config == null) {
                file.delete()
            } else {
                val jsonString = config.toJsonString()
                FileOutputStream(file).use {
                    it.write(jsonString.toByteArray())
                }
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveBadNetworkConfig", t)
        }
    }
}