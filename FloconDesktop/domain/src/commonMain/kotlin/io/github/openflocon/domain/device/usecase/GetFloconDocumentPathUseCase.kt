package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository
import java.io.File
import java.nio.file.Paths

class GetFloconDocumentPathUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): File {
        val configuredPath = settingsRepository.getFloconDocumentsPath()
        return if (!configuredPath.isNullOrBlank()) {
            File(configuredPath)
        } else {
            defaultPath()
        }
    }

    companion object {
        fun defaultPath(): File =
            Paths.get(System.getProperty("user.home"), "Documents", "Flocon").toFile()
    }
}