package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloconTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    content: @Composable ColumnScope.() -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        content = content,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        modifier = modifier
    )
}

@Composable
fun FloconTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
) {
    FloconTab(
        selected = selected,
        onClick = onClick,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(4.dp)
        )
    }
}
