package io.github.openflocon.flocon.accessibility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import io.github.openflocon.flocon.FloconLogger

private const val WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"

private var applicationContext: Context? = null

internal fun onApplicationForTalkBackSwitch(context: Context) {
    applicationContext = context
}

internal actual fun canToggleTalkBack(): Boolean {
    val context = applicationContext ?: return false
    return context.packageManager.checkPermission(WRITE_SECURE_SETTINGS, context.packageName) ==
        PackageManager.PERMISSION_GRANTED
}

internal actual fun setTalkBackEnabled(enabled: Boolean): TalkBackToggleResult {
    val context = applicationContext ?: return TalkBackToggleResult.Failed
    if (talkBackToggleDecision(canToggleTalkBack()) == TalkBackToggleDecision.OpenSettings) {
        return if (context.openTalkBackSettings()) TalkBackToggleResult.OpenedSettings else TalkBackToggleResult.Failed
    }

    return try {
        val resolver = context.contentResolver
        val current = Settings.Secure.getString(resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val updated = if (enabled) {
            val service = findTalkBackService { context.isInstalled(it) } ?: return TalkBackToggleResult.Failed
            withService(current, service)
        } else {
            withoutTalkBack(current)
        }
        val written = Settings.Secure.putString(resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated) &&
            Settings.Secure.putInt(resolver, Settings.Secure.ACCESSIBILITY_ENABLED, if (updated.isEmpty()) 0 else 1)
        if (written) TalkBackToggleResult.Toggled else TalkBackToggleResult.Failed
    } catch (t: SecurityException) {
        FloconLogger.logError("error on setTalkBackEnabled", t)
        TalkBackToggleResult.Failed
    }
}

/**
 * Opens the settings page of TalkBack (the accessibility settings before Android 12, or if it fails).
 * Since Android 10 an activity can only be started from the background in few cases: it works while the app is visible.
 */
private fun Context.openTalkBackSettings(): Boolean {
    val service = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        findTalkBackService { isInstalled(it) }
    } else {
        null
    }
    if (service != null) {
        val details = Intent(Settings.ACTION_ACCESSIBILITY_DETAILS_SETTINGS)
            .putExtra(Intent.EXTRA_COMPONENT_NAME, service)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (startSettings(details)) return true
    }
    return startSettings(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

private fun Context.startSettings(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (t: Exception) {
    FloconLogger.logError("error on openTalkBackSettings", t)
    false
}

@Suppress("DEPRECATION")
private fun Context.isInstalled(packageName: String): Boolean = try {
    packageManager.getApplicationInfo(packageName, 0)
    true
} catch (_: PackageManager.NameNotFoundException) {
    false
}
