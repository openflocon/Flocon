package io.github.openflocon.flocon.plugins.device

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Choreographer
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import java.util.WeakHashMap

/**
 * Une instance par activité
 */
internal class ActivityFpsOverlay(private val activity: Activity) : Choreographer.FrameCallback {

    private val choreographer by lazy {
        Choreographer.getInstance()
    }
    private var frameCount = 0
    private var lastTimeMillis = 0L
    private var running = false

    private val fpsView by lazy {
        TextView(activity).apply {
            textSize = 14f
            setTextColor(Color.RED)
            setBackgroundColor(Color.argb(128, 0, 0, 0))
            text = "FPS: --"
            setPadding(16, 8, 16, 8)
        }
    }

    fun attach() {
        val root = activity.findViewById<ViewGroup>(android.R.id.content)
        if (fpsView.parent == null) {
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                marginStart = 20
                topMargin = 100
            }
            root.addView(fpsView, lp)
        }
        startMeter()
    }

    fun detach() {
        fpsView.post {
            stopMeter()
            (fpsView.parent as? ViewGroup)?.removeView(fpsView)
        }
    }

    private fun startMeter() {
        if (running) return
        running = true
        frameCount = 0
        lastTimeMillis = System.currentTimeMillis()
        choreographer.postFrameCallback(this)
    }

    private fun stopMeter() {
        running = false
        choreographer.removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running) return

        frameCount++
        val now = System.currentTimeMillis()
        val delta = now - lastTimeMillis

        if (delta >= 1000) {
            val fps = frameCount * 1000 / delta
            fpsView.text = "FPS: $fps"
            frameCount = 0
            lastTimeMillis = now
        }
        choreographer.postFrameCallback(this)
    }
}

/**
 * Manager qui gère les overlays FPS pour chaque activité
 */
class FpsOverlay(context: Context) {

    private val app = context.applicationContext as Application

    private val overlays = WeakHashMap<Activity, ActivityFpsOverlay>()
    private var started = false

    fun start() {
        if (started) return
        started = true
        println("FpsOverlay start")

        app.registerActivityLifecycleCallbacks(callbacks)

        // 🔑 Si on démarre après la création d’une activité,
        // on rattache un overlay aux activités déjà présentes
        println("FpsOverlay ActivityTracker.currentActivities(): ${ActivityTracker.currentActivities()}")
        ActivityTracker.currentActivities().forEach { activity ->
            activity.runOnUiThread {
                attachOverlay(activity)
            }
        }
    }

    fun stop() {
        if (!started) return

        started = false
        println("FpsOverlay stop")
        app.unregisterActivityLifecycleCallbacks(callbacks)
        val keys = overlays.keys
        keys.forEach { detachOverlay(it) }
    }

    private fun attachOverlay(activity: Activity) {
        synchronized(overlays) {
            println("FpsOverlay attachOverlay-values: ${overlays.keys}")
            if (overlays.containsKey(activity)) {
                return
            }
            val overlay = ActivityFpsOverlay(activity)
            overlays[activity] = overlay

            println("FpsOverlay attachOverlay: $activity")
            val root = activity.findViewById<ViewGroup>(android.R.id.content)
            if (root == null) {
                // on attend que la vue existe
                println("FpsOverlay [postponed] attachOverlay: $activity")
                activity.window?.decorView?.viewTreeObserver?.let { viewTreeObserver ->
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            activity.runOnUiThread {
                                println("FpsOverlay [executed] attachOverlay: $activity")
                                overlay.attach()
                                viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }
                    })
                }
                return
            }

            println("FpsOverlay [direct] attachOverlay: $activity")
            overlay.attach()
        }
    }

    private fun detachOverlay(activity: Activity) {
        println("FpsOverlay detachOverlay: $activity")
        synchronized(overlays) {
            overlays.remove(activity)?.detach()
        }
    }

    private val callbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            if (started) attachOverlay(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            if (started) detachOverlay(activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            detachOverlay(activity)
        }

        // inutilisés
        override fun onActivityCreated(a: Activity, b: Bundle?) {}
        override fun onActivityStarted(a: Activity) {}
        override fun onActivityStopped(a: Activity) {}
        override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
    }
}

