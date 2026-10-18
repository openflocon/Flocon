package io.github.openflocon.flocondesktop.iossimulator

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.flocondesktop.common.localOpenUrlInIosSimulator
import kotlinx.coroutines.withContext

class IosSimulatorRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
) : IosSimulatorRepository {

    override suspend fun openUrl(url: String): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        localOpenUrlInIosSimulator(url = url)
    }
}
