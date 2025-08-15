package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FloconTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        content = content,
        modifier = modifier
    )
}

@Composable
fun FloconTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloconTab(
        selected = selected,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}
