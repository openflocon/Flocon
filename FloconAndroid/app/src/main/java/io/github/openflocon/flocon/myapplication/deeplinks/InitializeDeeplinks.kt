package io.github.openflocon.flocon.myapplication.deeplinks

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.deeplinks.deeplinks

fun initializeDeeplinks() {
    Flocon.deeplinks {
        deeplink("flocon://home")
        deeplink("flocon://test")
        deeplink(
            "flocon://user/[userId]",
            label = "User",
            parameters = {
                "userId" withAutoComplete listOf("Notification", "AppStart", "HomeScreen")
            }
        )
        deeplink(
            "flocon://post/[postId]?comment=[commentText]",
            label = "Post",
            description = "Open a post and send a comment"
        )
    }
}