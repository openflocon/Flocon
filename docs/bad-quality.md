---
title: Bad Network Simulation
description: Simulate network throttling, jitter latency, packet loss, and random HTTP/Socket errors with Flocon.
---

# 📉 Bad Network Simulation (Network Conditioning)

Testing apps under ideal Wi-Fi conditions often masks critical bugs. Flocon provides a built-in **Network Conditioner / Bad Quality Simulator** that lets you simulate degraded network environments directly from the desktop companion — without touching your device's operating system settings.

---

## Capabilities

With Flocon's Bad Network Simulation, you can configure:

- **Latency & Jitter**: Add fixed delays or random latency ranges (e.g. `800ms – 3000ms`) to test slow 2G/3G connections and timeout thresholds.
- **Failure Probability**: Set a failure rate percentage (e.g. `25% of requests fail`) to test retry mechanisms and offline resilience.
- **Custom Error Types**: Choose between returning HTTP error codes (e.g. `500 Internal Server Error`, `503 Service Unavailable`) or hard transport exceptions (`IOException`, `SocketTimeoutException`).
- **Targeted Rules**: Apply network degradation globally or target specific API domains/paths.

---

## How It Works

1. Open **Flocon Desktop** and navigate to the **Network** tab.
2. Click the **Network Conditioner / Bad Quality** icon (or access it from the tools menu).
3. **Configure Latency**:
   - Enable **Simulate Latency**.
   - Set Minimum Latency (e.g. `500 ms`) and Maximum Latency (e.g. `2000 ms`).
4. **Configure Error Simulation**:
   - Enable **Simulate Errors**.
   - Set Failure Probability (e.g. `30%`).
   - Select Error Behavior: **HTTP Status Code** or **Throw Exception**.
5. Toggle **Enable Bad Quality**.

---

## Supported Clients

The Bad Network Simulation engine works automatically with any client using the Flocon network plugins:

=== "OkHttp (Android)"
    ```kotlin
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(FloconOkhttpInterceptor())
        .build()
    ```

=== "Ktor (Kotlin Multiplatform)"
    ```kotlin
    val httpClient = HttpClient {
        install(FloconKtorPlugin)
    }
    ```

=== "gRPC (Android)"
    ```kotlin
    val channel = ManagedChannelBuilder.forAddress(host, port)
        .intercept(FloconGrpcInterceptor())
        .build()
    ```

---

## Testing Real-World Scenarios

### 1. Elevator / Subway Simulation (Intermittent Drops)
Set failure rate to `40%` with `IOException` errors to verify that your app shows user-friendly retry banners instead of crashing.

### 2. Slow 3G / Poor Reception (High Latency)
Set latency between `2000ms` and `5000ms` to verify that loading spinners, shimmer skeletons, and cancel-on-back navigation behave properly.

### 3. Backend Outage (503 Service Unavailable)
Configure a 100% failure rate returning HTTP 503 to ensure your maintenance/outage screens display correctly.
