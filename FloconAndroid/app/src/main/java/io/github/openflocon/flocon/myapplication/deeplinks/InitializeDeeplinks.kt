package io.github.openflocon.flocon.myapplication.deeplinks

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.deeplinks.deeplinks
import io.github.openflocon.flocon.plugins.deeplinks.model.Deeplink

fun initializeDeeplinks() {
    Flocon.deeplinks(
        listOf(
            Deeplink("flocon://home"),
            Deeplink("flocon://test"),
            Deeplink(
                "flocon://user/[userId]",
                label = "User"
            ),
            Deeplink(
                "flocon://post/[postId]?comment=[commentText]",
                label = "Post",
                description = "Open a post and send a comment"
            ),
        )
    )
}