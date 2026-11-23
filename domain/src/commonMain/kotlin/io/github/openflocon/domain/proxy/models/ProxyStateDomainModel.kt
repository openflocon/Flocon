package io.github.openflocon.domain.proxy.models

/** State of the proxy process run by Flocon Desktop (see `proxy/`). */
sealed interface ProxyStateDomainModel {
    data object Stopped : ProxyStateDomainModel
    data object Starting : ProxyStateDomainModel

    /** [ips] are the addresses of the computer the phone can reach, best one first. */
    data class Running(
        val proxyPort: Int,
        val caPort: Int,
        val ips: List<String>,
    ) : ProxyStateDomainModel

    data class Failed(val message: String) : ProxyStateDomainModel

    /** No Node.js on the computer: the standalone proxy is being downloaded. [progress] is 0..1, null while unknown. */
    data class Downloading(val progress: Float?) : ProxyStateDomainModel
}
