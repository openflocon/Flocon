package io.github.openflocon.domain.adb.repository

import io.github.openflocon.domain.common.Either

interface AdbRepository {

    fun executeAdbCommand(adbPath: String, command: String): Either<Throwable, String>

    fun findAdbPath(): String?

}
