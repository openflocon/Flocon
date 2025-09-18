package io.github.openflocon.flocon.plugins.dashboard.mapper

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.*
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.CheckBoxConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ElementConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.FormConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.LabelConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextFieldConfig
import org.json.JSONArray
import org.json.JSONObject

internal fun DashboardConfig.toJson(
    registerCallback: (DashboardCallback) -> Unit,
): JSONObject {
    val rootJson = JSONObject()

    rootJson.put("dashboardId", id)

    val containersJsonArray = JSONArray()

    containers.forEach { container ->
        val containerJson = container.toJson(
            dashboardId = id,
            registerCallback = registerCallback,
        )
        containersJsonArray.put(containerJson)
    }

    rootJson.put("containers", containersJsonArray)
    return rootJson
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
): JSONObject = JSONObject().apply {

    val elementsJsonArray = JSONArray(elements.map { element ->
        parseElementConfig(
            element = element,
            registerCallback = registerCallback,
            dashboardId = dashboardId
        )
    })

    put("name", name)
    put("elements", elementsJsonArray)

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
): JSONObject = when (element) {
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
): JSONObject {
    val actionId = createActionId(dashboardId, id)

    registerCallback(
        FormCallback(
            id = actionId,
            actions = onSubmitted
        )
    )

    return JSONObject().apply {
        put("formId", actionId)
        put("submitText", submitText)
        put("containerType", containerType)
    }
}

/** Section specific config */
private fun SectionConfig.toJson(): JSONObject {
    return JSONObject().apply {
        put("containerType", containerType)
    }
}

// {
//     "button" : {
//         "text": "click me",
//         "id": "1"
//     }
// }
internal fun ButtonConfig.toJson(actionId: String): JSONObject {
    return JSONObject().apply {
        put("button", JSONObject().apply {
            put("text", text)
            put("id", actionId)
        })
    }
}

// {
//     "label" : {
//         "label": "user id",
//     }
// }
internal fun LabelConfig.toJson(): JSONObject {
    return JSONObject().apply {
        put("label", JSONObject().apply {
            put("label", label)
            color?.let {
                put("color", it)
            }
        })
    }
}

// {
//     "text" : {
//         "label": "user id",
//         "value": "01010101010"
//     }
// }
internal fun TextConfig.toJson(): JSONObject {
    return JSONObject().apply {
        put("text", JSONObject().apply {
            put("label", label)
            put("value", value)
            color?.let {
                put("color", it)
            }
        })
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
internal fun TextFieldConfig.toJson(actionId: String): JSONObject {
    return JSONObject().apply {
        put("textField", JSONObject().apply {
            put("id", actionId)
            put("label", label)
            put("placeHolder", placeHolder)
            put("value", value)
        })
    }
}


// {
//     "checkBox" : {
//         "id": "1",
//         "label": "update name",
//         "value: "florent",
//     }
// }
internal fun CheckBoxConfig.toJson(actionId: String): JSONObject {
    return JSONObject().apply {
        put("checkBox", JSONObject().apply {
            put("id", actionId)
            put("label", label)
            put("value", value)
        })
    }
}

// {
//     "plainText" : {
//         "label": "user id",
//         "value": "01010101010",
//         "type": "text" / "json"
//     }
// }
internal fun PlainTextConfig.toJson(): JSONObject {
    return JSONObject().apply {
        put("text", JSONObject().apply {
            put("label", label)
            put("value", value)
            put("type", type)
        })
    }
}
