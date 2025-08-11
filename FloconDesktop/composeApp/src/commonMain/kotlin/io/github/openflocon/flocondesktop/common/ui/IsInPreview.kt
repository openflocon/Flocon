package io.github.openflocon.domain.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

val isInPreview: Boolean
    @Composable
    get() {
        return LocalInspectionMode.current
    }
