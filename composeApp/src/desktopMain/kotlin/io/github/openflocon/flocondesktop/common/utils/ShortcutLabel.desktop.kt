package io.github.openflocon.flocondesktop.common.utils

actual val primaryModifierLabel: String =
    if (System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)) "⌘" else "Ctrl"
