package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeeplinkTabUiModel {
    val id: String

    data object Freeform : DeeplinkTabUiModel {
        override val id = "freeform"
    }

    data object Variables : DeeplinkTabUiModel {
        override val id = "variables"
    }

    // history entries and declared deeplinks don't share their ids
    data class Link(
        val deeplinkId: Long,
        val isHistory: Boolean,
        val title: String,
    ) : DeeplinkTabUiModel {
        override val id = "link-$isHistory-$deeplinkId"
    }
}

fun DeeplinkViewState.toTab() = DeeplinkTabUiModel.Link(
    deeplinkId = deeplinkId,
    isHistory = isHistory,
    title = displayName,
)
