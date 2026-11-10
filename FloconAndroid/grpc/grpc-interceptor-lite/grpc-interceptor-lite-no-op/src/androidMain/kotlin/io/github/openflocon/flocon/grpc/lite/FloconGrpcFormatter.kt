package io.github.openflocon.flocon.grpc.lite

import io.github.openflocon.flocon.grpc.FloconGrpcBaseFormatter

class FloconGrpcFormatter(
    @Suppress("unused") private val shouldExcludeField: (name: String) -> Boolean = defaultFieldExcluder,
) : FloconGrpcBaseFormatter {

    override fun <T> format(message: T): String = ""

    companion object {
        val excluded = setOf(
            "unknownFields",
            "memoizedHashCode",
            "bitField",
            "memoizedSerializedSize",
            "bytes",
        )

        val defaultFieldExcluder: (name: String) -> Boolean = { name ->
            excluded.any { name.startsWith(prefix = it) }
        }
    }
}
