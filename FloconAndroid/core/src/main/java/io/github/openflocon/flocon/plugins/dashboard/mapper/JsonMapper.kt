package io.github.openflocon.flocon.plugins.dashboard.mapper

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback.*
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.CheckBoxConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextFieldConfig
import org.json.JSONArray
import org.json.JSONObject

fun DashboardConfig.toJson(
    registerCallback: (DashboardCallback) -> Unit,
): JSONObject {
    val rootJson = JSONObject()

    rootJson.put("dashboardId", id)

    val sectionsJsonArray = JSONArray()

    sections.forEach { section ->
        val sectionJson = section.sectionToJson(
            dashboardId = id,
            registerCallback = registerCallback,
        )
        sectionsJsonArray.put(sectionJson)
    }

    rootJson.put("sections", sectionsJsonArray)
    return rootJson
}


internal fun SectionConfig.sectionToJson(
    registerCallback: (DashboardCallback) -> Unit,
    dashboardId: String,
): JSONObject {
    val sectionJson = JSONObject()
    sectionJson.put("name", this.name)

    val sectionArray = JSONArray()
    elements.forEach {
        val elementJson: JSONObject = when (it) {
            is ButtonConfig -> {
                val actionId = dashboardId + "_" + it.id
                registerCallback(
                    ButtonCallback(
                        id = actionId,
                        action = it.onClick,
                    )
                )
                it.toJson(
                    actionId = actionId,
                )
            }

            is TextConfig -> it.toJson()
            is PlainTextConfig -> it.toJson()
            is TextFieldConfig -> {
                val actionId = dashboardId + "_" + it.id

                registerCallback(
                    TextFieldCallback(
                        id = actionId,
                        action = it.onSubmitted,
                    )
                )
                it.toJson(
                    actionId = actionId
                )
            }

            is CheckBoxConfig -> {
                val actionId = dashboardId + "_" + it.id
                registerCallback(
                    CheckBoxCallback(
                        id = actionId,
                        action = it.onUpdated,
                    )
                )
                it.toJson(
                    actionId = actionId,
                )
            }
        }
        sectionArray.put(elementJson)
    }
    sectionJson.put("elements", sectionArray)
    return sectionJson
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
