package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preferences_sidebar_title
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import org.jetbrains.compose.resources.stringResource

/** The preference files of the device, listed on the left of the screen. */
@Composable
internal fun SharedPreferencesSidebar(
    state: SharedPrefsStateUiModel,
    filter: String,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            stringResource(Res.string.preferences_sidebar_title),
            color = FloconTheme.colors.inkFaint,
            style = FloconTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
            ),
            modifier = Modifier
                .padding(all = FloconTheme.spacing.small)
                .padding(horizontal = FloconTheme.spacing.extraSmall),
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FloconTheme.spacing.small)
                .padding(bottom = FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        ) {
            when (state) {
                SharedPrefsStateUiModel.Empty -> Unit

                SharedPrefsStateUiModel.Loading -> FloconLinearProgressIndicator(Modifier.fillMaxWidth())

                is SharedPrefsStateUiModel.WithContent -> {
                    val preferences = remember(state.preferences, filter) {
                        state.preferences.filter {
                            it.name.contains(filter, ignoreCase = true) ||
                                it.type?.contains(filter, ignoreCase = true) == true
                        }
                    }
                    preferences.forEach { preference ->
                        SharedPreferencesSidebarItem(
                            preference = preference,
                            isSelected = preference.id == state.selected.id,
                            onClick = { onSharedPrefSelected(preference) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedPreferencesSidebarItem(
    preference: DeviceSharedPrefUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(FloconTheme.sizes.rowHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) FloconTheme.colors.ink.copy(alpha = 0.05f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        PreferenceTypeIcon(preference.typeKind)
        Text(
            preference.name,
            color = FloconTheme.colors.ink,
            style = FloconTheme.typography.listItem.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        preference.type?.let {
            Text(
                it,
                color = FloconTheme.colors.inkFaint,
                style = FloconTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
