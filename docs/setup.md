🛠️ Getting Started

## Installation

in your module .kts

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
```
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
```

in your `Application.kt`
```kotlin
// android initialization
Flocon.initialize(this)

// desktop / ios
Flocon.initialize()
```

```toml
[versions]
flocon = "LASTEST_VERSION"

[libraries]
flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
```

Download & install the last [Desktop client](https://github.com/openflocon/Flocon/releases)

> [!NOTE]
> If you are on MacOS, you might need to follow specific instructions to open the app. See [MacOS Installation](macos-install.md).

## 🧰 Requirements

### for android
- An Android device with USB debugging enabled
- Android Studio or SDK tools installed
- ADB (Android Debug Bridge) accessible from your system path

### for all platforms
- Flocon Desktop app (JVM-based)
- Flocon SDK integrated into your app
- At least `kotlin 2.0.0` in your app
- Be aligned between the mobile library version & the desktop app version