package io.github.openflocon.flocon.myapplication.multi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.openflocon.flocon.myapplication.multi.dashboard.tokens.Tokens
import io.github.openflocon.flocon.myapplication.multi.dashboard.tokens.tokensFlow
import io.github.openflocon.flocon.myapplication.multi.dashboard.user.User
import io.github.openflocon.flocon.myapplication.multi.dashboard.user.userFlow
import io.github.openflocon.flocon.plugins.dashboard.dsl.button
import io.github.openflocon.flocon.plugins.dashboard.dsl.checkBox
import io.github.openflocon.flocon.plugins.dashboard.dsl.form
import io.github.openflocon.flocon.plugins.dashboard.dsl.json
import io.github.openflocon.flocon.plugins.dashboard.dsl.label
import io.github.openflocon.flocon.plugins.dashboard.dsl.plainText
import io.github.openflocon.flocon.plugins.dashboard.dsl.section
import io.github.openflocon.flocon.plugins.dashboard.dsl.text
import io.github.openflocon.flocon.plugins.dashboard.dsl.textField
import io.github.openflocon.flocon.plugins.dashboard.floconDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DashboardData(
    val user: User?,
    val tokens: Tokens?,
)

fun initializeDashboard() {
    GlobalScope.launch {
        combine(
            userFlow,
            tokensFlow,
        ) { user, tokens ->
            DashboardData(
                user = user,
                tokens = tokens,
            )
        }.collect { (user, tokens) ->
            floconDashboard(id = "main") {
                user?.let {
                    section(name = "User") {
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
                }
                tokens?.let {
                    section(name = "Tokens") {
                        text(label = "accessToken", value = tokens.accessToken)
                        text(label = "refreshToken", value = tokens.refreshToken)
                        text(label = "expiration", value = tokens.expiration)
                        button(
                            text = "Clear Access Token",
                            id = "clearAccessToken",
                            onClick = {
                                println("cleaned access token")
                            }
                        )
                    }
                }
            }
        }
    }
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

    floconDashboard(id = "form") {
        form(
            name = "Test form",
            submitText = "Submit form test button",
            onSubmitted = { values ->
                values.forEach { (key, value) ->
                    println("$key - $value")
                    // input_1 - test
                    // checkbox_1 - true
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