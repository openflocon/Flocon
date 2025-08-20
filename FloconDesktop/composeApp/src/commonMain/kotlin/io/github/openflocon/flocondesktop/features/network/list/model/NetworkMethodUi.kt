package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.graphql
import org.jetbrains.compose.resources.DrawableResource

@Immutable
sealed interface NetworkMethodUi {
    val text: String
    val icon: DrawableResource?

    sealed interface Http : NetworkMethodUi {

        data object GET : Http {
            override val text: String = "GET"
            override val icon = null
        }

        data object POST : Http {
            override val text: String = "POST"
            override val icon = null
        }

        data object PUT : Http {
            override val text: String = "PUT"
            override val icon = null
        }

        data object DELETE : Http {
            override val text: String = "DELETE"
            override val icon = null
        }
    }

    sealed interface GraphQl : NetworkMethodUi {
        data object QUERY : GraphQl {
            override val text: String = "QUERY"
            override val icon = Res.drawable.graphql
        }

        data object MUTATION : GraphQl {
            override val text: String = "MUTATION"
            override val icon = Res.drawable.graphql
        }
    }

    data object Grpc : NetworkMethodUi {
        override val text = "gRPC"
        override val icon = null
    }

    data class OTHER(
        override val text: String,
        override val icon: DrawableResource?,
    ) : NetworkMethodUi {
        companion object {
            val EMPTY = OTHER(text = "", icon = null)
        }
    }

    companion object {
        fun all() = listOf(
            Http.GET,
            Http.POST,
            Http.PUT,
            Http.DELETE,
            GraphQl.QUERY,
            GraphQl.MUTATION,
            Grpc,
            OTHER(text = "Other", icon = null),
        )
    }
}
