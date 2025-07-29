package com.florent37.flocondesktop.common

expect fun findAdbPath(): String?

expect fun executeSystemCommand(command: String): Either<Throwable, String>
