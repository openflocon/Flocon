package io.github.openflocon.flocon.accessibility

/** The TalkBack builds we know about: the Google one, and the one of Samsung devices. */
internal val TalkBackServices = listOf(
    "com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService",
    "com.samsung.android.accessibility.talkback/com.samsung.android.marvin.talkback.TalkBackService",
)

/** The packages of [TalkBackServices], for the manifest `queries` too. */
internal val TalkBackPackages = TalkBackServices.map { it.substringBefore('/') }

/** The first known TalkBack service whose package passes [isInstalled]; null when there is none. */
internal fun findTalkBackService(isInstalled: (packageName: String) -> Boolean): String? =
    TalkBackServices.firstOrNull { isInstalled(it.substringBefore('/')) }

/** The value of `enabled_accessibility_services` with [service] added, [current] being what the device holds. */
internal fun withService(current: String?, service: String): String {
    val enabled = current.enabledServices()
    return (if (service in enabled) enabled else enabled + service).joinToString(":")
}

/** The value of `enabled_accessibility_services` without any TalkBack, the other services are kept. */
internal fun withoutTalkBack(current: String?): String =
    current.enabledServices().filter { it.substringBefore('/') !in TalkBackPackages }.joinToString(":")

private fun String?.enabledServices(): List<String> =
    orEmpty().split(':').map { it.trim() }.filter { it.isNotEmpty() && it != "null" }
