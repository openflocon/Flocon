package io.github.openflocon.domain.versions.model

/**
 * The SDK family a client app embeds. Each known one is released from its own GitHub repository,
 * so the latest version to compare a client with depends on its SDK.
 */
sealed class FloconSdk(
    /** The name sent by the SDK in the bonjour message. */
    val sdkName: String,
    /** The GitHub repository the SDK is released from, `owner/name`. Null when we do not know where it is released. */
    val repository: String?,
) {
    data object Kotlin : FloconSdk(sdkName = "kotlin", repository = "openflocon/Flocon")
    data object Swift : FloconSdk(sdkName = "swift", repository = "openflocon/FloconSwift")
    data object Flutter : FloconSdk(sdkName = "flutter", repository = "openflocon/FloconFlutter")

    /** An SDK we do not know: nothing can be compared with it. */
    data class Other(val name: String) : FloconSdk(sdkName = name, repository = null)

    /** Where the release notes of the latest version are, null when the repository is unknown. */
    val latestReleaseLink: String? get() = repository?.let { "https://github.com/$it/releases/latest" }

    companion object {
        /** @param sdkName the name sent in the bonjour message. Null when it is missing. */
        fun fromName(sdkName: String?): FloconSdk? {
            if (sdkName.isNullOrBlank()) return null
            return listOf(Kotlin, Swift, Flutter).firstOrNull { it.sdkName.equals(sdkName, ignoreCase = true) }
                ?: Other(sdkName)
        }
    }
}
