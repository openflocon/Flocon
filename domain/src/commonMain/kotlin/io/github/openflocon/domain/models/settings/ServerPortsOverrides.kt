package io.github.openflocon.domain.models.settings

/**
 * Ports forced for the current session only (launch options), never persisted.
 * A null port is not overridden.
 */
data class ServerPortsOverrides(
    val websocketPort: Int? = null,
    val httpPort: Int? = null,
) {
    val isEmpty: Boolean
        get() = websocketPort == null && httpPort == null

    /** The overrides win over the [saved] ports. */
    fun applyTo(saved: ServerPortsDomainModel) = ServerPortsDomainModel(
        websocketPort = websocketPort ?: saved.websocketPort,
        httpPort = httpPort ?: saved.httpPort,
    )

    companion object {
        val None = ServerPortsOverrides()
    }
}
