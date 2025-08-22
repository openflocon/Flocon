package io.github.openflocon.flocon.grpc

interface FloconGrpcFormatter {

    fun <T> format(message: T): String
}