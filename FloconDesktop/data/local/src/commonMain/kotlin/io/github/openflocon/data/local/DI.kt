package io.github.openflocon.data.local

import io.github.openflocon.data.local.adb.adbModule
import io.github.openflocon.data.local.analytics.analyticsModule
import io.github.openflocon.data.local.crashreporter.crashReporterLocalModule
import io.github.openflocon.data.local.dashboard.dashboardModule
import io.github.openflocon.data.local.database.databaseModule
import io.github.openflocon.data.local.deeplink.deeplinkModule
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.deeplink.models.DeeplinkVariableEntity
import io.github.openflocon.data.local.device.deviceModule
import io.github.openflocon.data.local.files.filesModule
import io.github.openflocon.data.local.images.imagesModule
import io.github.openflocon.data.local.network.networkModule
import io.github.openflocon.data.local.sharedpreference.sharedPreferenceModule
import io.github.openflocon.data.local.table.tableModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val JSON = named("local_json")

val dataLocalModule = module {
    single(JSON) {
        Json {
            ignoreUnknownKeys = true

            serializersModule = SerializersModule {
                polymorphic(DeeplinkEntity.Parameter::class) {
                    subclass(DeeplinkEntity.Parameter.AutoComplete::class)
                    subclass(DeeplinkEntity.Parameter.Variable::class)

                    defaultDeserializer { DeeplinkEntity.Parameter.AutoComplete.serializer() }
                }
                polymorphic(DeeplinkVariableEntity.Mode::class) {
                    subclass(DeeplinkVariableEntity.Mode.Input::class)
                    subclass(DeeplinkVariableEntity.Mode.AutoComplete::class)

                    defaultDeserializer { DeeplinkVariableEntity.Mode.Input.serializer() }
                }
            }
        }
    }
    includes(
        adbModule,
        analyticsModule,
        dashboardModule,
        databaseModule,
        deeplinkModule,
        deviceModule,
        filesModule,
        imagesModule,
        networkModule,
        sharedPreferenceModule,
        tableModule,
        crashReporterLocalModule,
    )
}
