# Install Flocon in a Kotlin Multiplatform project

Source of truth: `docs/install-kotlin.md`.

## 1. Dependencies (shared module)

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.openflocon:flocon:LAST_VERSION")
            implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
        }
    }
}
```

Check the platform column of each module in `docs/install-kotlin.md` (for example OkHttp is Android/JVM only, Ktor also covers iOS/Wasm). Wasm has no filesystem or SQLite, so no files/databases.

## 2. Release builds

A KMP module has one variant, so it depends on the real modules with `implementation`. In the Android app, add `releaseImplementation` of `flocon-no-op`; substitute only `flocon-accessibility` / `flocon-qr-scanner` with their `-no-op` in the release configurations if they are used: the exact snippet is in `docs/install-kotlin.md`, "Release builds". The iOS framework, desktop (JVM) and Wasm targets have no release configuration: add `flocon-no-op` to the build that ships, or guard `startFlocon` with the app's own debug check; ask once which one the user wants.

## 3. Initialization per platform

```kotlin
// Android
startFlocon(FloconContext(this)) { install(FloconNetwork) }
// Desktop (JVM)
startFlocon(FloconContext(appName = "My App", packageName = "com.example.myapp")) { install(FloconNetwork) }
// iOS / Wasm (call from shared code or from Swift)
startFlocon(FloconContext()) { install(FloconNetwork) }
```

Put the shared part in `commonMain` and keep the `FloconContext` creation in each platform entry point.

## 4. Check

Compile the Android target and the iOS framework (or at least `compileKotlinIosSimulatorArm64`). State what was not run.
