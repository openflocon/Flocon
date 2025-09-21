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
    private var fps = 60f // Valeur par défaut plus réaliste

    /**
     * Initialise le tracking des activités - à appeler très tôt dans l'application
     * Cette méthode peut être appelée avant même qu'une activité existe
     */
    init {
        // Enregistrer les callbacks d'activité pour tracker dès maintenant
        application.registerActivityLifecycleCallbacks(activityCallbacks)
    }

    fun start() {
        if (isStarted) return
        isStarted = true

        // Commencer le calcul FPS
        startFpsCalculation()

        // Ajouter le FPS à l'activité actuelle si elle existe
        getCurrentActivity()?.let { activity ->
            addFpsToActivity(activity)
        }
    }

    fun stop() {
        if (!isStarted) return
        isStarted = false

        // Arrêter le calcul FPS
        stopFpsCalculation()

        // Supprimer tous les FPS views
        cleanupViews()
    }

    /**
     * Nettoie complètement le FpsDisplayer - à appeler lors de la destruction de l'app
     */
    fun destroy() {
        stop()
        application.unregisterActivityLifecycleCallbacks(activityCallbacks)
        currentActivityRef = null
        activityFpsViews.clear()
    }

    private fun startFpsCalculation() {
        lastFrameTime = System.nanoTime()
        frameCount = 0

        // Utiliser Choreographer pour compter les vrais frames
        frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCount++

                val currentTime = System.nanoTime()
                val timeDiff = currentTime - lastFrameTime

                // Calculer FPS chaque seconde
                if (timeDiff >= 1_000_000_000L) { // 1 seconde en nanosecondes
                    fps = frameCount * 1_000_000_000f / timeDiff
                    frameCount = 0
                    lastFrameTime = currentTime

                    // Mettre à jour l'affichage
                    updateAllFpsViews()
                }

                if (isStarted) {
                    choreographer.postFrameCallback(this)
                }
            }
        }

        choreographer.postFrameCallback(frameCallback!!)

        // Runnable pour mise à jour périodique de l'affichage (au cas où pas de frames)
        fpsRunnable = object : Runnable {
            override fun run() {
                updateAllFpsViews()
                if (isStarted) {
                    handler.postDelayed(this, 1000) // Mise à jour toutes les secondes
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
        // Nettoyer les références mortes
        val iterator = activityFpsViews.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val activity = entry.key.get()
            val textView = entry.value.get()

            if (activity == null || textView == null) {
                iterator.remove()
                continue
            }

            // Mise à jour sur le thread principal
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
        // Vérifier si on a déjà une vue pour cette activité
        if (findFpsViewForActivity(activity) != null) return

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        if (rootView != null && rootView.childCount > 0) {
            // La vue est déjà disponible
            createAndAddFpsView(activity, rootView)
        } else {
            // Attendre que la vue soit disponible
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
                setMargins(0, 50, 16, 0) // Marge pour éviter la barre de statut
            }

            // Si le rootView n'est pas un FrameLayout, on l'enveloppe
            when (rootView) {
                is FrameLayout -> {
                    rootView.addView(fpsTextView, layoutParams)
                }
                else -> {
                    // Créer un overlay FrameLayout
                    val overlay = FrameLayout(activity)
                    overlay.addView(fpsTextView, layoutParams)
                    rootView.addView(overlay, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ))
                }
            }

            // Stocker avec des WeakReferences
            activityFpsViews[WeakReference(activity)] = WeakReference(fpsTextView)

        } catch (e: Exception) {
            // En cas d'erreur, utiliser un WindowManager overlay
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

            // Stocker avec des WeakReferences
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
                    // Si c'est un overlay de WindowManager
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
            // Mettre à jour l'activité courante
            currentActivityRef = WeakReference(activity)

            if (isStarted) {
                // Petit délai pour s'assurer que la vue est prête
                handler.postDelayed({
                    // Vérifier que l'activité est toujours valide
                    if (currentActivityRef?.get() == activity) {
                        addFpsToActivity(activity)
                    }
                }, 100)
            }
        }

        override fun onActivityPaused(activity: Activity) {
            removeFpsFromActivity(activity)

            // Si c'est l'activité courante qui se met en pause
            if (currentActivityRef?.get() == activity) {
                currentActivityRef = null
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            removeFpsFromActivity(activity)

            // Si c'est l'activité courante qui est détruite
            if (currentActivityRef?.get() == activity) {
                currentActivityRef = null
            }
        }
    }
}