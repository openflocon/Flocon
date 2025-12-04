package io.github.openflocon.domain.crashreporter.usecase

import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository

class DeleteCrashReportUseCase(
    private val repository: CrashReporterRepository,
) {
    suspend operator fun invoke(id: String) {
        return repository.deleteCrashReport(id)
    }
}