package io.github.openflocon.flocon.grpc

interface FloconGrpcBaseFormatter {

    fun <T> format(message: T): String
}