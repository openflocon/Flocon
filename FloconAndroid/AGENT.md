# Flocon Project Overview

Flocon is a modular, plugin-based framework built with **Kotlin Multiplatform (KMP)**. It provides a standardized way to integrate common cross-cutting concerns like networking, database access, datastores, and deep linking into applications.

## 🚀 Key Features

- **Modular Architecture**: Separate modules for different functionalities (network, database, etc.).
- **Plugin System**: Easily extensible with "no-op" variants for testing and modularity.
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

- `:flocon`: Core library providing the plugin registration and context management.
- `:database`: 
    - `:database:core`: Abstractions for database providers.
    - `:database:room` / `:database:room3`: Room-based implementations.
- `:network`:
    - `:network:core`: Core networking abstractions.
    - `:network:okhttp-interceptor` / `:network:ktor-interceptor`: Client-specific interceptors.
- `:grpc`:
    - `:grpc-interceptor`: Interceptors for gRPC calls.
- `:datastores`: Modules for Typed DataStore integration.
- `:deeplinks`: Abstractions and implementations for handling deep links.

## 🧩 Core Concepts

### Plugins
Flocon operates on a plugin-based architecture. Modules typically provide a "Core" or implementation module and a "No-Op" module. The No-Op modules allow the app to compile and run without the actual implementation, which is useful for specialized builds or testing.

### Interceptors
For networking and gRPC, Flocon uses an interceptor-based approach to hook into the communication pipeline of standard libraries (OkHttp, Ktor, gRPC).

## 📖 Development Guidelines

- **Multiplatform First**: Always consider KMP compatibility when adding new features or modules.
- **Modularity**: Keep modules focused and avoid circular dependencies.
- **Naming Conventions**: Follow the `io.github.openflocon.flocon` package naming structure.
- **Version Catalog**: All dependency versions must be managed in `gradle/libs.versions.toml`.

---
*Created by Antigravity AI to assist in project understanding.*
