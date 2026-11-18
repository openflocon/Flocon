package io.github.openflocon.flocondesktop.iossimulator

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.flocondesktop.common.localOpenUrlInIosSimulator
import io.github.openflocon.flocondesktop.common.localStartIosSimulatorRecording
import io.github.openflocon.flocondesktop.common.localStopIosSimulatorRecording
import io.github.openflocon.flocondesktop.common.localTakeIosSimulatorScreenshot
import kotlinx.coroutines.withContext

class IosSimulatorRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
) : IosSimulatorRepository {

    override suspend fun openUrl(url: String): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        localOpenUrlInIosSimulator(url = url)
    }

    override suspend fun takeScreenshot(filePath: String): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        localTakeIosSimulatorScreenshot(filePath = filePath)
    }

    override suspend fun startRecording(filePath: String): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        localStartIosSimulatorRecording(filePath = filePath)
    }

    override suspend fun stopRecording(filePath: String): Either<Throwable, String> = withContext(dispatcherProvider.data) {
        localStopIosSimulatorRecording(filePath = filePath)
    }
}
