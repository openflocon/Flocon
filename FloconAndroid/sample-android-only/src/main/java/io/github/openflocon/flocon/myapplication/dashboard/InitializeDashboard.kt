package io.github.openflocon.flocon.myapplication.dashboard

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.lifecycleScope
import io.github.openflocon.flocon.myapplication.dashboard.device.deviceFlow
import io.github.openflocon.flocon.myapplication.dashboard.device.initializeDeviceFlow
import io.github.openflocon.flocon.myapplication.dashboard.tokens.tokensFlow
import io.github.openflocon.flocon.myapplication.dashboard.user.userFlow
import io.github.openflocon.flocon.plugins.dashboard.dsl.button
import io.github.openflocon.flocon.plugins.dashboard.dsl.checkBox
import io.github.openflocon.flocon.plugins.dashboard.dsl.json
import io.github.openflocon.flocon.plugins.dashboard.dsl.label
import io.github.openflocon.flocon.plugins.dashboard.dsl.markdown
import io.github.openflocon.flocon.plugins.dashboard.dsl.plainText
import io.github.openflocon.flocon.plugins.dashboard.dsl.text
import io.github.openflocon.flocon.plugins.dashboard.dsl.textField
import io.github.openflocon.flocon.plugins.dashboard.floconDashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun initializeDashboard(activity: ComponentActivity) {
    initializeDeviceFlow(activity)
    activity.lifecycleScope.launch {
        floconDashboard(id = "main") {
            section(name = "User", userFlow) { user ->
                text(label = "username", value = user.userName)
                text(label = "fullName", value = user.fullName, color = Color.Red.toArgb())
                text(label = "user id", value = user.id)
                label(label = "actions :")
                button(
                    text = "Change User Id",
                    id = "changeUserId",
                    onClick = {
                        userFlow.update { it.copy(userName = "__flo__") }
                    }
                )
                textField(
                    label = "Update Name",
                    placeHolder = "name",
                    id = "changeUserName",
                    value = user.fullName,
                    onSubmitted = { value ->
                        userFlow.update { it.copy(fullName = value) }
                    })
            }
            section(name = "Device", deviceFlow) { device ->
                text(label = "name", value = device?.name ?: "")
                text(label = "androidVersion", value = device?.androidVersion ?: "")
                text(label = "language", value = device?.language ?: "")
                text(label = "width", value = device?.width?.toString() ?: "")
                text(label = "height", value = device?.height?.toString() ?: "")
                text(label = "density", value = device?.density?.toString() ?: "")
                checkBox(
                    id = "darkTheme",
                    label = "darkTheme",
                    value = device?.darkTheme ?: false,
                    onUpdated = { newValue ->
                        deviceFlow.update {
                            it?.copy(
                                darkTheme = newValue,
                            )
                        }
                    }
                )
            }
            section(name = "Tokens", tokensFlow) { tokens ->
                text(label = "accessToken", value = tokens.accessToken)
                text(label = "refreshToken", value = tokens.refreshToken)
                text(label = "expiration", value = tokens.expiration)
                button(
                    text = "Clear Access Token",
                    id = "clearAccessToken",
                    onClick = {
                        activity.lifecycleScope.launch(Dispatchers.Main) {
                            tokensFlow.update {
                                it.copy(accessToken = "")
                            }
                            Toast.makeText(
                                activity,
                                "cleaned access token",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }
    }
    activity.lifecycleScope.launch {
        floconDashboard(id = "plainText") {
            section(name = "Test") {
                plainText(
                    label = "lorem",
                    value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ullamcorper elit. Pellentesque turpis ex, cursus cursus urna sed, iaculis sagittis nisl. Curabitur vehicula nunc eu metus rhoncus placerat. Vivamus at placerat ligula. Morbi ullamcorper cursus tellus, vitae molestie lorem sollicitudin euismod. Sed ullamcorper, risus vitae facilisis tempor, elit leo accumsan purus, ut ultricies augue erat et justo. Duis efficitur mauris eu finibus tincidunt. Aenean magna libero, auctor quis turpis et, viverra porta lorem. Ut tempus odio sit amet vestibulum condimentum. Donec et augue quis arcu blandit sodales. In laoreet odio id turpis ultricies, eu ornare dui blandit. Morbi hendrerit velit turpis, eget ornare ex consequat id. Nullam rhoncus, libero et sollicitudin tristique, risus ipsum luctus neque, ultricies ullamcorper felis metus non turpis. Nullam sed accumsan sem, at fermentum tortor."
                )
                json(
                    label = "json", value = """
                                {
                                  "testData": {
                                    "name": "John Doe",
                                    "age": 30,
                                    "isStudent": false,
                                    "courses": [
                                      {
                                        "title": "History I",
                                        "credits": 3
                                      },
                                      {
                                        "title": "Math II",
                                        "credits": 4
                                      }
                                    ],
                                    "address": {
                                      "street": "123 Main St",
                                      "city": "Anytown",
                                      "zipCode": "12345"
                                    }
                                  },
                                  "status": "success",
                                  "message": "Test data loaded successfully."
                                }
                    """.trimIndent()
                )
            }
        }
    }

    activity.lifecycleScope.launch {
        floconDashboard(id = "form") {
            form(
                name = "Test form",
                submitText = "Submit form test button",
                onSubmitted = { values ->
                    values.forEach { (key, value) ->
                        Log.e("TAG", "$key - $value")
                        // input_1 - test
                        // checkbox_1 - true
                        activity.lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                activity,
                                "submitted : $values",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            ) {
                textField(
                    id = "input_1",
                    label = "Input field 1",
                    placeHolder = "placeholder",
                    value = "test"
                )
                checkBox(
                    id = "checkbox_1",
                    label = "Checkbox 1",
                    value = true,
                )
            }
        }
    }
    activity.lifecycleScope.floconDashboard(id = "markdown") {
        section(name = "Markdown") {
            markdown(
                label = "Release Note",
                value = """
                    # Release Note
                    
                    This is a **markdown** text.
                    
                    * [x] item 1
                    * [ ] item 2
                    
                    Hello `world` !
                """.trimIndent()
            )
        }
    }

}