package io.github.openflocon.flocon.grpc

import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.util.JsonFormat

class GrpcFormatter : FloconGrpcFormatter {

    private val printer = JsonFormat.printer().alwaysPrintFieldsWithNoPresence()

    override fun <T> format(message: T): String =
        (message as? MessageOrBuilder)?.let { printer.print(it) } ?: ""
}