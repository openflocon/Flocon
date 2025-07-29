package com.florent37.flocondesktop.common

expect fun findAdbPath(): String?

expect fun executeAdbCommand(adbPath: String, command: String): Either<Throwable, String>
