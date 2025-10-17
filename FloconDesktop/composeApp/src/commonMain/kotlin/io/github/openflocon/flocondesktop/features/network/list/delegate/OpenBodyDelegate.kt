package io.github.openflocon.flocondesktop.features.network.list.delegate

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.failure
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import io.github.openflocon.flocondesktop.common.utils.OpenFileError
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import java.io.File

class OpenBodyDelegate {

    fun openBodyExternally(request: NetworkDetailViewState) : Either<OpenFileError, Unit> {
        return openAndWriteBody(
            prefix = "request_body_",
            body = request.requestBody
        )
    }

    fun openBodyExternally(response: NetworkDetailViewState.Response.Success) : Either<OpenFileError, Unit> {
        return openAndWriteBody(
            prefix = "response_body_",
            body = response.body
        )
    }

    /**
     * Centralizes the logic for writing the body to a temp file and opening it.
     */
    private fun openAndWriteBody(prefix: String, body: String): Either<OpenFileError, Unit> {
        if (body.isBlank()) {
            Logger.w("⚠️ Body is empty, nothing to open.")
            return OpenFileError.FileNotFound.failure()
        }

        val extension = detectBodyExtension(body)

        val tempFile = runCatching {
            File.createTempFile(prefix, ".$extension")
        }.getOrElse { e ->
            Logger.e("❌ Failed to create temporary file: ${e.message}")
            return OpenFileError.IoError(e).failure()
        }

        runCatching {
            tempFile.writeText(body)
            Logger.i("📝 Body written to temporary file: ${tempFile.absolutePath}")
        }.onFailure { e ->
            Logger.e("💥 Error while writing file: ${e.message}")
            return OpenFileError.IoError(e).failure()
        }

        return OpenFile.openFileOnDesktop(tempFile.absolutePath)
            .alsoFailure { error ->
                Logger.e("❌ Failed to open file: ${error.message}")
            }.alsoSuccess {
                Logger.i("✅ File opened successfully: ${tempFile.absolutePath}")
            }
    }

    /**
     * Tries to infer file extension based on content.
     */
    private fun detectBodyExtension(body: String): String {
        val trimmed = body.trimStart()

        return when {
            trimmed.startsWith("{") || trimmed.startsWith("[") -> "json"
            trimmed.startsWith("<") && trimmed.contains("</") && !trimmed.contains(
                "<html",
                ignoreCase = true
            ) -> "xml"

            trimmed.contains("<html", ignoreCase = true) -> "html"
            else -> "txt"
        }
    }
}
