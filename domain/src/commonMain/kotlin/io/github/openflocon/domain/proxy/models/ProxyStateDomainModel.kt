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

    /** The binary is not shipped in this build (uber jar, or not built in dev, see `proxy/README.md`). */
    data object BinaryNotFound : ProxyStateDomainModel
}
