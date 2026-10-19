package io.github.openflocon.domain.versions

import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.DismissClientVersionUseCase
import io.github.openflocon.domain.versions.usecase.DismissDesktopVersionUseCase
import io.github.openflocon.domain.versions.usecase.GetReleaseNoteUseCase
import io.github.openflocon.domain.versions.usecase.GetReleaseNotesUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveLastAvailableFloconVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveUpdateAvailableUseCase
import io.github.openflocon.domain.versions.usecase.ShouldShowReleaseNotesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val versionModule = module {
    factoryOf(::CheckIsDesktopOnLastVersionUseCase)
    factoryOf(::DismissDesktopVersionUseCase)
    factoryOf(::DismissClientVersionUseCase)
    factoryOf(::ObserveLastAvailableFloconVersionUseCase)
    factoryOf(::ObserveIsClientOnLastVersionUseCase)
    factoryOf(::ObserveUpdateAvailableUseCase)
    factoryOf(::GetReleaseNotesUseCase)
    factoryOf(::GetReleaseNoteUseCase)
    factoryOf(::ShouldShowReleaseNotesUseCase)
}
