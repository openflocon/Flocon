---
title: gRPC Inspector
description: Inspect and debug gRPC streams and unary calls with Flocon.
---

# ⚡ gRPC Inspector

Flocon allows you to inspect **gRPC calls and Protocol Buffers messages** in real time, supporting Android applications built with `grpc-java` / `grpc-android`.

---

## Prerequisites

!!! warning "gRPC Version"
    Ensure your `grpc-android` / `grpc-java` version is at least **`1.70.0`**.

---

## Choosing the Right Artifact

Android projects frequently use either standard Protobuf (`protobuf-java` / `protobuf-kotlin`) or the lightweight mobile variant (`protobuf-javalite` / `protobuf-kotlin-lite`). Flocon provides dedicated artifacts for each to ensure clean JSON serialization:

=== "Protobuf Lite (Recommended for Android)"

    ```kotlin
    dependencies {
        implementation("com.google.protobuf:protobuf-kotlin-lite:$PROTOBUF_VERSION")
        
        debugImplementation("io.github.openflocon:flocon-grpc-interceptor-lite:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-grpc-interceptor-lite-no-op:LAST_VERSION")
    }
    ```

=== "Full Protobuf"

    ```kotlin
    dependencies {
        implementation("com.google.protobuf:protobuf-kotlin:$PROTOBUF_VERSION")
        
        debugImplementation("io.github.openflocon:flocon-grpc-interceptor:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-grpc-interceptor-no-op:LAST_VERSION")
    }
    ```

---

## Interceptor Configuration

Attach `FloconGrpcInterceptor` to your `ManagedChannelBuilder`:

```kotlin
val channel = ManagedChannelBuilder.forAddress(host, port)
    .intercept(FloconGrpcInterceptor())
    .build()
```