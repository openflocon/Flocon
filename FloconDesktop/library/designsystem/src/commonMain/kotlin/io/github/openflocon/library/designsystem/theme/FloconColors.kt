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
    val methodGet: Color,
    val methodPost: Color,
    val methodPut: Color,
    val methodPatch: Color,
    val methodDelete: Color,
    val methodGraphQl: Color,
    val methodGrpc: Color,
    val methodWebSocket: Color,
    val methodOther: Color,
    val onMethodGet: Color,
    val onMethodPost: Color,
    val onMethodPut: Color,
    val onMethodPatch: Color,
    val onMethodDelete: Color,
    val onMethodGraphQl: Color,
    val onMethodGrpc: Color,
    val onMethodWebSocket: Color,
    val onMethodOther: Color,
    val catDeeplink: Color,
    val catDeeplinkSoft: Color,
    val catAdb: Color,
    val catAdbSoft: Color,
    val catAppstart: Color,
    val statusSuccess: Color,
    val statusInfo: Color,
    val statusWarning: Color,
    val statusError: Color,
    // opaque surface for what floats above the content (panels, dialogs, menus)
    val floating: Color,
    // background of the app chrome (left panel, top bar), transparent = the page shows through
    val chrome: Color,
    // tint the top bar fades to on its right side, transparent = flat chrome
    val chromeTint: Color,
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
    bg000 = Color(0xFF111111),
    bg100 = Color(0xFF151515),
    bg200 = Color(0xFF1F1F1F),
    bg300 = Color(0xFF282828),
    line = Color(0xFF2A2A2A),
    lineStrong = Color(0xFF3A3A3A),
    ink = Color(0xFFECECEC),
    inkMuted = Color(0xFFA3A3A3),
    inkFaint = Color(0xFF828282),
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
    methodGet = Color(0xFF223B65),
    methodPost = Color(0xFF275A3D),
    methodPut = Color(0xFF633619),
    methodPatch = Color(0xFF645B29),
    methodDelete = Color(0xFF4D181B),
    methodGraphQl = Color(0xFF473E66),
    methodGrpc = Color(0xFF385667),
    methodWebSocket = Color(0xFF18244D),
    methodOther = Color(0xFF32353D),
    onMethodGet = Color(0xFF76A8F9),
    onMethodPost = Color(0xFF6EE599),
    onMethodPut = Color(0xFFFA8F45),
    onMethodPatch = Color(0xFFFDE66C),
    onMethodDelete = Color(0xFFD57777),
    onMethodGraphQl = Color(0xFFB9A2FB),
    onMethodGrpc = Color(0xFF97DCFD),
    onMethodWebSocket = Color(0xFF8396D3),
    onMethodOther = Color(0xFFA6AAB3),
    catDeeplink = Color(0xFFF483C2),
    catDeeplinkSoft = Color(0x21F483C2),
    catAdb = Color(0xFFA8ADB6),
    catAdbSoft = Color(0x21A8ADB6),
    catAppstart = Color(0xFFF5B440),
    statusSuccess = Color(0xFF4FD08A),
    statusInfo = Color(0xFF5EAAF7),
    statusWarning = Color(0xFFF7A531),
    statusError = Color(0xFFFF6D6D),
    floating = Color(0xFF1C1C28),
    chrome = Color.Transparent,
    chromeTint = Color.Transparent,
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
    methodGet = Color(0xFFD4E4FD),
    methodPost = Color(0xFFD7F8E3),
    methodPut = Color(0xFFFEE0CC),
    methodPatch = Color(0xFFFFF8D7),
    methodDelete = Color(0xFFF0CDCD),
    methodGraphQl = Color(0xFFECE5FE),
    methodGrpc = Color(0xFFE2F5FE),
    methodWebSocket = Color(0xFFCED5ED),
    methodOther = Color(0xFFDEE0E3),
    onMethodGet = Color(0xFF234E94),
    onMethodPost = Color(0xFF246B3D),
    onMethodPut = Color(0xFF95450D),
    onMethodPatch = Color(0xFF726520),
    onMethodDelete = Color(0xFF6F1111),
    onMethodGraphQl = Color(0xFF645396),
    onMethodGrpc = Color(0xFF3E6A7E),
    onMethodWebSocket = Color(0xFF122669),
    onMethodOther = Color(0xFF40444D),
    catDeeplink = Color(0xFFB3317A),
    catDeeplinkSoft = Color(0xFFFBE8F2),
    catAdb = Color(0xFF555A63),
    catAdbSoft = Color(0xFFECECEA),
    catAppstart = Color(0xFFC27C0E),
    statusSuccess = Color(0xFF17804A),
    statusInfo = Color(0xFF1D6BBF),
    statusWarning = Color(0xFFA35A00),
    statusError = Color(0xFFC62F2F),
    floating = Color(0xFFF3F3F8),
    chrome = Color.Transparent,
    chromeTint = Color.Transparent,
)

val LocalFloconColors = staticCompositionLocalOf { FloconDarkColors }
