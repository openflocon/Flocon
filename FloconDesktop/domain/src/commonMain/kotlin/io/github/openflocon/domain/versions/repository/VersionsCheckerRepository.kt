package io.github.openflocon.domain.versions.repository

import io.github.openflocon.domain.common.Either

interface VersionsCheckerRepository {
    suspend fun checkIsLastVersion() : Either<Throwable, String>
}
