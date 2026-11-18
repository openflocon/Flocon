package io.github.openflocon.domain.versions.usecase

import io.github.openflocon.domain.settings.repository.SettingsRepository

class ShouldShowReleaseNotesUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(current: String): Boolean {
        val lastSeen = settingsRepository.getLastSeenReleaseNotesVersion()
        if (lastSeen == current) return false

        settingsRepository.setLastSeenReleaseNotesVersion(current)
        // a missing value means a fresh install: nothing to announce
        return lastSeen != null
    }
}
