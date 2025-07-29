package com.florent37.flocondesktop.features.grpc.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class GrpcItemColumnWidths(
    val requestTimeFormatted: Dp = 90.dp,
    val url: Float = 1f, // weight
    val method: Float = 2f, // weight
    val status: Dp = 80.dp,
    val durationFormatted: Dp = 65.dp,
)
