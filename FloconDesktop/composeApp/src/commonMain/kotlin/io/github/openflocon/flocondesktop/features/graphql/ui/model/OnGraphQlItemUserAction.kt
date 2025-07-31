package io.github.openflocon.flocondesktop.features.graphql.ui.model

sealed interface OnGraphQlItemUserAction {
    data class OnClicked(
        val item: GraphQlItemViewState,
    ) : OnGraphQlItemUserAction

    data class CopyUrl(
        val item: GraphQlItemViewState,
    ) : OnGraphQlItemUserAction

    data class Remove(
        val item: GraphQlItemViewState,
    ) : OnGraphQlItemUserAction

    data class RemoveLinesAbove(
        val item: GraphQlItemViewState,
    ) : OnGraphQlItemUserAction
}
