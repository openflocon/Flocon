# Flocon Project Overview

Flocon is a modular, plugin-based framework built with **Kotlin Multiplatform (KMP)**. It provides a standardized way to integrate common cross-cutting concerns like networking, database access, datastores, and deep linking into applications.

## 🚀 Key Features

- **Modular Architecture**: Separate modules for different functionalities (network, database, etc.).
- **Plugin System**: One module per feature; a single `flocon-no-op` turns Flocon off in release builds.
- **KMP Support**: Targets Android, iOS, JVM, and WasmJs.
- **Modern Tech Stack**: Uses Room 3, Ktor, OkHttp, gRPC, and Compose Multiplatform.

## 🛠 Technical Stack

- **Kotlin**: 2.1.0
- **Build System**: Gradle with Version Catalog (`libs.versions.toml`).
- **Dependency Injection**: Manual / Constructor injection (based on current exploration).
- **Asynchronous Programming**: Kotlin Coroutines & Flow.
- **Networking**: Ktor 3.x, OkHttp 4.x, gRPC 1.70.x.
- **Database**: Room 2.x & Room 3.0.0-alpha01.
- **UI**: Compose Multiplatform 1.9.0.

## 📂 Module Structure

Each feature lives in its own folder (`<feature>/<feature>`, Gradle path `:<feature>:<feature>`). Maven `artifactId`s are declared explicitly per module and do not depend on the path. `:flocon:flocon-no-op` turns Flocon off; `accessibility` and `qrscanner` also keep a `-no-op` sibling, because of their Android manifest entries.

- `:flocon:flocon`: Core library providing the plugin registration and context management.
- `:database`:
    - `:database:core:core`: Abstractions for database providers.
    - `:database:room:room` / `:database:room3:room3`: Room-based implementations.
- `:network`:
    - `:network:core:core`: Core networking abstractions.
    - `:network:okhttp-interceptor:okhttp-interceptor` / `:network:ktor-interceptor:ktor-interceptor`: Client-specific interceptors.
- `:grpc`:
    - `:grpc:grpc-interceptor:grpc-interceptor`: Interceptors for gRPC calls.
- `:datastores:datastores`: Modules for Typed DataStore integration.
- `:deeplinks:deeplinks`: Abstractions and implementations for handling deep links.

## 🧩 Core Concepts

### Plugins
Flocon operates on a plugin-based architecture. Apps use the real modules in every variant and add `flocon-no-op` in release: it turns `FloconRuntime` off (nothing is started, every `Flocon.xxxPlugin` is a no-op, interceptors pass calls through), and its R8 rule removes the Flocon code from minified builds.

### Interceptors
For networking and gRPC, Flocon uses an interceptor-based approach to hook into the communication pipeline of standard libraries (OkHttp, Ktor, gRPC).

## 📖 Development Guidelines

- **Multiplatform First**: Always consider KMP compatibility when adding new features or modules.
- **Modularity**: Keep modules focused and avoid circular dependencies.
- **Naming Conventions**: Follow the `io.github.openflocon.flocon` package naming structure.
- **Version Catalog**: All dependency versions must be managed in `gradle/libs.versions.toml`.

---
*Created by Antigravity AI to assist in project understanding.*
