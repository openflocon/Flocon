@file:OptIn(ExperimentalSerializationApi::class)

package io.github.openflocon.flocon.plugins.dashboard.mapper

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.ButtonCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.CheckBoxCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.FormCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.TextFieldCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.CheckBoxConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ElementConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.FormConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.LabelConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.MarkdownConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextFieldConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

internal fun DashboardConfig.toJson(
    registerCallback: (DashboardCallback) -> Unit,
): JsonObject {
    return buildJsonObject {
        put("dashboardId", id)
        putJsonArray("containers") {
            addAll(containers.map { container ->
                container.toJson(
                    dashboardId = id,
                    registerCallback = registerCallback,
                )
            })
        }
    }
}

//  {
//      "name" : "container name",
//      "containerType" : "SECTION",
//      "elements" : [ ... ],
//      "containerConfig" : ...
//  }
internal fun ContainerConfig.toJson(
    registerCallback: (DashboardCallback) -> Unit,
    dashboardId: String,
): JsonObject = buildJsonObject {
    put("name", name)
    putJsonArray("elements") {
        addAll(elements.map { element ->
            parseElementConfig(
                element = element,
                registerCallback = registerCallback,
                dashboardId = dashboardId
            )
        })
    }

    put(
        "containerConfig",
        when (this@toJson) {
            is FormConfig -> this@toJson.toJson(dashboardId, registerCallback)
            is SectionConfig -> this@toJson.toJson()
        }
    )
}

private fun parseElementConfig(
    element: ElementConfig,
    registerCallback: (DashboardCallback) -> Unit,
    dashboardId: String,
): JsonObject = when (element) {
    is ButtonConfig -> {
        val actionId = createActionId(dashboardId, element.id)
        registerCallback(
            ButtonCallback(
                id = actionId,
                action = element.onClick,
            )
        )
        element.toJson(
            actionId = actionId,
        )
    }

    is LabelConfig -> element.toJson()
    is TextConfig -> element.toJson()
    is PlainTextConfig -> element.toJson()
    is MarkdownConfig -> element.toJson()
    is TextFieldConfig -> {
        val actionId = createActionId(dashboardId, element.id)

        registerCallback(
            TextFieldCallback(
                id = actionId,
                action = element.onSubmitted,
            )
        )
        element.toJson(
            actionId = actionId
        )
    }

    is CheckBoxConfig -> {
        val actionId = createActionId(dashboardId, element.id)
        registerCallback(
            CheckBoxCallback(
                id = actionId,
                action = element.onUpdated,
            )
        )
        element.toJson(
            actionId = actionId,
        )
    }
}

private fun createActionId(dashboardId: String, elementId: String) = dashboardId + "_" + elementId

/** Form specific config*/
private fun FormConfig.toJson(
    dashboardId: String,
    registerCallback: (DashboardCallback) -> Unit,
): JsonObject {
    val actionId = createActionId(dashboardId, id)

    registerCallback(
        FormCallback(
            id = actionId,
            actions = onSubmitted
        )
    )

    return buildJsonObject {
        put("formId", actionId)
        put("submitText", submitText)
        put("containerType", containerType.name)
    }
}

/** Section specific config */
private fun SectionConfig.toJson(): JsonObject = buildJsonObject {
    put("containerType", containerType.name)
}

// {
//     "button" : {
//         "text": "click me",
//         "id": "1"
//     }
// }
internal fun ButtonConfig.toJson(actionId: String): JsonObject = buildJsonObject {
    putJsonObject("button") {
        put("text", text)
        put("id", actionId)
    }
}

// {
//     "label" : {
//         "label": "user id",
//     }
// }
internal fun LabelConfig.toJson(): JsonObject = buildJsonObject {
    putJsonObject("label") {
        put("label", label)
        color?.let { put("color", it) }
    }
}

// {
//     "text" : {
//         "label": "user id",
//         "value": "01010101010"
//     }
// }
internal fun TextConfig.toJson(): JsonObject = buildJsonObject {
    putJsonObject("text") {
        put("label", label)
        put("value", value)
        color?.let { put("color", it) }
    }
}

// {
//     "textField" : {
//         "id": "1",
//         "label": "update name",
//         "placeHolder": "new name",
//         "value: "florent",
//     }
// }
internal fun TextFieldConfig.toJson(actionId: String): JsonObject = buildJsonObject {
    putJsonObject("textField") {
        put("id", actionId)
        put("label", label)
        put("placeHolder", placeHolder)
        put("value", value)
    }
}


// {
//     "checkBox" : {
//         "id": "1",
//         "label": "update name",
//         "value: "florent",
//     }
// }
internal fun CheckBoxConfig.toJson(actionId: String) = buildJsonObject {
    putJsonObject("checkBox") {
        put("id", actionId)
        put("label", label)
        put("value", value)
    }
}

// {
//     "plainText" : {
//         "label": "user id",
//         "value": "01010101010",
//         "type": "text" / "json"
//     }
// }
internal fun PlainTextConfig.toJson() = buildJsonObject {
    putJsonObject("plainText") {
        put("label", label)
        put("value", value)
        put("type", type)
    }
}

// {
//     "markdown" : {
//         "label": "Release note",
//         "value": "# V 1.0",
//     }
// }
internal fun MarkdownConfig.toJson() = buildJsonObject {
    putJsonObject("markdown") {
        put("label", label)
        put("value", value)
    }
}
