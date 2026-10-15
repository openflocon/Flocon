package io.github.openflocon.domain.device.usecase

import java.io.File
import java.nio.file.Paths

class GetFloconDocumentPathUseCase() {
    operator fun invoke() : File {
        return Paths.get(System.getProperty("user.home"), "Documents", "Flocon").toFile()
    }
}