package io.github.openflocon.data.local.timeline.mapper

import io.github.openflocon.data.local.timeline.datasource.KIND_ADB
import io.github.openflocon.data.local.timeline.datasource.KIND_ANALYTICS
import io.github.openflocon.data.local.timeline.datasource.KIND_APP_START
import io.github.openflocon.data.local.timeline.datasource.KIND_CRASH
import io.github.openflocon.data.local.timeline.datasource.KIND_DATABASE
import io.github.openflocon.data.local.timeline.datasource.KIND_DEEPLINK
import io.github.openflocon.data.local.timeline.datasource.KIND_NETWORK
import io.github.openflocon.data.local.timeline.datasource.KIND_TABLE
import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource

internal fun TimelineRowEntity.toDomain(): TimelineEventDomainModel = TimelineEventDomainModel(
    id = id,
    source = when (kind) {
        KIND_NETWORK -> TimelineSource.Network
        KIND_DATABASE -> TimelineSource.Database
        KIND_ANALYTICS -> TimelineSource.Analytics
        KIND_CRASH -> TimelineSource.Crash
        KIND_TABLE -> TimelineSource.Table
        KIND_DEEPLINK -> TimelineSource.Deeplink
        KIND_ADB -> TimelineSource.Adb
        KIND_APP_START -> TimelineSource.AppStart
        else -> error("unknown timeline kind: $kind")
    },
    timestamp = ts,
    title = title,
    subtitle = subtitle,
    appInstance = appInstance,
)
