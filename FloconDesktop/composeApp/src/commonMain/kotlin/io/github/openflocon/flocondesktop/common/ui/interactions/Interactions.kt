package io.github.openflocon.domain.common.ui.interactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Modifier.hover(isHover: (Boolean) -> Unit): Modifier
