package io.github.openflocon.flocondesktop.features.crashreporter.model

import androidx.compose.runtime.Immutable

@Immutable
data class StackLine(
    val number: Int,
    val text: String,
    val kind: Kind,
) {
    enum class Kind { Header, App, Framework, Suppressed }
}

@Immutable
data class ParsedStackTrace(
    val lines: List<StackLine>,
) {
    /** The first frame that is in the code of the app, where the crash most likely comes from. */
    val firstAppFrame: StackLine? = lines.firstOrNull { it.kind == StackLine.Kind.App }
    val frameworkFramesCount: Int = lines.count { it.kind == StackLine.Kind.Framework }
    val suppressed: List<StackLine> = lines.filter { it.kind == StackLine.Kind.Suppressed }
}

private val frameworkPrefixes = listOf(
    "java.", "javax.", "jdk.", "sun.", "kotlin.", "kotlinx.",
    "android.", "androidx.", "com.android.", "com.google.android.", "dalvik.", "libcore.",
)

fun parseStackTrace(stackTrace: String): ParsedStackTrace {
    var inSuppressed = false
    val lines = stackTrace.lines()
        .filter { it.isNotBlank() }
        .mapIndexed { index, raw ->
            val text = raw.trim()
            val kind = when {
                text.startsWith("Suppressed:") -> {
                    inSuppressed = true
                    StackLine.Kind.Suppressed
                }

                text.startsWith("at ") -> {
                    inSuppressed = false
                    if (frameworkPrefixes.any { text.removePrefix("at ").startsWith(it) }) {
                        StackLine.Kind.Framework
                    } else {
                        StackLine.Kind.App
                    }
                }

                // a wrapped continuation of a suppressed exception
                inSuppressed && !text.startsWith("Caused by:") -> StackLine.Kind.Suppressed

                else -> {
                    inSuppressed = false
                    StackLine.Kind.Header
                }
            }
            StackLine(number = index + 1, text = text, kind = kind)
        }
    return ParsedStackTrace(lines)
}

/** "at a.b.Class.method(File.kt:12)" -> "Class.method" to "File.kt:12" */
fun StackLine.symbolAndLocation(): Pair<String, String> {
    val frame = text.removePrefix("at ")
    val location = frame.substringAfter('(', "").substringBeforeLast(')', "")
    val symbol = frame.substringBefore('(').split('.').takeLast(2).joinToString(".")
    return symbol to location
}
