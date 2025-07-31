package io.github.openflocon.flocondesktop.features.grpc.domain.model

data class GrpcResponseDomainModel(
    val timestamp: Long,
    val status: String,
    val headers: List<GrpcHeaderDomainModel>,
    val result: CallResult,
) {
    sealed interface CallResult {
        data class Success(
            val data: String,
        ) : CallResult

        data class Error(
            val cause: String,
        ) : CallResult
    }
}
