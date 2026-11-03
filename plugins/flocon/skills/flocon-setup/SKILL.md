---
name: flocon-setup
description: Use when installing Flocon in an Android, Kotlin Multiplatform or Swift (iOS) project, or when migrating an existing Flocon integration to a newer SDK version (for example 1.x to 2.0) - adding dependencies, no-op release builds, initialization and plugin installation.
---

# Setting up or migrating Flocon in a project

Flocon is a debugging SDK: the app talks to Flocon Desktop. This skill puts the SDK in a project, or moves an existing integration to a newer version. It does not debug a running app (that is `flocon-mcp`).

Always follow the repository docs when they are available: `docs2/setup.md` (install), `docs2/migration.md` (1.x to 2.0) and the feature pages. If this skill and the docs disagree, the docs win; tell the user.

## 1. Detect the situation

Look at the project before changing anything:

| Look for | Tells you |
| :--- | :--- |
| `io.github.openflocon` in `libs.versions.toml` / `*.gradle.kts` | Flocon already used: read its version |
| `Flocon.initialize(` or `import io.github.openflocon.flocon.plugins.` | SDK 1.x |
| `startFlocon(` | SDK 2.x |
| `com.android.application` / `com.android.library` only | Android project |
| `kotlin("multiplatform")` with `iosMain` / `commonMain` | KMP project |
| `*.xcodeproj` / `Package.swift` with no Kotlin | Swift project |

Then pick one path:

- No Flocon → install: [references/android.md](references/android.md), [references/kmp.md](references/kmp.md) or [references/swift.md](references/swift.md).
- Flocon 1.x → migrate: [references/migrations/v1-to-v2.md](references/migrations/v1-to-v2.md).
- Already on the target version → only add the missing plugins from the install page.

Read only the file you need. A future migration is a new file in `references/migrations/`.

## 2. Rules for every path

- Work on a branch; do not touch unrelated uncommitted changes (use a separate worktree if the tree is dirty).
- Flocon must never ship active in release builds: every real module has a `-no-op` twin with the same API, used in release and in any non-debug variant.
- Install only the plugins the project really uses; find them by searching the code, do not guess.
- A plugin that is not installed does nothing at runtime, it does not fail the build. After the change, list every `Flocon.xxxPlugin` call and check each one is installed.
- Compile every variant you touched (debug, release, and others such as an internal distribution build). List the Gradle tasks first, their names depend on flavors.
- Ending with a green compile is not proof it works: say what was not run (app on a device, connection to Flocon Desktop).
- Never put project, company or app names in docs or skill files you write for Flocon itself.
