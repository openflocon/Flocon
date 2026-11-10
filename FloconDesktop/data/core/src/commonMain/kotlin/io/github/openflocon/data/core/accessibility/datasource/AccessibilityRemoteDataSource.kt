package io.github.openflocon.data.core.accessibility.datasource

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface AccessibilityRemoteDataSource {
    fun getEvents(message: FloconIncomingMessageDomainModel): List<AccessibilityEventDomainModel>
    fun getState(message: FloconIncomingMessageDomainModel): AccessibilityStateDomainModel?
    fun getFocusedNode(message: FloconIncomingMessageDomainModel): AccessibilityFocusedNodeDomainModel?
}
