package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Tokens of the Flocon design system (bg, ink, line, one color per event category, http statuses).
 */
@Immutable
data class FloconColors(
    val bg000: Color,
    val bg100: Color,
    val bg200: Color,
    val bg300: Color,
    val line: Color,
    val lineStrong: Color,
    val ink: Color,
    val inkMuted: Color,
    val inkFaint: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentInk: Color,
    val catNetwork: Color,
    val catNetworkSoft: Color,
    val catDatabase: Color,
    val catDatabaseSoft: Color,
    val catAnalytics: Color,
    val catAnalyticsSoft: Color,
    val catCrash: Color,
    val catCrashSoft: Color,
    val catTable: Color,
    val catTableSoft: Color,
    val catGrpc: Color,
    val catDeeplink: Color,
    val catDeeplinkSoft: Color,
    val catAdb: Color,
    val catAdbSoft: Color,
    val catAppstart: Color,
    val statusSuccess: Color,
    val statusInfo: Color,
    val statusWarning: Color,
    val statusError: Color,
) {
    /** Color of the category of an event and its soft background. */
    fun category(c: EventCategory): Pair<Color, Color> = when (c) {
        EventCategory.Network -> catNetwork to catNetworkSoft
        EventCategory.Database -> catDatabase to catDatabaseSoft
        EventCategory.Analytics -> catAnalytics to catAnalyticsSoft
        EventCategory.Crash -> catCrash to catCrashSoft
        EventCategory.Table -> catTable to catTableSoft
        EventCategory.Deeplink -> catDeeplink to catDeeplinkSoft
        EventCategory.Adb -> catAdb to catAdbSoft
        EventCategory.AppStart -> accent to accentSoft
    }

    /** Color of an http code: 1xx info, 2xx success, 4xx warning, 5xx error. */
    fun httpStatus(code: Int): Color = when (code / 100) {
        1 -> statusInfo
        2 -> statusSuccess
        4 -> statusWarning
        5 -> statusError
        else -> inkMuted
    }
}

enum class EventCategory { Network, Database, Analytics, Crash, Table, Deeplink, Adb, AppStart }

val FloconDarkColors = FloconColors(
    bg000 = Color(0xFF0E0F11),
    bg100 = Color(0xFF15161A),
    bg200 = Color(0xFF1B1D21),
    bg300 = Color(0xFF24262B),
    line = Color(0xFF2A2C32),
    lineStrong = Color(0xFF3A3D44),
    ink = Color(0xFFECEEF1),
    inkMuted = Color(0xFFA2A6AE),
    inkFaint = Color(0xFF80848C),
    accent = Color(0xFFF5B440),
    accentSoft = Color(0x1FF5B440),
    accentInk = Color(0xFF1C1405),
    catNetwork = Color(0xFF5EAAF7),
    catNetworkSoft = Color(0x215EAAF7),
    catDatabase = Color(0xFF3FCFB5),
    catDatabaseSoft = Color(0x213FCFB5),
    catAnalytics = Color(0xFFB494FF),
    catAnalyticsSoft = Color(0x24B494FF),
    catCrash = Color(0xFFFF6D6D),
    catCrashSoft = Color(0x1FFF6D6D),
    catTable = Color(0xFFA9D65E),
    catTableSoft = Color(0x21A9D65E),
    catGrpc = Color(0xFF5AD2E6),
    catDeeplink = Color(0xFFF483C2),
    catDeeplinkSoft = Color(0x21F483C2),
    catAdb = Color(0xFFA8ADB6),
    catAdbSoft = Color(0x21A8ADB6),
    catAppstart = Color(0xFFF5B440),
    statusSuccess = Color(0xFF4FD08A),
    statusInfo = Color(0xFF5EAAF7),
    statusWarning = Color(0xFFF7A531),
    statusError = Color(0xFFFF6D6D),
)

val FloconLightColors = FloconColors(
    bg000 = Color(0xFFF4F4F1),
    bg100 = Color(0xFFFFFFFF),
    bg200 = Color(0xFFFAFAF8),
    bg300 = Color(0xFFEFEFEB),
    line = Color(0xFFE6E5E0),
    lineStrong = Color(0xFFCFCEC7),
    ink = Color(0xFF16171A),
    inkMuted = Color(0xFF585C64),
    inkFaint = Color(0xFF6E727A),
    accent = Color(0xFFC27C0E),
    accentSoft = Color(0xFFFCF1DC),
    accentInk = Color(0xFFFFFFFF),
    catNetwork = Color(0xFF1D6BBF),
    catNetworkSoft = Color(0xFFE5F0FC),
    catDatabase = Color(0xFF0B7D6A),
    catDatabaseSoft = Color(0xFFDFF5F0),
    catAnalytics = Color(0xFF6A45D6),
    catAnalyticsSoft = Color(0xFFEFE9FD),
    catCrash = Color(0xFFC62F2F),
    catCrashSoft = Color(0xFFFDEAEA),
    catTable = Color(0xFF4D7A12),
    catTableSoft = Color(0xFFEEF6E0),
    catGrpc = Color(0xFF0E7490),
    catDeeplink = Color(0xFFB3317A),
    catDeeplinkSoft = Color(0xFFFBE8F2),
    catAdb = Color(0xFF555A63),
    catAdbSoft = Color(0xFFECECEA),
    catAppstart = Color(0xFFC27C0E),
    statusSuccess = Color(0xFF17804A),
    statusInfo = Color(0xFF1D6BBF),
    statusWarning = Color(0xFFA35A00),
    statusError = Color(0xFFC62F2F),
)

val LocalFloconColors = staticCompositionLocalOf { FloconDarkColors }
