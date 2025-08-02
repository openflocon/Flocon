package io.github.openflocon.flocondesktop.features.network.ui.model

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
    }

    data class OTHER(
        override val text: String,
        override val icon: DrawableResource?,
    ) : NetworkMethodUi
}
