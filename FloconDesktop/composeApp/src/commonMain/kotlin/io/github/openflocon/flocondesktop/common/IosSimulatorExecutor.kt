package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either

expect fun localOpenUrlInIosSimulator(url: String): Either<Throwable, String>
