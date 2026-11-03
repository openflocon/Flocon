# Install Flocon in a Swift (iOS) project

Status: partial. This repository contains the Kotlin Multiplatform SDK; it has no Swift package, so there is no verified Swift-only setup here.

Do not invent a Swift API. Steps:

1. Ask the user whether the project uses (a) a Kotlin Multiplatform framework that includes Flocon, or (b) a separate Swift SDK (`flocon-swift`).
2. (a) KMP framework: install and initialize on the Kotlin side as in [kmp.md](kmp.md), expose a small `initFlocon()` function from the shared module and call it from the Swift app entry point.
   - Kotlin default interface getters are not inherited by Swift classes: a Swift class implementing a Flocon interface (for example `FloconPreference`) must declare every property, such as `type`.
3. (b) Swift SDK: read that package's README for its installation (Swift Package Manager URL, initialization) and follow it. If it is not available locally, say so and stop rather than guessing.
4. Allow the connection to Flocon Desktop in debug only, and keep Flocon out of release builds.

Once a Swift SDK is documented in this repository, replace this file with the real steps.
