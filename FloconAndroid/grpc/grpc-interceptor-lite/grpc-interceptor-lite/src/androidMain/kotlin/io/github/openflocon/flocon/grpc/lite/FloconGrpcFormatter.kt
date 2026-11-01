package io.github.openflocon.flocon.grpc.lite

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.openflocon.flocon.grpc.FloconGrpcBaseFormatter

class FloconGrpcFormatter(
    private val shouldExcludeField: (name: String) -> Boolean = defaultFieldExcluder,
) : FloconGrpcBaseFormatter {

    private val gson = buildGsonInstance(defaultFieldExcluder)

    override fun <T> format(message: T): String =
        gson.toJson(message)

    private fun buildGsonInstance(
        excluder: (name: String) -> Boolean,
    ): Gson {
        return GsonBuilder().setPrettyPrinting()
            .setFieldNamingStrategy {
                it.name.removeSuffix("_")
            }
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes): Boolean {
                    return excluder(f.name)
                }

                override fun shouldSkipClass(clazz: Class<*>): Boolean {
                    return false
                }
            }).create()
    }

    companion object {
        val excluded = setOf(
            "unknownFields",
            "memoizedHashCode",
            "bitField",
            "memoizedSerializedSize",
            "bytes",
        )

        val defaultFieldExcluder: (name: String) -> Boolean = { name ->
            var isExcluded = false
            excluded.forEach { toExclude ->
                if (name.startsWith(prefix = toExclude)) {
                    isExcluded = true
                }
            }
            isExcluded
        }
    }
}