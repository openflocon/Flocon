package io.github.openflocon.domain.adb

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.models.ProcessId

class StopAdbProcessUseCase(
    private val adbRepository: AdbRepository,
) {
    operator fun invoke(
        processId: ProcessId,
    ) {
        return adbRepository.stopAdbProcess(
            processId = processId
        )
    }
}
