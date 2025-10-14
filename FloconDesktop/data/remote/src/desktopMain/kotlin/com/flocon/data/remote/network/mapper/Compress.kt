package com.flocon.data.remote.network.mapper

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.success
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream


actual fun decompressGzipBytes(compressedData: ByteArray): Either<Throwable, String> {
    return try {
        val inputStream = GZIPInputStream(ByteArrayInputStream(compressedData))
        val outputStream = ByteArrayOutputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        outputStream.toString("UTF-8").success()
    } catch (t: Throwable) {
        return Failure(t)
    }
}

actual fun decompressDeflateBytes(compressedData: ByteArray): Either<Throwable, String> {
    return try {
        val inputStream = InflaterInputStream(ByteArrayInputStream(compressedData))
        val outputStream = ByteArrayOutputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        outputStream.toString("UTF-8").success()
    } catch (t: Throwable) {
        return Failure(t)
    }
}
