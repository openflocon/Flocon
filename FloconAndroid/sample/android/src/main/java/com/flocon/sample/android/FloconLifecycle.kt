package com.flocon.sample.android

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.timeline.timelinePlugin

/**
 * Logs in the Flocon timeline when the app goes to the foreground or the background: the
 * single activity of the sample is visible or not. The observer is removed with the lifecycle.
 */
fun logFloconLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) = Flocon.timelinePlugin.logLifecycle("foreground")
        override fun onStop(owner: LifecycleOwner) = Flocon.timelinePlugin.logLifecycle("background")
    })
}
