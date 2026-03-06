package io.github.openflocon.flocon.myapplication.deeplinks

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.deeplinks.deeplinks

fun initializeDeeplinks() {
    Flocon.deeplinks {
        variable("test_variable")
        variable("host") {
            description = "Host variable"
            autoComplete(listOf("flocon", "flocon2", "flocon3"))
        }
        deeplink("[host]://home") {
            "host" withVariable "host"
        }
        deeplink("[host]://test") {
            "host" withVariable "host"
        }
        deeplink("[host]://user/[userId]") {
            label = "User"
            "userId" withAutoComplete listOf("Florent", "David", "Guillaume")
            "host" withVariable "host"
        }
        deeplink("[host]://post/[postId]?comment=[commentText]") {
            label = "Post"
            description = "Open a post and send a comment"
            "commentText" withVariable "test_variable"
            "host" withVariable "host"
        }
    }
}