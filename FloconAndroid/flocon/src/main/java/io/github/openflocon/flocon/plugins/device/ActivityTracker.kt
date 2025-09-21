package io.github.openflocon.flocon.plugins.device

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.util.WeakHashMap

internal fun startActivityTracker(context: Context) {
    ActivityTracker.initWith(context)
}

/**
 * Petit tracker d’activités actives, pour retrouver celles déjà lancées
 */
internal object ActivityTracker : Application.ActivityLifecycleCallbacks {
    private val activities = WeakHashMap<Activity, Boolean>()

    fun initWith(context: Context) {
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(ActivityTracker)
        if(context is Activity) {
            activities[context] = true
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities[activity] = true
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }

    fun currentActivities(): List<Activity> = activities.keys.toList()

    // autres vides
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}