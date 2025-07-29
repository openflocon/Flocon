package com.florent37.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

val isInPreview: Boolean
    @Composable
    get() {
        return LocalInspectionMode.current
    }
