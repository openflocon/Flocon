package io.github.openflocon.flocon.plugins.device

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class FpsDisplayer(context: Context) {

    private val application: Application = context.applicationContext as Application
    private val handler = Handler(Looper.getMainLooper())
    private val choreographer = Choreographer.getInstance()
    private val activityFpsViews = ConcurrentHashMap<WeakReference<Activity>, WeakReference<TextView>>()
    private val activityCallbacks = ActivityCallbacks()

    private var currentActivityRef: WeakReference<Activity>? = null
    private var isStarted = false
    private var fpsRunnable: Runnable? = null
    private var frameCallback: Choreographer.FrameCallback? = null
    private var lastFrameTime = System.nanoTime()
    private var frameCount = 0
    private var fps = 60f

    init {
        application.registerActivityLifecycleCallbacks(activityCallbacks)
    }

    fun start() {
        if (isStarted) return
        isStarted = true
        startFpsCalculation()
        getCurrentActivity()?.let { activity ->
            addFpsToActivity(activity)
        }
    }

    fun stop() {
        if (!isStarted) return
        isStarted = false
        stopFpsCalculation()
        cleanupViews()
    }

    fun destroy() {
        stop()
        application.unregisterActivityLifecycleCallbacks(activityCallbacks)
        currentActivityRef = null
        activityFpsViews.clear()
    }

    private fun startFpsCalculation() {
        lastFrameTime = System.nanoTime()
        frameCount = 0

        frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCount++
                val currentTime = System.nanoTime()
                val timeDiff = currentTime - lastFrameTime

                if (timeDiff >= 1_000_000_000L) {
                    fps = frameCount * 1_000_000_000f / timeDiff
                    frameCount = 0
                    lastFrameTime = currentTime
                    updateAllFpsViews()
                }

                if (isStarted) {
                    choreographer.postFrameCallback(this)
                }
            }
        }

        choreographer.postFrameCallback(frameCallback!!)

        fpsRunnable = object : Runnable {
            override fun run() {
                updateAllFpsViews()
                if (isStarted) {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(fpsRunnable!!)
    }

    private fun stopFpsCalculation() {
        frameCallback?.let { choreographer.removeFrameCallback(it) }
        frameCallback = null

        fpsRunnable?.let { handler.removeCallbacks(it) }
        fpsRunnable = null
    }

    private fun updateAllFpsViews() {
        val iterator = activityFpsViews.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val activity = entry.key.get()
            val textView = entry.value.get()

            if (activity == null || textView == null) {
                iterator.remove()
                continue
            }

            handler.post {
                textView.text = "FPS: ${String.format("%.1f", fps)}"
            }
        }
    }

    private fun cleanupViews() {
        val iterator = activityFpsViews.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val activity = entry.key.get()
            val textView = entry.value.get()

            if (activity != null && textView != null) {
                removeFpsView(activity, textView)
            }
            iterator.remove()
        }
    }

    private fun getCurrentActivity(): Activity? {
        return currentActivityRef?.get()
    }

    private fun addFpsToActivity(activity: Activity) {
        if (findFpsViewForActivity(activity) != null) return

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        if (rootView != null && rootView.childCount > 0) {
            createAndAddFpsView(activity, rootView)
        } else {
            rootView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (isStarted && findFpsViewForActivity(activity) == null) {
                        createAndAddFpsView(activity, rootView)
                    }
                }
            })
        }
    }

    private fun findFpsViewForActivity(activity: Activity): TextView? {
        activityFpsViews.entries.forEach { entry ->
            if (entry.key.get() == activity) {
                return entry.value.get()
            }
        }
        return null
    }

    private fun createAndAddFpsView(activity: Activity, rootView: ViewGroup) {
        try {
            val fpsTextView = TextView(activity).apply {
                text = "FPS: ${String.format("%.1f", fps)}"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.argb(150, 0, 0, 0))
                textSize = 12f
                setPadding(8, 4, 8, 4)
            }

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 50, 16, 0)
            }

            when (rootView) {
                is FrameLayout -> {
                    rootView.addView(fpsTextView, layoutParams)
                }
                else -> {
                    val overlay = FrameLayout(activity)
                    overlay.addView(fpsTextView, layoutParams)
                    rootView.addView(overlay, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ))
                }
            }

            activityFpsViews[WeakReference(activity)] = WeakReference(fpsTextView)

        } catch (e: Exception) {
            addFpsAsWindowOverlay(activity)
        }
    }

    private fun addFpsAsWindowOverlay(activity: Activity) {
        try {
            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val fpsTextView = TextView(activity).apply {
                text = "FPS: ${String.format("%.1f", fps)}"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.argb(150, 0, 0, 0))
                textSize = 12f
                setPadding(8, 4, 8, 4)
            }

            val layoutParams = WindowManager.LayoutParams().apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                type = WindowManager.LayoutParams.TYPE_APPLICATION
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.TOP or Gravity.END
                x = 16
                y = 50
            }

            windowManager.addView(fpsTextView, layoutParams)

            activityFpsViews[WeakReference(activity)] = WeakReference(fpsTextView)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeFpsFromActivity(activity: Activity) {
        val iterator = activityFpsViews.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key.get() == activity) {
                val textView = entry.value.get()
                if (textView != null) {
                    removeFpsView(activity, textView)
                }
                iterator.remove()
                break
            }
        }
    }

    private fun removeFpsView(activity: Activity, fpsView: TextView) {
        try {
            val parent = fpsView.parent
            when (parent) {
                is ViewGroup -> parent.removeView(fpsView)
                else -> {
                    try {
                        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        windowManager.removeView(fpsView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class ActivityCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            currentActivityRef = WeakReference(activity)

            if (isStarted) {
                handler.postDelayed({
                    if (currentActivityRef?.get() == activity) {
                        addFpsToActivity(activity)
                    }
                }, 100)
            }
        }

        override fun onActivityPaused(activity: Activity) {
            removeFpsFromActivity(activity)
            if (currentActivityRef?.get() == activity) {
                currentActivityRef = null
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            removeFpsFromActivity(activity)
            if (currentActivityRef?.get() == activity) {
                currentActivityRef = null
            }
        }
    }
}
