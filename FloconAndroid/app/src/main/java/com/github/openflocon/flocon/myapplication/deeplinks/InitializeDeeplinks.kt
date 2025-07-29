package com.github.openflocon.flocon.myapplication.deeplinks

import com.github.openflocon.flocon.Flocon
import com.github.openflocon.flocon.plugins.deeplinks.deeplinks
import com.github.openflocon.flocon.plugins.deeplinks.model.Deeplink

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