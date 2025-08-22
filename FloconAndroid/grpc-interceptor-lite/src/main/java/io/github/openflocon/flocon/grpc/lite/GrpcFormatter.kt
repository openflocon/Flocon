package io.github.openflocon.flocon.grpc.lite

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.openflocon.flocon.grpc.FloconGrpcFormatter

class GrpcFormatter : FloconGrpcFormatter {

    private val excluded = setOf(
        "unknownFields",
        "memoizedHashCode",
        "bitField",
        "memoizedSerializedSize",
        "bytes",
    )

    private val defaultFieldExcluder: (name: String) -> Boolean = { name ->
        var isExcluded = false
        excluded.forEach { toExclude ->
            if (name.startsWith(prefix = toExclude)) {
                isExcluded = true
            }
        }
        isExcluded
    }

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
}