package io.github.openflocon.domain.maintenance.usecase

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.MediaFilesSize

class GetMediaFilesSizeUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(): MediaFilesSize = repository.getSizes()
}
