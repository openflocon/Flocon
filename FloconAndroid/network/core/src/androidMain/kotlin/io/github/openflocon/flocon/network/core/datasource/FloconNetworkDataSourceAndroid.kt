package io.github.openflocon.flocon.network.core.datasource

import android.content.Context
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse
import io.github.openflocon.flocon.network.core.plugin.FLOCON_NETWORK_BAD_CONFIG_JSON
import io.github.openflocon.flocon.network.core.plugin.FLOCON_NETWORK_MOCKS_JSON
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal class FloconNetworkDataSourceAndroid(
    private val context: Context,
    private val encoder: FloconEncoder
) : FloconNetworkDataSource {
    override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {
        try {
            val file = File(context.filesDir, FLOCON_NETWORK_MOCKS_JSON)
            val jsonString = encoder.encode(mocks)
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

            encoder.decode<List<MockNetworkResponse>>(jsonString)
                .orEmpty()
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

            encoder.decode(jsonString)
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
                val jsonString = encoder.encode(config)
                FileOutputStream(file).use {
                    it.write(jsonString.toByteArray())
                }
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveBadNetworkConfig", t)
        }
    }
}