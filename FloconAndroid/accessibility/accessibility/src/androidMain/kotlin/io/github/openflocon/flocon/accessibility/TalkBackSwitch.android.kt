package io.github.openflocon.flocon.accessibility

import android.content.Context
import android.content.pm.PackageManager
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

internal actual fun setTalkBackEnabled(enabled: Boolean): Boolean {
    val context = applicationContext ?: return false
    if (!canToggleTalkBack()) return false

    return try {
        val resolver = context.contentResolver
        val current = Settings.Secure.getString(resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val updated = if (enabled) {
            val service = findTalkBackService { context.isInstalled(it) } ?: return false
            withService(current, service)
        } else {
            withoutTalkBack(current)
        }
        Settings.Secure.putString(resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated) &&
            Settings.Secure.putInt(resolver, Settings.Secure.ACCESSIBILITY_ENABLED, if (updated.isEmpty()) 0 else 1)
    } catch (t: SecurityException) {
        FloconLogger.logError("error on setTalkBackEnabled", t)
        false
    }
}

@Suppress("DEPRECATION")
private fun Context.isInstalled(packageName: String): Boolean = try {
    packageManager.getApplicationInfo(packageName, 0)
    true
} catch (_: PackageManager.NameNotFoundException) {
    false
}
