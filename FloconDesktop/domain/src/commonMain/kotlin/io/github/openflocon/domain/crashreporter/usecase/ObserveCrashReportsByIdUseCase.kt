package io.github.openflocon.domain.crashreporter.usecase

import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import kotlinx.coroutines.flow.Flow

class ObserveCrashReportsByIdUseCase(
    private val repository: CrashReporterRepository,
) {
    operator fun invoke(id: String): Flow<CrashReportDomainModel?> {
        return repository.observeCrashReportById(id)
    }
}