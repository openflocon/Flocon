package com.florent37.flocondesktop.features.grpc.ui.model

sealed interface OnGrpcItemUserAction {
    data class OnClicked(
        val item: GrpcItemViewState,
    ) : OnGrpcItemUserAction

    data class CopyUrl(
        val item: GrpcItemViewState,
    ) : OnGrpcItemUserAction

    data class CopyMethod(
        val item: GrpcItemViewState,
    ) : OnGrpcItemUserAction

    data class Remove(
        val item: GrpcItemViewState,
    ) : OnGrpcItemUserAction

    data class RemoveLinesAbove(
        val item: GrpcItemViewState,
    ) : OnGrpcItemUserAction
}
