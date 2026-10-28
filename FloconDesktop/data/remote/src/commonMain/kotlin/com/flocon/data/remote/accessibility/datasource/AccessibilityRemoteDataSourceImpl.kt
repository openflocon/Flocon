package com.flocon.data.remote.accessibility.datasource

import com.flocon.data.remote.accessibility.model.AccessibilityFocusedNodeDataModel
import com.flocon.data.remote.accessibility.model.AccessibilityItemDataModel
import com.flocon.data.remote.accessibility.model.AccessibilityStateDataModel
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityRemoteDataSource
import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class AccessibilityRemoteDataSourceImpl(
    private val json: Json,
) : AccessibilityRemoteDataSource {

    override fun getEvents(message: FloconIncomingMessageDomainModel): List<AccessibilityEventDomainModel> = try {
        json.decodeFromString<List<AccessibilityItemDataModel>>(message.body).map { it.toDomain() }
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }

    override fun getState(message: FloconIncomingMessageDomainModel): AccessibilityStateDomainModel? = try {
        json.decodeFromString<AccessibilityStateDataModel>(message.body).toDomain()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override fun getFocusedNode(message: FloconIncomingMessageDomainModel): AccessibilityFocusedNodeDomainModel? = try {
        json.decodeFromString<AccessibilityFocusedNodeDataModel>(message.body).toDomain()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

private fun AccessibilityItemDataModel.toDomain() = AccessibilityEventDomainModel(
    id = id,
    type = type,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras.orEmpty(),
    createdAt = createdAt,
)

private fun AccessibilityStateDataModel.toDomain() = AccessibilityStateDomainModel(
    supported = supported,
    enabled = enabled,
    touchExploration = touchExploration,
    services = services.orEmpty(),
)

private fun AccessibilityFocusedNodeDataModel.toDomain() = AccessibilityFocusedNodeDomainModel(
    found = found,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras.orEmpty(),
    index = index,
    count = count,
    hasNext = hasNext,
    hasPrevious = hasPrevious,
)
