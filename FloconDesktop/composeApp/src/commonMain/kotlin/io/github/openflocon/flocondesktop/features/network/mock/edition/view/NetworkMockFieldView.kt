package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

// TODO Remove
@Composable
fun NetworkMockFieldView(
    label: String,
    placeHolder: String?,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    suffix: @Composable (() -> Unit)? = null,
) {
    FloconTextField(
        value = value,
        onValueChange = onValueChange,
        minLines = minLines,
        maxLines = maxLines,
        label = defaultLabel(label),
        placeholder = defaultPlaceHolder(placeHolder?.takeIf(String::isNotEmpty)),
        suffix = suffix,
        containerColor = FloconTheme.colorPalette.panel,
        modifier = modifier
    )
}
