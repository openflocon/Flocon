package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either

expect fun localFindAdbPath(): String?

expect fun localExecuteAdbCommand(adbPath: String, command: String): Either<Throwable, String>
