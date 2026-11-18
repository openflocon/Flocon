package com.flocon.data.remote.deeplink.models

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.deeplink.models.Deeplinks
import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinksReceivedDataModel(
    val deeplinks: List<DeeplinkReceivedDataModel>,
    val variables: List<DeeplinkVariableReceivedDataModel>
)

internal fun DeeplinksReceivedDataModel.toDomain(): Deeplinks = Deeplinks(
    deeplinks = deeplinks.map {
        DeeplinkDomainModel(
            label = it.label,
            link = it.link,
            description = it.description,
            parameters = it.parameters.map(DeeplinkReceivedDataModel.Parameter::toDomain),
            id = 0, // will be created by the DB later
        )
    },
    variables = variables.map {
        DeeplinkVariableDomainModel(
            name = it.name,
            mode = when (val mode = it.mode) {
                is DeeplinkVariableReceivedDataModel.Mode.AutoComplete -> DeeplinkVariableDomainModel.Mode.AutoComplete(mode.suggestions)
                DeeplinkVariableReceivedDataModel.Mode.Input -> DeeplinkVariableDomainModel.Mode.Input
            },
            description = it.description
        )
    }
)

private fun DeeplinkReceivedDataModel.Parameter.toDomain() = when (this) {
    is DeeplinkReceivedDataModel.Parameter.AutoComplete -> DeeplinkDomainModel.Parameter.AutoComplete(
        name = name,
        autoComplete = autoComplete
    )

    is DeeplinkReceivedDataModel.Parameter.Variable -> DeeplinkDomainModel.Parameter.Variable(
        name = name,
        variableName = variableName
    )
}
