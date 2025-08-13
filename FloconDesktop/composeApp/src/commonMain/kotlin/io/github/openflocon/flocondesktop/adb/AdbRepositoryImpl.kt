package io.github.openflocon.flocondesktop.adb

import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.flocondesktop.common.localExecuteAdbCommand
import io.github.openflocon.flocondesktop.common.localFindAdbPath

// TODO Move
class AdbRepositoryImpl : AdbRepository {

    override fun executeAdbCommand(
        adbPath: String,
        command: String,
    ): Either<Throwable, String> = localExecuteAdbCommand(adbPath = adbPath, command = command)

    override fun findAdbPath(): String? = localFindAdbPath()
}
