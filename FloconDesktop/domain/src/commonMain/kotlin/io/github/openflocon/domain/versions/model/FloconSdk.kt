package io.github.openflocon.domain.versions.model

/**
 * The SDK families a client app can embed. Each one is released from its own GitHub repository,
 * so the latest version to compare a client with depends on its SDK.
 */
enum class FloconSdk(
    /** The name sent by the SDK in the bonjour message. */
    val sdkName: String,
    /** The GitHub repository the SDK is released from, `owner/name`. */
    val repository: String,
) {
    KOTLIN(sdkName = "kotlin", repository = "openflocon/Flocon"),
    SWIFT(sdkName = "swift", repository = "openflocon/FloconSwift"),
    FLUTTER(sdkName = "flutter", repository = "openflocon/FloconFlutter"),
    ;

    /** Where the release notes of the latest version are. */
    val latestReleaseLink: String get() = "https://github.com/$repository/releases/latest"

    companion object {
        /** @param sdkName the name sent in the bonjour message. Null when it is missing or not one we know. */
        fun fromName(sdkName: String?): FloconSdk? = entries.firstOrNull { it.sdkName.equals(sdkName, ignoreCase = true) }
    }
}
