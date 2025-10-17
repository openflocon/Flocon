package io.github.openflocon.flocondesktop.common.utils

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import java.awt.Desktop
import java.io.File
import java.io.IOException

sealed interface OpenFileError {
    val message: String

    data object FileNotFound : OpenFileError {
        override val message: String = "File not found"
    }
    data object DesktopNotSupported : OpenFileError {
        override val message: String = "Desktop API is not supported"
    }
    data object OpenActionNotSupported : OpenFileError {
        override val message: String = "Desktop OPEN action is not supported"
    }
    data class IoError(val error: Throwable) : OpenFileError {
        override val message: String = "Error while opening file: ${error.message}"
    }
}

object OpenFile {

    fun isSupported(): Boolean {
        return Desktop.isDesktopSupported()
    }

    fun openFileOnDesktop(path: String): Either<OpenFileError, Unit> {
        val file = File(path)

        if (!file.exists()) {
            Logger.e("❌ File does not exist: ${file.absolutePath}")
            return OpenFileError.FileNotFound.failure()
        }

        if (!Desktop.isDesktopSupported()) {
            Logger.e("❌ Desktop API is not supported")
            return OpenFileError.DesktopNotSupported.failure()
        }

        val desktop = Desktop.getDesktop()

        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            Logger.e("❌ Desktop OPEN action is not supported")
            return OpenFileError.OpenActionNotSupported.failure()
        }

        return try {
            desktop.open(file)
            Logger.i("📂 File opened with default application: ${file.absolutePath}")
            Unit.success()
        } catch (e: IOException) {
            Logger.e("💥 Error while opening file: ${e.message}")
            OpenFileError.IoError(e).failure()
        }
    }
}
