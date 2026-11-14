@file:OptIn(ExperimentalForeignApi::class)

package io.github.openflocon.flocon.network.core.datasource

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.network.core.mapper.decodeBadQualityConfig
import io.github.openflocon.flocon.network.core.mapper.decodeMocks
import io.github.openflocon.flocon.network.core.mapper.encodeBadQualityConfig
import io.github.openflocon.flocon.network.core.mapper.encodeMocks
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse
import io.github.openflocon.flocon.network.core.plugin.FLOCON_NETWORK_BAD_CONFIG_JSON
import io.github.openflocon.flocon.network.core.plugin.FLOCON_NETWORK_MOCKS_JSON
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

internal class FloconNetworkDataSourceImpl(
    private val encoder: FloconEncoder
) : FloconNetworkDataSource {

    private val fileManager = NSFileManager.defaultManager

    private val baseDir: String by lazy {
        val base = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: "."
        val dir = "$base/flocon"
        fileManager.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
        dir
    }

    override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {
        try {
            write(FLOCON_NETWORK_MOCKS_JSON, encoder.encodeMocks(mocks))
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveMocksToFile", t)
        }
    }

    override fun loadMocksFromFile(): List<MockNetworkResponse> {
        return try {
            read(FLOCON_NETWORK_MOCKS_JSON)?.let { encoder.decodeMocks(it) } ?: emptyList()
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadMocksFromFile", t)
            emptyList()
        }
    }

    override fun saveBadNetworkConfig(config: BadQualityConfig?) {
        try {
            if (config == null) {
                fileManager.removeItemAtPath("$baseDir/$FLOCON_NETWORK_BAD_CONFIG_JSON", error = null)
            } else {
                write(FLOCON_NETWORK_BAD_CONFIG_JSON, encoder.encodeBadQualityConfig(config))
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveBadNetworkConfig", t)
        }
    }

    override fun loadBadNetworkConfig(): BadQualityConfig? {
        return try {
            read(FLOCON_NETWORK_BAD_CONFIG_JSON)?.let { encoder.decodeBadQualityConfig(it) }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadBadNetworkConfig", t)
            null
        }
    }

    private fun write(fileName: String, content: String) {
        NSString.create(string = content).writeToFile(
            path = "$baseDir/$fileName",
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
    }

    private fun read(fileName: String): String? {
        val path = "$baseDir/$fileName"
        if (!fileManager.fileExistsAtPath(path)) return null
        return NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)
    }
}
