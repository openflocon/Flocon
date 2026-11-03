# Install Flocon in a Kotlin Multiplatform project

Source of truth: `docs2/setup.md`.

## 1. Dependencies (shared module)

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.openflocon:flocon:LAST_VERSION")
            implementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
            implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
        }
    }
}
```

Check the platform column of each module in `docs2/migration.md` section 1 (for example OkHttp is Android/JVM only, Ktor also covers iOS/Wasm). Wasm has no filesystem or SQLite, so no files/databases.

## 2. Release builds

A KMP module has one variant, so it depends on the real modules. Swap them for the `-no-op` in the release configurations of the app with dependency substitution: the exact snippet is in `docs2/setup.md`, "Release builds". Add one line per Flocon module used.

## 3. Initialization per platform

```kotlin
// Android
startFlocon(this) { install(FloconNetwork) }
// Desktop (JVM)
startFlocon(FloconContext(appName = "My App", packageName = "com.example.myapp")) { install(FloconNetwork) }
// iOS / Wasm (call from shared code or from Swift)
startFlocon(FloconContext()) { install(FloconNetwork) }
```

Put the shared part in `commonMain` and keep the `FloconContext` creation in each platform entry point.

## 4. Check

Compile the Android target and the iOS framework (or at least `compileKotlinIosSimulatorArm64`). State what was not run.
