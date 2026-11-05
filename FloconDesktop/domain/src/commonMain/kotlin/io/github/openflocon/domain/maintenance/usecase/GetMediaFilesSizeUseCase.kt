package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository

class GetMediaFilesSizeUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(): Long = repository.getSizeBytes()
}
