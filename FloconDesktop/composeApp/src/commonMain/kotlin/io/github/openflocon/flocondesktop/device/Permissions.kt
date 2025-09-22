package io.github.openflocon.flocondesktop.device

enum class Permissions(
    val label: String,
    val value: String,
) {
    BACKGROUND_LOCATION(
        label = "Background location",
        value = "android.permission.ACCESS_BACKGROUND_LOCATION"
    ),
    ACCESS_COARSE_LOCATION(
        label = "Approximate location",
        value = "android.permission.ACCESS_COARSE_LOCATION"
    ),
    ACCESS_FINE_LOCATION(
        label = "Precise location",
        value = "android.permission.ACCESS_FINE_LOCATION"
    )
}
