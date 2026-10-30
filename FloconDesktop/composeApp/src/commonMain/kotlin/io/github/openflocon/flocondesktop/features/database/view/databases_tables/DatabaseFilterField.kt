package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_filter_placeholder
import flocondesktop.composeapp.generated.resources.ic_db_search
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseFilterField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconTextFieldWithoutM3(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(FloconTheme.sizes.controlHeight)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium),
        placeholder = defaultPlaceHolder(
            stringResource(Res.string.database_filter_placeholder),
            color = FloconTheme.colors.inkFaint,
        ),
        leadingComponent = {
            Image(
                painter = painterResource(Res.drawable.ic_db_search),
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colors.inkFaint),
                contentDescription = null,
            )
        },
        contentPadding = PaddingValues(horizontal = FloconTheme.spacing.small),
        containerColor = FloconTheme.colors.bg000.copy(alpha = 0.3f),
    )
}
