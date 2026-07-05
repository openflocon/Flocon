package io.github.openflocon.flocondesktop.features.adbcommander.model

data class QuickCommand(
    val name: String,
    val command: String,
    val category: String,
)

val defaultQuickCommands = listOf(
    // Device Info
    QuickCommand("Device Model", "shell getprop ro.product.model", "Device Info"),
    QuickCommand("Android Version", "shell getprop ro.build.version.release", "Device Info"),
    QuickCommand("SDK Version", "shell getprop ro.build.version.sdk", "Device Info"),
    QuickCommand("Device Serial", "shell getprop ro.serialno", "Device Info"),
    QuickCommand("Battery Status", "shell dumpsys battery", "Device Info"),
    QuickCommand("Screen Resolution", "shell wm size", "Device Info"),
    QuickCommand("Screen Density", "shell wm density", "Device Info"),
    QuickCommand("IP Address", "shell ip addr show wlan0", "Device Info"),

    // App Management
    QuickCommand("List Installed Packages", "shell pm list packages", "App Management"),
    QuickCommand("List 3rd Party Apps", "shell pm list packages -3", "App Management"),
    QuickCommand("Force Stop App", "shell am force-stop [package.name]", "App Management"),
    QuickCommand("Clear App Data", "shell pm clear [package.name]", "App Management"),
    QuickCommand("Uninstall App", "uninstall [package.name]", "App Management"),
    QuickCommand("Grant Permission", "shell pm grant [package.name] [permission]", "App Management"),
    QuickCommand("Revoke Permission", "shell pm revoke [package.name] [permission]", "App Management"),

    // Input & Interaction
    QuickCommand("Tap Screen", "shell input tap [x] [y]", "Input"),
    QuickCommand("Swipe", "shell input swipe [x1] [y1] [x2] [y2] [duration_ms]", "Input"),
    QuickCommand("Input Text", "shell input text [text]", "Input"),
    QuickCommand("Press Back", "shell input keyevent 4", "Input"),
    QuickCommand("Press Home", "shell input keyevent 3", "Input"),
    QuickCommand("Press Enter", "shell input keyevent 66", "Input"),
    QuickCommand("Press Power", "shell input keyevent 26", "Input"),
    QuickCommand("Volume Up", "shell input keyevent 24", "Input"),
    QuickCommand("Volume Down", "shell input keyevent 25", "Input"),
    QuickCommand("Open Recent Apps", "shell input keyevent 187", "Input"),

    // Connectivity
    QuickCommand("Enable WiFi", "shell svc wifi enable", "Connectivity"),
    QuickCommand("Disable WiFi", "shell svc wifi disable", "Connectivity"),
    QuickCommand("Enable Mobile Data", "shell svc data enable", "Connectivity"),
    QuickCommand("Disable Mobile Data", "shell svc data disable", "Connectivity"),
    QuickCommand("Toggle Airplane Mode ON", "shell settings put global airplane_mode_on 1", "Connectivity"),
    QuickCommand("Toggle Airplane Mode OFF", "shell settings put global airplane_mode_on 0", "Connectivity"),

    // Debug & Logs
    QuickCommand("Logcat (last 50 lines)", "logcat -t 50", "Debug"),
    QuickCommand("Clear Logcat", "logcat -c", "Debug"),
    QuickCommand("Dump Activity Stack", "shell dumpsys activity activities", "Debug"),
    QuickCommand("Current Activity", "shell dumpsys activity activities | grep mResumedActivity", "Debug"),
    QuickCommand("Current Fragment", "shell dumpsys activity top | grep -E 'Added Fragments|#[0-9]+:'", "Debug"),

    // Settings
    QuickCommand("Open Developer Options", "shell am start -a android.settings.APPLICATION_DEVELOPMENT_SETTINGS", "Settings"),
    QuickCommand("Open WiFi Settings", "shell am start -a android.settings.WIFI_SETTINGS", "Settings"),
    QuickCommand("Open App Settings", "shell am start -a android.settings.APPLICATION_DETAILS_SETTINGS -d package:[package.name]", "Settings"),
    QuickCommand("Open Date/Time Settings", "shell am start -a android.settings.DATE_SETTINGS", "Settings"),

    // Performance
    QuickCommand("CPU Info", "shell cat /proc/cpuinfo", "Performance"),
    QuickCommand("Memory Info", "shell cat /proc/meminfo", "Performance"),
    QuickCommand("Running Processes", "shell ps -A", "Performance"),
    QuickCommand("Disk Usage", "shell df -h", "Performance"),
)
