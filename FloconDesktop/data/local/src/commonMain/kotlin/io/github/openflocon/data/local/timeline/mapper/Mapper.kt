package io.github.openflocon.data.local.timeline.mapper

import io.github.openflocon.data.local.timeline.datasource.KIND_ACCESSIBILITY
import io.github.openflocon.data.local.timeline.datasource.KIND_ADB
import io.github.openflocon.data.local.timeline.datasource.KIND_ANALYTICS
import io.github.openflocon.data.local.timeline.datasource.KIND_APP_START
import io.github.openflocon.data.local.timeline.datasource.KIND_CRASH
import io.github.openflocon.data.local.timeline.datasource.KIND_DATABASE
import io.github.openflocon.data.local.timeline.datasource.KIND_DEEPLINK
import io.github.openflocon.data.local.timeline.datasource.KIND_NETWORK
import io.github.openflocon.data.local.timeline.datasource.KIND_NETWORK_RESPONSE
import io.github.openflocon.data.local.timeline.datasource.KIND_TABLE
import io.github.openflocon.data.local.timeline.datasource.KIND_WEBVIEW
import io.github.openflocon.data.local.timeline.models.TimelineFacetCountEntity
import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import io.github.openflocon.data.local.timeline.models.TimelineSessionCountEntity
import io.github.openflocon.data.local.timeline.models.TimelineSummaryRowEntity
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSourceSummaryDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel

private fun String.toTimelineSource(): TimelineSource = when (this) {
    KIND_NETWORK, KIND_NETWORK_RESPONSE -> TimelineSource.Network
    KIND_DATABASE -> TimelineSource.Database
    KIND_ANALYTICS -> TimelineSource.Analytics
    KIND_ACCESSIBILITY -> TimelineSource.Accessibility
    KIND_WEBVIEW -> TimelineSource.WebView
    KIND_CRASH -> TimelineSource.Crash
    KIND_TABLE -> TimelineSource.Table
    KIND_DEEPLINK -> TimelineSource.Deeplink
    KIND_ADB -> TimelineSource.Adb
    KIND_APP_START -> TimelineSource.AppStart
    else -> error("unknown timeline kind: $this")
}

internal fun TimelineRowEntity.toDomain(): TimelineEventDomainModel = TimelineEventDomainModel(
    id = id,
    source = kind.toTimelineSource(),
    timestamp = ts,
    title = title,
    subtitle = subtitle,
    appInstance = appInstance,
    method = method,
    statusCode = statusCode,
    durationMs = durationMs,
    phase = if (kind == KIND_NETWORK_RESPONSE) TimelinePhase.Response else TimelinePhase.Request,
    sessionStart = sessionStart,
    isImage = isImage,
)

internal fun toSummaryDomain(
    summaryRows: List<TimelineSummaryRowEntity>,
    sessionRows: List<TimelineSessionCountEntity>,
    facetRows: List<TimelineFacetCountEntity> = emptyList(),
): TimelineSummaryDomainModel = TimelineSummaryDomainModel(
    sources = summaryRows.associate { row ->
        row.kind.toTimelineSource() to TimelineSourceSummaryDomainModel(
            count = row.count,
            httpErrorCount = row.errors,
            firstTimestamp = row.firstTs,
            lastTimestamp = row.lastTs,
        )
    },
    sessionEventCounts = sessionRows.associate { it.sessionStart to it.count },
    facetCounts = facetRows
        .mapNotNull { row -> TimelineFacet.entries.firstOrNull { it.name == row.facet }?.let { it to row } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, rows) -> rows.associate { it.value to it.count } },
)
