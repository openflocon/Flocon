package io.github.openflocon.flocon.myapplication.multi.ui

/**
 * The platforms the sample runs on. Declared on each [SampleScreen] and [SampleFeature] to display
 * where it is available, the entry point of each platform sets [SampleFeatures.platform].
 */
enum class SamplePlatform(val label: String, val displayName: String) {
    Android("Android", "Android"),
    Ios("iOS", "iOS"),
    Desktop("Desktop", "Desktop (JVM)"),
    Web("Web", "Web (Wasm)"),
    ;

    companion object {
        val All: Set<SamplePlatform> = entries.toSet()
        val AndroidOnly: Set<SamplePlatform> = setOf(Android)
        val NotWeb: Set<SamplePlatform> = All - Web
    }
}
