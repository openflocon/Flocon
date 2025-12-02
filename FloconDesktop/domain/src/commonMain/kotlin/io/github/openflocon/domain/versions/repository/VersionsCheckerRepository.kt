package io.github.openflocon.domain.versions.repository

import io.github.openflocon.domain.common.Either
import kotlinx.coroutines.flow.Flow

interface VersionsCheckerRepository {
    val lastVersion: Flow<String?>
    suspend fun checkIsLastVersion(): Either<Throwable, String>
}
