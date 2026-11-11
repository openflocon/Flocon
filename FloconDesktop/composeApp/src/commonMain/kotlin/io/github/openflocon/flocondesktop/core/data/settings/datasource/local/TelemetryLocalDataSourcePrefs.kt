package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import io.github.openflocon.data.core.telemetry.datasource.TelemetryLocalDataSource
import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel

internal class TelemetryLocalDataSourcePrefs : TelemetryLocalDataSource {

    private val settings = createSettings()

    override fun getLevel(): TelemetryLevelDomainModel? = settings.getStringOrNull(LEVEL)
        ?.let { name -> TelemetryLevelDomainModel.entries.firstOrNull { it.name == name } }

    override fun setLevel(level: TelemetryLevelDomainModel) {
        settings.putString(LEVEL, level.name)
    }

    override fun getInstallationId(): String? = settings.getStringOrNull(INSTALLATION_ID)

    override fun setInstallationId(id: String?) {
        if (id == null) settings.remove(INSTALLATION_ID) else settings.putString(INSTALLATION_ID, id)
    }

    private companion object {
        const val LEVEL = "telemetry_level"
        const val INSTALLATION_ID = "telemetry_installation_id"
    }
}
