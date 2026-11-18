package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.library.designsystem.theme.AppScale

fun AppScaleSetting.toAppScale(): AppScale = when (this) {
    AppScaleSetting.Compact -> AppScale.Compact
    AppScaleSetting.Default -> AppScale.Default
    AppScaleSetting.Comfortable -> AppScale.Comfortable
    AppScaleSetting.Large -> AppScale.Large
    AppScaleSetting.ExtraLarge -> AppScale.ExtraLarge
    AppScaleSetting.Maximum -> AppScale.Maximum
}

fun AppScale.toSetting(): AppScaleSetting = when (this) {
    AppScale.Compact -> AppScaleSetting.Compact
    AppScale.Default -> AppScaleSetting.Default
    AppScale.Comfortable -> AppScaleSetting.Comfortable
    AppScale.Large -> AppScaleSetting.Large
    AppScale.ExtraLarge -> AppScaleSetting.ExtraLarge
    AppScale.Maximum -> AppScaleSetting.Maximum
}
